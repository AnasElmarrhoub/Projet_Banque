import java.sql.*;
import java.util.ArrayList;

public class Banque {
	private String nomBanque;
	private ArrayList<Client> listeClients = new ArrayList<>();
	private ArrayList<Compte> listeComptes = new ArrayList<>();

	public Banque(String nomBanque) {
		this.nomBanque = nomBanque;
	}

	

	public void ajouterClient(Client cl) {
		listeClients.add(cl); // Ajout mémoire

		// Ajout Base de données
		try {
			Connection conn = ConnexionBD.getConnection();
			String sql = "INSERT INTO clients (id, nom, prenom, rue, ville, code_postal) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, cl.getId());
			pstmt.setString(2, cl.getNom());
			pstmt.setString(3, cl.getPrenom());
			pstmt.setString(4, cl.getAdresse().getNomRue());
			pstmt.setString(5, cl.getAdresse().getVille());
			pstmt.setString(6, cl.getAdresse().getCodePostal());

			pstmt.executeUpdate();
			System.out.println("Client " + cl.getNom() + " enregistré en BDD.");

		} catch (SQLException e) {
			System.out.println("Erreur ajout Client BDD: " + e.getMessage());
		}
	}

	public Client chercherClient(int id) {
		for (Client c : listeClients) {
			if (c.getId() == id)
				return c;
		}
		return null;
	}

	

	public void ajouterCompte(Compte c) {
		listeComptes.add(c); // Ajout mémoire

		// Ajout Base de données
		try {
			Connection conn = ConnexionBD.getConnection();
			String sql = "INSERT INTO comptes (numero, solde, type_compte, plafond_decouvert, taux_interet, id_client) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, c.getNumero());
			pstmt.setDouble(2, c.getSolde());

			if (c instanceof ComptePrincipal) {
				pstmt.setString(3, "COURANT");
				// On utilise le getter qu'on a ajouté
				pstmt.setDouble(4, ((ComptePrincipal) c).getPlafond());
				pstmt.setNull(5, java.sql.Types.DOUBLE);
			} else {
				pstmt.setString(3, "EPARGNE");
				pstmt.setNull(4, java.sql.Types.DOUBLE);
				pstmt.setDouble(5, ((CompteEpargne) c).getTauxInteret());
			}

			pstmt.setInt(6, c.getProprietaire().getId());

			pstmt.executeUpdate();
			System.out.println("Compte N°" + c.getNumero() + " enregistré en BDD.");

		} catch (SQLException e) {
			System.out.println("Erreur ajout Compte BDD: " + e.getMessage());
		}
	}

	public Compte chercherCompte(int numero) {
		for (Compte c : listeComptes) {
			if (c.getNumero() == numero)
				return c;
		}
		return null;
	}

	// --- OPÉRATIONS ---

	public void effectuerVirement(int numSource, int numDest, double montant) {
		try {
			Compte source = chercherCompte(numSource);
			Compte dest = chercherCompte(numDest);

			if (source == null || dest == null)
				throw new BanqueException("Compte introuvable");

			// Opération mémoire
			source.retirer(montant);
			dest.verser(montant);

			// Mise à jour BDD
			updateSoldeBDD(source);
			updateSoldeBDD(dest);
			
			enregistrerOperationBD(numSource, "RETRAIT", montant);
	        // On enregistre le versement chez le destinataire
	        enregistrerOperationBD(numDest, "VERSEMENT", montant);

			System.out.println("Virement effectué avec succès !");
			Journalisation.ajouterJournal("Virement de " + montant + " : " + numSource + " -> " + numDest);

		} catch (Exception e) {
			System.out.println("Erreur virement : " + e.getMessage());
		}
	}

	private void updateSoldeBDD(Compte c) {
		try {
			Connection conn = ConnexionBD.getConnection();
			String sql = "UPDATE comptes SET solde = ? WHERE numero = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setDouble(1, c.getSolde());
			pstmt.setInt(2, c.getNumero());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Erreur update solde : " + e.getMessage());
		}
	}
	

	public void chargerClientsDepuisBD() {
		String sql = "SELECT * FROM Clients"; 

		try (java.sql.Connection conn = ConnexionBD.getConnection();
				java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
				java.sql.ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				Adresse adresse = new Adresse(rs.getString("rue"), rs.getString("ville"), rs.getString("code_postal"));
				Client client = new Client(rs.getInt("id"), rs.getString("nom"), rs.getString("prenom"), adresse);
				if (chercherClient(client.getId()) == null) {
					listeClients.add(client);
				}
			}
			System.out.println("--> Chargement terminé : " + listeClients.size() + " clients récupérés.");
		} catch (java.sql.SQLException e) {
			System.out.println("Erreur chargement clients : " + e.getMessage());
		}
	}

	public void chargerComptesDepuisBD() {
		String sql = "SELECT * FROM comptes";
		try (java.sql.Connection conn = ConnexionBD.getConnection();
				java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
				java.sql.ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				int idClient = rs.getInt("id_client");
				Client proprietaire = chercherClient(idClient);
				if (proprietaire != null) {
					Compte compte = null;
					String type = rs.getString("type_compte");
					if ("COURANT".equals(type)) {
						compte = new ComptePrincipal(rs.getInt("numero"), rs.getDouble("solde"), proprietaire,
								new ArrayList<>(), rs.getDouble("plafond_decouvert"));
					} else if ("EPARGNE".equals(type)) {
						compte = new CompteEpargne(rs.getInt("numero"), rs.getDouble("solde"), proprietaire,
								new ArrayList<>());
					}

					if (compte != null && chercherCompte(compte.getNumero()) == null) {
						listeComptes.add(compte);
						proprietaire.ajouterCompte(compte);
					}
				}
			}
			System.out.println("--> Chargement terminé : " + listeComptes.size() + " comptes récupérés.");
		} catch (java.sql.SQLException e) {
			System.out.println("Erreur chargement comptes : " + e.getMessage());
		}
	}
	
	private void enregistrerOperationBD(int numCompte, String type, double montant) {
	    String sql = "INSERT INTO Operations (montant, type_op, date_op, num_compte) VALUES (?, ?, NOW(), ?)";
	    try (java.sql.Connection conn = ConnexionBD.getConnection();
	         java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setDouble(1, montant);
	        pstmt.setString(2, type);
	        pstmt.setInt(3, numCompte);
	        pstmt.executeUpdate();
	        System.out.println("  → Opération (" + type + ") enregistrée en BDD pour le compte " + numCompte);
	    } catch (java.sql.SQLException e) {
	        System.out.println("  ✗ Erreur sauvegarde opération : " + e.getMessage());
	    }
	}
	public ArrayList<Client> getClients() {
		return this.listeClients;
	}

	@Override
	public String toString() {
		return "Banque " + nomBanque + " (" + listeClients.size() + " clients)";
	}
}