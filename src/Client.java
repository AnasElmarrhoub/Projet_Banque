

import java.util.ArrayList;
import java.util.List;

public class Client {
	private int id;
	private String nom;
	private String prenom;
	private Adresse adresse; 
	private List<Compte> mesComptes;
	
	public Client(int id, String nom, String prenom, Adresse adresse) {
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.adresse = adresse;
		this.mesComptes = new ArrayList<>();
	}
	public void ajouterCompte(Compte c) {
        this.mesComptes.add(c);
    }
	public int getId() {
		return id;
	}
	public String getNom() {
		return nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public Adresse getAdresse() {
		return adresse;
	}
	public List<Compte> getMesComptes() {
		return mesComptes;
	}
	@Override
    public String toString() {
        return "Client " + id + " : " + prenom + " " + nom + " (" + mesComptes.size() + " comptes)";
    }
	
}
