import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for logging bank events to disk.
 * <ul>
 *   <li>{@link #ajouterJournal(String)} — appends a timestamped entry to the main journal file.</li>
 *   <li>{@link #sauvegarderTicket(String, int, double, double)} — writes a formatted receipt to a per-account ticket file.</li>
 * </ul>
 */
public class Journalisation {

    private static final String JOURNAL_FILE = "journal_banque.txt";
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /** Private constructor — this is a utility class and should not be instantiated. */
    private Journalisation() {}

    /**
     * Appends a timestamped message to the bank journal.
     *
     * @param message the event description to log
     */
    public static void ajouterJournal(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(JOURNAL_FILE, true))) {
            out.println(LocalDateTime.now().format(DATE_FMT) + " : " + message);
        } catch (IOException e) {
            System.err.println("Erreur journal : " + e.getMessage());
        }
    }

    /**
     * Writes a formatted receipt file for the given operation.
     * The file is named {@code Ticket_<typeOp>_<numCompte>.txt}.
     *
     * @param typeOp      operation type (e.g. "VERSEMENT", "RETRAIT")
     * @param numCompte   account number
     * @param montant     operation amount in MAD
     * @param soldeApres  balance after the operation in MAD
     */
    public static void sauvegarderTicket(String typeOp, int numCompte, double montant, double soldeApres) {
        String nomFichier = "Ticket_" + typeOp + "_" + numCompte + ".txt";
        String now = LocalDateTime.now().format(DATE_FMT);

        try (PrintWriter ticket = new PrintWriter(new FileWriter(nomFichier))) {
            ticket.println("********************************");
            ticket.println("          BANQUE INPT           ");
            ticket.println("********************************");
            ticket.println("Date      : " + now);
            ticket.println("Opération : " + typeOp);
            ticket.println("Compte N° : " + numCompte);
            ticket.println("--------------------------------");
            ticket.printf( "Montant   : %.2f MAD%n", montant);
            ticket.printf( "Solde     : %.2f MAD%n", soldeApres);
            ticket.println("********************************");
            ticket.println("    MERCI DE VOTRE CONFIANCE    ");
            ticket.println("********************************");
            System.out.println(">> Ticket imprimé : " + nomFichier);
        } catch (IOException e) {
            System.err.println("Erreur impression ticket : " + e.getMessage());
        }
    }
}