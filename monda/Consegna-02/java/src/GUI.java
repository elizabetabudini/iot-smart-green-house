import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import gnu.io.CommPortIdentifier;

public class GUI extends JFrame {

    //private variables
    private static final long serialVersionUID = 1L;
    private static final int WINDOW_X = 800;
    private static final int WINDOW_Y = 400;
    private final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    private final Label lblPort = new Label("Port:");
    private final Label lblBaud = new Label("Baud:");
    private final JLabel lblDistance = new JLabel("Distance:");
    private final JComboBox<String> cbPort = new JComboBox<String>();
    private final TextField txtBaud = new TextField(10);
    private final TextArea txtLog = new TextArea(16, 50);
    private final JPanel panConnect = new JPanel();
    private final JPanel panLog = new JPanel();
    private final JPanel panFunctions = new JPanel();
    private final JPanel panel = new JPanel(new GridBagLayout());
    private final JButton btnConnect = new JButton("Connect");
    private final JButton btnOpen = new JButton("Open");
    private final JButton btnClose = new JButton("Stop");
    private final JButton btnClear = new JButton("Clear");
    private static SerialMonitor monitor = new SerialMonitor();

    //Costructor that initialize GUI
    public GUI() throws IOException {
        initialize();

    }

    //Initialize GUI
    private void initialize() throws IOException {
        setTitle("smart_garage by Tentoni,Mondaini,Pracucci");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //Can't be resizable
        setResizable(false);
        
        
        // Set location of the window in the "CENTER"
        setSize(WINDOW_X, WINDOW_Y);
        final int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        final int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);

        //Set textArea not focusable
        txtLog.setFocusable(false);

        //Create the layout
        add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout());
        panel.add(panConnect, BorderLayout.NORTH);
        panConnect.add(lblPort);
        panConnect.add(cbPort);
        panConnect.add(lblBaud);
        panConnect.add(txtBaud);
        panConnect.add(btnConnect);
        panel.add(panLog, FlowLayout.CENTER);
        panLog.add(lblDistance);
        panLog.add(txtLog);
        panel.add(panFunctions, BorderLayout.SOUTH);
        panFunctions.add(btnOpen);
        panFunctions.add(btnClose);
        panFunctions.add(btnClear);
        
        panLog.setFocusable(false);
        btnOpen.setEnabled(false);
        btnClose.setEnabled(false);

        //Use the CommPortIdentifier and put the result to a comboBox
        Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            cbPort.addItem(currPortId.getName());
        }

        //When the button connect is pressed
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                //Write in the textarea
                txtLog.append("Start monitoring serial port "+cbPort.getSelectedItem().toString()+" at boud rate: "+txtBaud.getText()+"\n");
                //Start the SerialMonitor
                monitor.start(cbPort.getSelectedItem().toString(), Integer.parseInt(txtBaud.getText()), lblDistance,txtLog);
                btnOpen.setEnabled(true);
                btnClose.setEnabled(true);
                btnConnect.setEnabled(false);
                cbPort.setEnabled(false);
            }
        });
        
        //When the button Open is pressed
        btnOpen.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                try {
                    //Send open to Arduino
                    monitor.Send("open");
                } catch (InterruptedException | IOException e1) {
                    System.out.println("Error");
                    e1.printStackTrace();
                }
            }
        });
        
        //When the button Close is pressed
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                try {
                    //Send stop to Arduino
                    monitor.Send("stop");
                } catch (InterruptedException | IOException e1) {
                    System.out.println("Error");
                    e1.printStackTrace();
                }
            }
        });
        
        //When the button Clear is pressed
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                //Clear the textArea
                txtLog.setText("");
            }
        });
    }

    //Main method
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //Create a GUI and put it visible
                    final GUI window = new GUI();
                    window.setVisible(true);
                } catch (Exception e) {
                    System.out.println("Error Launch");
                }
            }
        });
    }
}