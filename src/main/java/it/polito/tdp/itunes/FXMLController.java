/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.itunes;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.itunes.model.Adiacenza;
import it.polito.tdp.itunes.model.Genre;
import it.polito.tdp.itunes.model.Model;
import it.polito.tdp.itunes.model.Track;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaLista"
    private Button btnCreaLista; // Value injected by FXMLLoader

    @FXML // fx:id="btnMassimo"
    private Button btnMassimo; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCanzone"
    private ComboBox<Track> cmbCanzone; // Value injected by FXMLLoader

    @FXML // fx:id="cmbGenere"
    private ComboBox<Genre> cmbGenere; // Value injected by FXMLLoader

    @FXML // fx:id="txtMemoria"
    private TextField txtMemoria; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void btnCreaLista(ActionEvent event) {

    	//Questo è il bottone che mi richiama il metodo ricorsivo. Lo gestisco.
    	txtResult.clear();
    	
    	//Faccio il controllo degli errori.
    	Track c = this.cmbCanzone.getValue();
    	
    	//Controllo che l'utente abbia inserito una canzone preferita.
    	if(c == null) {
    		txtResult.appendText("Seleziona una canzone!");
    		return;
    	}
    	//Controllo che la memoria passata sia un valore intero.
    	int m;
    	try {
    		m = Integer.parseInt(txtMemoria.getText());
    	} catch(NumberFormatException e) {
    		txtResult.appendText("Inserire un valore numerico per la memoria");
    		return;
    	}
    	//Controllo che il grafo sia stato creato.
    	if(!this.model.grafoCreato()) {
    		txtResult.appendText("Creare il grafo!");
    		return;
    	}
    	
    	//Ora posso invocare il metodo
    	txtResult.appendText("Lista canzoni migliori:\n");
    	for(Track t : this.model.cercaLista(c, m)) {
    		txtResult.appendText(t.toString()+ "\n");
    	}
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {

    	txtResult.clear();
    	
    	Genre g = this.cmbGenere.getValue();
    	
    	if(g == null) {
    		txtResult.appendText("Seleziona un genere");
    		return;
    	}
    	else {
    		this.model.creaGrafo(g);
    		
    		txtResult.appendText("Grafo creato!\n");
    		txtResult.appendText("Numero Vertici "+ this.model.nVertici() + "\n");
    		txtResult.appendText("Numero Vertici "+ this.model.nArchi() + "\n");
    	
    		//Dopo aver creato il grafo, popolo la tendina di canzoni.
        	this.cmbCanzone.getItems().clear();		//Ogni volta che clicco su creaGrafo() devo ripulire dalla lista precedentemente creata.
    		this.cmbCanzone.getItems().addAll(this.model.getVertici());
        
    	}
    }

    @FXML
    void doDeltaMassimo(ActionEvent event) {
    	
    	txtResult.clear();
    	
    	if(this.model.grafoCreato() == false) {
    		txtResult.appendText("Creare il grafo.");
    		return;	//Ricordarsi di fare return.
    	}
    	else {
    		for (Adiacenza a: this.model.getDeltaMassimo()) {
    			txtResult.appendText(a.toString()+ "\n");
    		}
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaLista != null : "fx:id=\"btnCreaLista\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnMassimo != null : "fx:id=\"btnMassimo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCanzone != null : "fx:id=\"cmbCanzone\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbGenere != null : "fx:id=\"cmbGenere\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtMemoria != null : "fx:id=\"txtMemoria\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	 //La tendina va riempita all'inizio, quando l'utente deve già selezionare il genere.
    	 //Il posto giusto per riempire le tendine con dei dati che ci arrivano dal modello è qui, perché ho già il modello.
    	
    	this.cmbGenere.getItems().addAll(this.model.getGeneri());
    	//Ricordarsi per l'esame di provare spesso il programma. Già dopo questa operazione conviene provare che funzioni. Effettivamente funziona.
    	
   }

}
