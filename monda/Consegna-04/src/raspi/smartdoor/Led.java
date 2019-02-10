package smartdoor;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import java.io.IOException;

/**
 * Led installato sulla breadboard.
 */
public class Led implements Light {
    private GpioPinDigitalOutput pin;

    /**
     * Costruttore della classe Led.
     * 
     * @param pinNum
     *            Numero del pin sul quale e' installato il pin.
     */
    public Led(int pinNum) {
        try {
            GpioController gpio = GpioFactory.getInstance();
            pin = gpio.provisionDigitalOutputPin(Config.pinMap[pinNum]);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void switchOn() throws IOException {
        pin.high();
    }

    @Override
    public void switchOff() throws IOException {
        pin.low();
    }

    @Override
    public void pulse(int millisecond) throws InterruptedException, IOException {
        switchOn();
        Thread.sleep(millisecond);
        switchOff();
    }

}
