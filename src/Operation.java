import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single bank operation (deposit or withdrawal).
 */
public class Operation {

    public static final String TYPE_VERSEMENT = "VERSEMENT";
    public static final String TYPE_RETRAIT   = "RETRAIT";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final int           numero;
    private final double        montant;
    private final String        typeOperation;
    private final LocalDateTime dateOperation;

    public Operation(int numero, double montant, String typeOperation, LocalDateTime dateOperation) {
        this.numero        = numero;
        this.montant       = montant;
        this.typeOperation = typeOperation;
        this.dateOperation = dateOperation;
    }

    // --- Getters ---

    public int getNumero() {
        return numero;
    }

    public double getMontant() {
        return montant;
    }

    public String getTypeOperation() {
        return typeOperation;
    }

    public LocalDateTime getDateOperation() {
        return dateOperation;
    }

    @Override
    public String toString() {
        return "[" + dateOperation.format(FORMATTER) + "] " + typeOperation + " : " + montant + " MAD";
    }
}
