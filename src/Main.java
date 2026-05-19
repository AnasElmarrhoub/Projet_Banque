import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Entry point for the INPT Bank management system demo.
 * Creates clients and accounts, performs deposits, withdrawals and transfers,
 * then demonstrates persistence by reloading all data from the database.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   SYSTÈME DE GESTION BANCAIRE INPT");
        System.out.println("========================================\n");

        // Clean database state for a fresh demo run
        nettoyerBD();

        Banque maBanque = new Banque("INPT Bank");

        try {
            // --- 1. CRÉATION DES CLIENTS ---
            System.out.println("--- 1. CRÉATION DES CLIENTS ---\n");
            Adresse ad1 = new Adresse("Bd Mohammed V", "Rabat", "10000");
            Client client1 = new Client(1, "Moloudi", "Hicham", ad1);

            Adresse ad2 = new Adresse("Route d'El Jadida", "Casablanca", "20000");
            Client client2 = new Client(2, "Ghannam", "Othman", ad2);

            Adresse ad3 = new Adresse("Av Hassan II", "Fès", "30000");
            Client client3 = new Client(3, "Elmarrhoub", "Anas", ad3);

            maBanque.ajouterClient(client1);
            maBanque.ajouterClient(client2);
            maBanque.ajouterClient(client3);

            // --- 2. CRÉATION DES COMPTES ---
            System.out.println("\n--- 2. CRÉATION DES COMPTES ---\n");
            ComptePrincipal compte1 = new ComptePrincipal(100, 5000, client1, new ArrayList<>(), 1000);
            ComptePrincipal compte2 = new ComptePrincipal(101, 3000, client3, new ArrayList<>(), 500);

            CompteEpargne compte3 = new CompteEpargne(200, 10000, client2, new ArrayList<>());
            CompteEpargne compte4 = new CompteEpargne(201, 8000, client1, new ArrayList<>());

            maBanque.ajouterCompte(compte1);
            maBanque.ajouterCompte(compte2);
            maBanque.ajouterCompte(compte3);
            maBanque.ajouterCompte(compte4);

            // --- 3. VERSEMENTS ---
            System.out.println("\n--- 3. VERSEMENTS ---\n");
            compte1.verser(2000);
            System.out.println();
            compte3.verser(1500);
            System.out.println();

            // --- 4. RETRAITS ---
            System.out.println("--- 4. RETRAITS ---\n");
            System.out.println("Retrait de 1 000 MAD du compte courant 100 :");
            compte1.retirer(1000);
            System.out.println();

            System.out.println("Retrait de 2 000 MAD du compte épargne 200 (intérêts appliqués automatiquement) :");
            compte3.retirer(2000);
            System.out.println();

            // --- 5. VIREMENTS ---
            System.out.println("--- 5. VIREMENTS ---\n");
            System.out.printf("Virement 1 : 1 500 MAD  |  Compte 100 → Compte 200%n");
            System.out.printf("  Avant — Compte 100 : %.2f MAD  |  Compte 200 : %.2f MAD%n",
                    compte1.getSolde(), compte3.getSolde());
            maBanque.effectuerVirement(100, 200, 1500);
            System.out.printf("  Après — Compte 100 : %.2f MAD  |  Compte 200 : %.2f MAD%n%n",
                    compte1.getSolde(), compte3.getSolde());

            System.out.println("Virement 2 : 1 000 MAD  |  Compte 200 → Compte 201");
            maBanque.effectuerVirement(200, 201, 1000);
            System.out.println();

            // --- 6. RECHARGEMENT DEPUIS LA BASE DE DONNÉES ---
            System.out.println("--- 6. RECHARGEMENT DEPUIS LA BASE DE DONNÉES ---\n");
            Banque nouvelleBanque = new Banque("INPT Bank - Session 2");
            System.out.println("Avant chargement : " + nouvelleBanque.getClients().size() + " clients en mémoire");
            nouvelleBanque.chargerClientsDepuisBD();
            nouvelleBanque.chargerComptesDepuisBD();
            System.out.println("Après chargement : " + nouvelleBanque.getClients().size() + " clients en mémoire");
            System.out.println("Détail des comptes chargés :");
            for (Client c : nouvelleBanque.getClients()) {
                System.out.println("  " + c);
                for (Compte cpt : c.getMesComptes()) {
                    System.out.println("    → " + cpt);
                }
            }
            System.out.println();

        } catch (BanqueException e) {
            System.err.println("ERREUR BANCAIRE : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERREUR SYSTÈME  : " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private static void nettoyerBD() {
        try {
            Connection conn = ConnexionBD.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM operations");
            stmt.executeUpdate("DELETE FROM comptes");
            stmt.executeUpdate("DELETE FROM clients");
            System.out.println("Base de données nettoyée pour la démonstration.\n");
        } catch (Exception e) {
            System.out.println("Info : base de données déjà vide ou inaccessible, on continue...\n");
        }
    }
}