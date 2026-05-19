
public class SoldeInsuffisantException extends BanqueException {
	public SoldeInsuffisantException(double solde, double montantDemande) {
        super("Solde insuffisant ! Vous avez " + solde + " mais vous demandez " + montantDemande);
    }

}
