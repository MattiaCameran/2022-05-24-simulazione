package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	private ItunesDAO dao;
	private Graph<Track, DefaultWeightedEdge> grafo;
	
	private Map<Integer, Track> idMap;
	
	private List<Track> listaMigliore;	//La size di questa lista sarà il parametro per stabilire se è la migliore o no.
	
	public List<Track> cercaLista(Track c, int m){		//m è la memoria totale del lettore MP3
		
		//La prima cosa da fare è capire quali sono le canzoni su cui ragionare, ovvero quelle che hanno lo stesso formato di c.
		//Creo qui l'elenco di canzoni uguale alla componente connessa di c.
		List<Track> canzoniValide = new ArrayList<Track>();
		
		ConnectivityInspector<Track, DefaultWeightedEdge> ci = new ConnectivityInspector<>(this.grafo);		//Il connectivityInspector è una classe che serve ad ottenere la componente connessa di un vertice.
		
		canzoniValide.addAll(ci.connectedSetOf(c));	//Costruisco una lista a partire dal set di vertici connessi a c restituiti da connectedSetOf.
		
		//Ricorda: componente connessa = tutti i vertici raggiungibili da c.
		
		List<Track> parziale = new ArrayList<Track>();	//Creo la lista parziale e le aggiungo subito la canzone preferita c.
		listaMigliore = new ArrayList<>();				//Inizializzare la lista.
		parziale.add(c);
		
		cerca(parziale, m, canzoniValide);
		
		return listaMigliore;
		
	}
	
	
	private void cerca(List<Track> parziale, int m, List<Track> canzoniValide) {
		
		//Una per volta, provo a prendere una delle mie canzoni valide, metterle in parziale e provare ad andare avanti.
		
		//SOLUZIONE MIGLIORE.
		if(listaMigliore.size() < parziale.size()) {
			//listaMigliore.clear();
			//listaMigliore.addAll(new ArrayList<Track>(parziale));
			listaMigliore = new ArrayList<Track>(parziale);	//Andava bene come prima ma posso anche sovrascrivere semplicemente così.
		
			//RICORDA: NON SCRIVERE RETURN. Per come è strutturato il codice io sto facendo un controllo ogni volta che aggiungo la canzone quindi non è questa parte di codice che deve essere la terminale.
			//La terminazione sarà data dall'if scritto sotto. Io finirò se ho finito la lista di canzoni valide o se ho riempito la memoria.
		
		}
		
		
		//CASO NORMALE: SOLUZIONE RICORSIVA
		for(Track t: canzoniValide) {
			
			//Devo filtrare. Faccio la ricorsione solo se porta a risultati utili.
			
			//La prima cosa da controllare è che t non ci sia già. Evidente sul fatto che tra le canzoniValide ci sia la canzone preferita già presente nella lista.
			if(!parziale.contains(t) && (sommaMemoria(parziale) + t.getBytes()) <= m) {	//Ricordarsi di guardare se c'è HashCode o equals, altrimenti non funziona la contains
																					//Io posso aggiungere inoltre solo se la memoria della lista e la canzone che sto aggiungendo non supera quella del MP3
				
			parziale.add(t);
			cerca(parziale, m, canzoniValide);
			parziale.remove(parziale.size()-1);	//Sempre meglio togliere l'ultimo inserito tramite accesso posizionale.
			}
		}
		
	}
	//Metodo per calcolare la memoria in bytes della lista di canzoni.
	private int sommaMemoria (List<Track> canzoni) {
		int somma = 0;
		for(Track t: canzoni) {
			somma += t.getBytes();
		}
		return somma;
	}

	public Model() {
		dao = new ItunesDAO();
		idMap = new HashMap<Integer, Track>();
		
		//Recupero tutte le canzoni del DB e riempio la mappa. Cambio il metodo nel DAO.
		this.dao.getAllTracks(idMap);
	}
	//Metodo per riempire la tendina nel controllore. Lo passerò al controller.
	public List<Genre> getGeneri(){
		return dao.getAllGenres();
	}
	
	//Metodo di creazione del grafo.
	public void creaGrafo (Genre g) {
		
		//Creo il grafo
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//Aggiungo i vertici
		
		//Creo la identity Map dove metto tutte le possibili canzoni e da questa prendo i sottinsiemi per impostare i vertici.
		
		//I vertici sono tutte le canzoni di genere g. Vado sul DAO a crearmi un metodo che dato un genere mi ritorni tutte le canzoni.
		//Ho creato il metodo per recuperare i vertici.
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(g, idMap));	//Aggiunti i vertici.
		
		//A questo punto conviene provare il codice. Creo la classe TestModel nel package Model e testo.
		
		//System.out.println("Grafo creato!");
		//System.out.println(String.format("Numero vertici %d", this.grafo.vertexSet().size()));
		//Funziona
		
		//Aggiungo gli archi
		
		//Il testo mi dice che le canzoni sono collegate tra loro se condividono lo stesso formato di file (MediaType).
		//Se dobbiamo impostare che il formato sia lo stesso possiamo cavarcela con MediaTypeId nella tabella Track.
		//Anche l'info dei millisecondi sta sulla tabella Track. Me la cavo con questa tabella.
		
		//Per ogni adiacenza ora aggiungo l'arco
		
		for(Adiacenza a: this.dao.getArchi(g, idMap)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getT1(), a.getT2(), a.getPeso());	//Ricordarsi di passare il grafo.
			
		}
		
		//Provo nuovamente
		System.out.println("Grafo creato!");
		System.out.println(String.format("Numero vertici %d", this.grafo.vertexSet().size()));
		System.out.println(String.format("Numero archi %d", this.grafo.edgeSet().size()));
		//Funziona
		
	}
	
	//Risoluzione punto d.
	public List<Adiacenza> getDeltaMassimo(){	//ritorno una lista perché posso avere più archi dal peso massimo.
		
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		
		int max = 0;	//Imposto un valore molto piccolo.
		
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			int peso = (int) this.grafo.getEdgeWeight(e);	//Ritorno il peso tramite getEdgeWeight.
			
			Adiacenza a = new Adiacenza (this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), peso);
			
			if(peso > max) {		//Se il peso è maggiore al massimo fino ad ora, sostituisco il massimo con il nuovo peso nella lista dopo averla ripulita.
				result.clear();
				result.add(a);
			}
			else if (peso == max){	//Se il peso è uguale aggiungo la nuova adiacenza
				result.add(a);
			}
		}
			//Dovrei ora avere una lista di tanti valori uguali corrispondenti al massimo del peso.
			return result;
	}
	
	public List<Track> getVertici() {
		return new ArrayList<Track>(this.grafo.vertexSet());	//Lista di canzoni.
	}
	
	public boolean grafoCreato() {	//Questo metodo mi serve per fare controllo degli errori nel collegamento del peso massimo al controller (posso collegare il peso massimo solo se ho già creato il grafo).
		if(this.grafo == null) {
			return false;
		}
		else
			return true;
	}
	
	public int nVertici() {
		
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		
		return this.grafo.edgeSet().size();
	}
	
	
	//Imposto ora la ricorsione per il punto 2.
	
	//Devo trovare l'insieme delle canzoni più numerose. E' un problema di ottimizzazione, devo tenere traccia della soluzione migliore.
}
