/**
 * Base checked exception for all domain-level banking errors.
 * Subclass this to create specific banking exceptions.
 */
public class BanqueException extends Exception {

    public BanqueException(String message) {
        super(message);
    }
}
