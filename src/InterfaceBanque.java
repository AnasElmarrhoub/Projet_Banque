import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty; 
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InterfaceBanque extends Application {

    private Banque maBanque = new Banque("INPT Bank");
    private TableView<Client> table = new TableView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        
        maBanque.chargerClientsDepuisBD(); 
        maBanque.chargerComptesDepuisBD();        
        TableColumn<Client, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Client, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        TableColumn<Client, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        TableColumn<Client, String> colSolde = new TableColumn<>("Solde Total");
        colSolde.setCellValueFactory(donneesLigne -> {
            Client client = donneesLigne.getValue();
            double total = 0;
            for (Compte c : client.getMesComptes()) {
                total += c.getSolde();
            }
            
            return new SimpleStringProperty(total + " MAD");
        });
        table.getColumns().addAll(colId, colNom, colPrenom, colSolde);
        table.getItems().addAll(maBanque.getClients());
        Label label = new Label("Liste des Clients de la Banque");
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        VBox root = new VBox(10, label, table);
        root.setPadding(new Insets(15));
        Scene scene = new Scene(root, 650, 400); 
        primaryStage.setTitle("Ma Banque JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}