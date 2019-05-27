package it.polito.tdp.emergency.model;

import java.time.LocalTime;

/**
 * Evento per la simulazione di Emergency
 *
 */
public class Evento implements Comparable<Evento>{

	/**
	 * Tipologie di eventi che possono accadere 
	 */
	public enum TipoEvento {
		ARRIVO, // Un nuovo paziente arriva in Emergency
		TRIAGE, // Infermiere assegna codice colore
		VISITA, // Paziente viene visitato da medico
		CURATO, // Paziente esce da studio medico
		TIMEOUT, // Attesa troppo lunga
	}
	
	private LocalTime ora ; // Timestamp dell'evento
	private TipoEvento tipo ; // Tipologia
	private Paziente paziente ; // Chi e' il paziente coinvolto nell'evento
	
	public Evento(LocalTime ora, TipoEvento tipo, Paziente paziente) {
		super();
		this.ora = ora;
		this.tipo = tipo;
		this.paziente = paziente;
	}
	public LocalTime getOra() {
		return ora;
	}
	public TipoEvento getTipo() {
		return tipo;
	}
	public Paziente getPaziente() {
		return paziente;
	}
	
	// Ordina per orario dell'evento
	@Override
	public int compareTo(Evento other) {
		return this.ora.compareTo(other.ora);
	}
	
	@Override
	public String toString() {
		return "Evento [ora=" + ora + ", tipo=" + tipo + ", paziente=" + paziente + "]";
	}
	
	
	
}
