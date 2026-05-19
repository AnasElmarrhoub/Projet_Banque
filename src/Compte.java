import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all bank account types.
 * Provides common fields and the {@code verser} (deposit) operation.
 * Subclasses must implement {@link #retirer(double)}.
 */
public abstract class Compte {

    protected int numero;
    protected double solde;
    protected Client proprietaire;
    protected List<Operation> historique;
    protected static int compteurOperationId = 0;

    protected Compte(int numero, double solde, Client proprietaire, List<Operation> historique) {
        this.numero       = numero;
        this.solde        = solde;
        this.proprietaire = proprietaire;
        this.historique   = (historique != null) ? historique : new ArrayList<>();
    }

    /**
     * Deposits {@code montant} into this account.
     *
     * @param montant amount to deposit (must be &gt; 0)
     * @throws BanqueException if {@code montant} is negative
     */
    public void verser(double montant) throws BanqueException {
        if (montant <= 0) throw new MontantNegatifException();
        this.solde += montant;
        LocalDateTime now = LocalDateTime.now();
        Operation operation = new Operation(compteurOperationId++, montant, Operation.TYPE_VERSEMENT, now);
        historique.add(operation);
        System.out.printf("Versement de %.2f MAD effectué sur le compte %d le : %s%n",
                montant, this.numero, now);
        System.out.printf("Votre solde actuel est : %.2f MAD%n", solde);
        Journalisation.sauvegarderTicket("VERSEMENT", this.numero, montant, this.solde);
    }

    /**
     * Withdraws {@code montant} from this account.
     *
     * @param montant amount to withdraw (must be &gt; 0)
     * @throws BanqueException if the amount is invalid or the balance is insufficient
     */
    public abstract void retirer(double montant) throws BanqueException;

    // --- Getters ---

    public int getNumero() {
        return numero;
    }

    public double getSolde() {
        return solde;
    }

    public Client getProprietaire() {
        return proprietaire;
    }

    public List<Operation> getHistorique() {
        return historique;
    }

    @Override
    public String toString() {
        return String.format("Compte N°%d | Solde : %.2f MAD | Propriétaire : %s",
                numero, solde, proprietaire.getNom());
    }
}
