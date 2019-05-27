package it.polito.tdp.emergency.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import it.polito.tdp.emergency.model.Evento.TipoEvento;
import it.polito.tdp.emergency.model.Paziente.StatoPaziente;
import javafx.scene.layout.Priority;

public class Simulatore {

	// Coda degli eventi
	private PriorityQueue<Evento> queue = new PriorityQueue<>() ;
	
	// Modello del Mondo
	private List<Paziente> pazienti;
	private int studiLiberi;
	
	// Parametri di simulazione (Che l'utente può modificare in ogni momento)
	private int NS = 3; // Numero di studi medici
	private int NP = 50; // Numero di pazienti in arrivo
	private Duration T_ARRIVAL = Duration.ofMinutes(15); // Intervallo di tempo tra i pazienti

	private LocalTime T_inizio = LocalTime.of(8, 0);
	private LocalTime T_fine = LocalTime.of(20, 0);

	//Durata visita
	private int DURATION_TRIAGE = 5;
	private int DURATION_WHITE = 10;
	private int DURATION_YELLOW = 15;
	private int DURATION_RED = 30;
	
	//Durata TimeOut
	private int TIMEOUT_WHITE = 120;
	private int TIMEOUT_YELLOW = 60;
	private int TIMEOUT_RED = 90;

	
	// Statistiche da calcolare
	private int numDimessi;
	private int numAbbandoni;
	private int numMorti;
	
	// Variabili interne
	private StatoPaziente nuovoStatoPaziente;
	
	public Simulatore() {
		this.pazienti = new ArrayList<>();
	}
	
	public void ruotaNuovoStatoPaziente() {
		if (nuovoStatoPaziente==StatoPaziente.WAITING_WHITE)
			nuovoStatoPaziente=StatoPaziente.WAITING_YELLOW;
		else if (nuovoStatoPaziente==StatoPaziente.WAITING_YELLOW)
			nuovoStatoPaziente=StatoPaziente.WAITING_RED;
		else if (nuovoStatoPaziente==StatoPaziente.WAITING_RED)
			nuovoStatoPaziente=StatoPaziente.WAITING_WHITE;
	}
	
	public void init() {
		//Creare i pazienti, il primo paziente arriva alle 8.00 (T_INIZIO)
		LocalTime oraArrivo = T_inizio;
		pazienti.clear();
		
		for (int i = 0; i<NP; i++) {
			Paziente p = new Paziente(i+1, oraArrivo);
			pazienti.add(p);
			
			//Ogni paziente arriva ogni 15 min (incremento 15 min)
			oraArrivo = oraArrivo.plus(T_ARRIVAL);
		}
		
		//Creare studi liberi
		studiLiberi = NS;
		
		nuovoStatoPaziente = nuovoStatoPaziente.WAITING_WHITE;
		
		//Creare gli eventi iniziali
		queue.clear();
		for (Paziente p : pazienti) {
			queue.add(new Evento(p.getOraArrivo(), TipoEvento.ARRIVO, p));
		}
		
		//Resettare le statistiche
		numDimessi=0;
		numAbbandoni=0;
		numMorti=0;
	}
	
	public void run() {
		
		while (!queue.isEmpty()) {
			//Estraggo evento dalla coda
			Evento ev = queue.poll();
			
			System.out.println(ev.toString());
			
			/*
			Se il paziente arriva dopo l'orario di chiusura, esco dal ciclo (Simulazione termina alle 20.00)
			if (ev.getOra().isAfter(T_fine)) break;
			*/
			
			Paziente p = ev.getPaziente();
			
			//Gestisco i diversi tipi di eventi
			switch (ev.getTipo()) {
			
			case ARRIVO:
				//Tra 5 minuti verrà assegnato un codice colore -> Diventa TRIAGE
				queue.add(new Evento(ev.getOra().plusMinutes(DURATION_TRIAGE),TipoEvento.TRIAGE,ev.getPaziente()));
				break;
				
			case TRIAGE:
				p.setStato(nuovoStatoPaziente);
				
				//Codice bianco
				if (p.getStato()==StatoPaziente.WAITING_WHITE)
				queue.add(new Evento(ev.getOra().plusMinutes(TIMEOUT_WHITE),TipoEvento.TIMEOUT,p));
				
				//Codice giallo
				if (p.getStato()==StatoPaziente.WAITING_YELLOW)
					queue.add(new Evento(ev.getOra().plusMinutes(TIMEOUT_YELLOW),TipoEvento.TIMEOUT,p));
				
				//Codice rosso
				if (p.getStato()==StatoPaziente.WAITING_RED)
					queue.add(new Evento(ev.getOra().plusMinutes(TIMEOUT_RED),TipoEvento.TIMEOUT,p));
			    
				//Aggiorno stato del prossimo paziente White -> Yellow -> Red -> White -> Yellow -> ... e così via
				ruotaNuovoStatoPaziente();
				
				break;
				
			case VISITA:
				
				//Determina il paziente con priorità max
				//Pazienta entra in uno studio
				//Studio diventa occupato
				//Schedula l'uscita (CURATO) del paziente
				
				break;
				
			case CURATO:
				//Paziente è fuori
				//Aggiorna numDimessi
				//Schedula evento Visita "adesso"
				break;
				
			case TIMEOUT:
				
				if (p.getStato() == StatoPaziente.WAITING_WHITE) {
					p.setStato(StatoPaziente.OUT);
					numAbbandoni++;
				}
				else if (p.getStato() == StatoPaziente.WAITING_YELLOW) {
					p.setStato(StatoPaziente.WAITING_RED);
					queue.add(new Evento(ev.getOra().plusMinutes(TIMEOUT_RED),TipoEvento.TIMEOUT,p));
				}
				else if (p.getStato() == StatoPaziente.WAITING_RED) {
					p.setStato(StatoPaziente.BLACK);
					numMorti++;
				}
				else {
					System.out.println("Timeout anomalo nello stato "+p.getStato());
				}
				break;
				
				
			}
		}
		
	}
	
}
