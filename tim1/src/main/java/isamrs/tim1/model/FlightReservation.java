package isamrs.tim1.model;

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
@Table(name = "FlightReservations")
public class FlightReservation extends Reservation {

	private static final long serialVersionUID = 1235885051649796607L;

	public FlightReservation(Set<PassengerSeat> passengerSeats, Flight flight) {
		super();
		passengerSeats = new HashSet<PassengerSeat>();
	}

	@OneToMany(mappedBy = "normalReservation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<PassengerSeat> passengerSeats;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight")
	private Flight flight;

	
	public FlightReservation() {
		super();
	}

	public Set<PassengerSeat> getPassengerSeats() {
		return passengerSeats;
	}

	public void setPassengerSeats(Set<PassengerSeat> passengerSeats) {
		this.passengerSeats = passengerSeats;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
