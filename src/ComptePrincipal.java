import java.time.LocalDateTime;
import java.util.List;

/**
 * A current account ({@code ComptePrincipal}) with an authorised overdraft limit.
 * Withdrawals are allowed up to {@code -overdraftLimit}.
 */
public class ComptePrincipal extends Compte {

    /** Maximum allowed negative balance (overdraft). Always a positive value. */
    private final double overdraftLimit;

    public ComptePrincipal(int numero, double solde, Client proprietaire,
                           List<Operation> historique, double overdraftLimit) {
        super(numero, solde, proprietaire, historique);
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Withdraws {@code montant} from the current account.
     * The withdrawal is refused if it would exceed the authorised overdraft.
     *
     * @param montant amount to withdraw (must be &gt; 0)
     * @throws BanqueException if the amount is invalid or the overdraft limit would be exceeded
     */
    @Override
    public void retirer(double montant) throws BanqueException {
        if (montant <= 0) throw new MontantNegatifException();

        double soldeApres = this.solde - montant;
        if (soldeApres < -this.overdraftLimit) {
            double maxRetrait = this.solde + this.overdraftLimit;
            throw new SoldeInsuffisantException(maxRetrait, montant);
        }

        this.solde = soldeApres;

        LocalDateTime now = LocalDateTime.now();
        Operation op = new Operation(compteurOperationId++, montant, Operation.TYPE_RETRAIT, now);
        this.historique.add(op);

        System.out.printf("✔ Retrait de %.2f MAD effectué sur le compte courant N°%d.%n", montant, this.numero);
        System.out.printf("  Solde actuel : %.2f MAD%n", this.solde);

        Journalisation.sauvegarderTicket("RETRAIT", this.numero, montant, this.solde);
    }

    public double getPlafond() {
        return overdraftLimit;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" [Type: Courant | Découvert autorisé: %.2f MAD]", overdraftLimit);
    }
}
