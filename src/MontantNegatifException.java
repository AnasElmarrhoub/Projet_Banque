/**
 * Thrown when a requested operation amount is zero or negative.
 */
public class MontantNegatifException extends BanqueException {

    public MontantNegatifException() {
        super("Erreur : le montant doit être strictement positif.");
    }
}
