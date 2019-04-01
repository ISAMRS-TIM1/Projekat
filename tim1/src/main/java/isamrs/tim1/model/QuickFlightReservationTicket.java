package isamrs.tim1.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "QuickFlightReservation")
public class QuickFlightReservationTicket extends QuickReservation implements Serializable {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "airline")
	private Airline airline;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight")
	private Flight flight;

	@OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<PassengerSeat> passengerSeats;

	public QuickFlightReservationTicket() {
		super();
		passengerSeats = new HashSet<PassengerSeat>();
	}

	public Airline getAirline() {
		return airline;
	}

	public void setAirline(Airline airline) {
		this.airline = airline;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public Set<PassengerSeat> getPassengerSeats() {
		return passengerSeats;
	}

	public void setPassengerSeats(Set<PassengerSeat> passengerSeats) {
		this.passengerSeats = passengerSeats;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = -2582500334701074086L;

}
