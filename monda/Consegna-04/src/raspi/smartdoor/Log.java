package smartdoor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

public class Log {

    private static class LogLazyTs {
        private static final Log SINGLETON = new Log();
    }

    public static Log getLog() {
        return LogLazyTs.SINGLETON;
    }

    /**
     * Metodo per salvare sul file log.txt il messaggio dato in input.
     * 
     * @param message
     *            Messaggio da loggare.
     */
    public void log(String message) {
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/var/www/html/IoT/log.txt"), true));
            bw.append("[L] " + Instant.now() + ": " + message + "\n");
            bw.close();
            System.out.println(message);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    /**
     * Metodo per salvare sul file log.txt il messaggio dato in input e reset.
     * 
     * @param message
     *            Messaggio da loggare.
     */
    public void logReset(String message) {
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/var/www/html/IoT/log.txt")));
            bw.write(message);
            bw.close();
            System.out.println(message);
        } catch (IOException exception) {

            exception.printStackTrace();
        }
    }

}
