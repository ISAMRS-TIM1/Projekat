package isamrs.tim1.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "FlightReservations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class FlightReservation implements Serializable {

	private static final long serialVersionUID = 1235885051649796607L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "reservation_id", unique = true, nullable = false)
	protected Long id;

	@Column(name = "dateOfReservation", unique = false, nullable = true)
	protected Date dateOfReservation;

	@Column(name = "done", unique = false, nullable = true)
	protected Boolean done;

	@Column(name = "price", unique = false, nullable = true)
	protected Double price;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "user")
	protected UserReservation user;

	@OneToMany(mappedBy = "reservations", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	protected Set<PassengerSeat> passengerSeats;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight")
	protected Flight flight;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "hotelReservation")
	protected HotelReservation hotelReservation;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "vehicleReservation")
	protected VehicleReservation vehicleReservation;

	public FlightReservation() {
		super();
		this.passengerSeats = new HashSet<PassengerSeat>();
	}

	public FlightReservation(Set<PassengerSeat> passengerSeats, Flight flight) {
		super();
		passengerSeats = new HashSet<PassengerSeat>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateOfReservation() {
		return dateOfReservation;
	}

	public void setDateOfReservation(Date dateOfReservation) {
		this.dateOfReservation = dateOfReservation;
	}

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public UserReservation getUser() {
		return user;
	}

	public void setUser(UserReservation user) {
		this.user = user;
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

	public HotelReservation getHotelReservation() {
		return hotelReservation;
	}

	public void setHotelReservation(HotelReservation hotelReservation) {
		this.hotelReservation = hotelReservation;
	}

	public VehicleReservation getVehicleReservation() {
		return vehicleReservation;
	}

	public void setVehicleReservation(VehicleReservation vehicleReservation) {
		this.vehicleReservation = vehicleReservation;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
