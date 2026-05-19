import java.time.LocalDateTime;
import java.util.List;

/**
 * A savings account ({@code CompteEpargne}) that applies interest on each withdrawal.
 * The annual interest rate is fixed at {@value #TAUX_INTERET_ANNUEL} (20 %).
 */
public class CompteEpargne extends Compte {

    private static final double TAUX_INTERET_ANNUEL = 0.20;

    public CompteEpargne(int numero, double solde, Client proprietaire, List<Operation> historique) {
        super(numero, solde, proprietaire, historique);
    }

    /**
     * Withdraws {@code montant} from the savings account.
     * Before the withdrawal, interest for the current period is credited to the balance.
     *
     * @param montant amount to withdraw (must be &gt; 0)
     * @throws BanqueException if the amount is invalid or the balance (after interest) is insufficient
     */
    @Override
    public void retirer(double montant) throws BanqueException {
        // Validate BEFORE modifying the balance
        if (montant <= 0) throw new MontantNegatifException();

        double gainPotentiel = this.solde * TAUX_INTERET_ANNUEL;
        double soldeAvecInterets = this.solde + gainPotentiel;

        if (soldeAvecInterets < montant) {
            throw new SoldeInsuffisantException(soldeAvecInterets, montant);
        }

        // Credit interest then debit
        this.solde = soldeAvecInterets - montant;

        LocalDateTime now = LocalDateTime.now();
        Operation op = new Operation(compteurOperationId++, montant, Operation.TYPE_RETRAIT, now);
        this.historique.add(op);

        System.out.printf("✔ RETRAIT EFFECTUÉ%n");
        System.out.printf("  Intérêts crédités  : +%.2f MAD%n", gainPotentiel);
        System.out.printf("  Montant retiré     :  %.2f MAD%n", montant);
        System.out.printf("  Nouveau solde      :  %.2f MAD%n", this.solde);
        System.out.printf("  Gain annuel futur  :  %.2f MAD%n", this.solde * TAUX_INTERET_ANNUEL);

        Journalisation.sauvegarderTicket("RETRAIT", this.numero, montant, this.solde);
    }

    public double getTauxInteret() {
        return TAUX_INTERET_ANNUEL;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" [Type: Épargne | Taux: %.0f%%]", TAUX_INTERET_ANNUEL * 100);
    }
}
