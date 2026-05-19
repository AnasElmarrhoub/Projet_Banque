/**
 * Thrown when an operation references an account number that does not exist.
 */
public class CompteInexistantException extends BanqueException {

    public CompteInexistantException(int numeroCompte) {
        super("Le compte numéro " + numeroCompte + " n'existe pas.");
    }
}