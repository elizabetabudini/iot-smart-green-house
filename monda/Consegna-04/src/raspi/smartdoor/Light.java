package smartdoor;
import java.io.IOException;

/**
 * Elemento luminoso che si puo' accendere e spegnere.
 */
public interface Light {
    /**
     * Accendi l'elemento luminoso.
     * 
     * @throws IOException
     *             Problema generico riscontrato durante l'accesione.
     */
    void switchOn() throws IOException;

    /**
     * Spegni l'elemento luminoso.
     * 
     * @throws IOException
     *             Problema generico riscontrato durante lo spegnimento.
     */
    void switchOff() throws IOException;
    
    /**
     * Effettua un impulso luminoso.
     * 
     * @param millisecond
     *             Durata dell'impulso.
     * @throws IOException
     *             Problema generico riscontrato durante l'impulso.
     * @throws InterruptedException 
     *             Problema generato durante la wait.
     */
    void pulse(int millisecond) throws IOException, InterruptedException;
}
