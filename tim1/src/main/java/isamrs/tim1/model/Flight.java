package isamrs.tim1.model;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Flights")
public class Flight {

	public Flight() {
		super();
		locationsOfConnecting = new HashSet<String>();
		reservations = new HashSet<FlightReservation>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "flight_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "departureTime", unique = false, nullable = false)
	private Date departureTime;

	@Column(name = "landingTime", unique = false, nullable = false)
	private Date landingTime;

	@Column(name = "flightDuration", unique = false, nullable = false)
	private Integer flightDuration;

	@Column(name = "flightLength", unique = false, nullable = false)
	private Integer flightLength;

	@Column(name = "numberOfFlightConnections", unique = false, nullable = false)
	private Integer numberOfFlightConnections;

	@Column(name = "locationsOfConnecting", unique = false, nullable = false)
	private Set<String> locationsOfConnecting;

	@Column(name = "ticketPrice", unique = false, nullable = false)
	private Integer ticketPrice;

	/*
	 * @Column(name = "luggagePricelist", unique = false, nullable = false)
	 * private Map<String, Integer> luggagePricelist;
	 */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "airline")
	private Airline airline;

	@OneToMany(mappedBy = "flight", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<FlightReservation> reservations;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "startDestination")
	private Destination startDestination;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "endDestination")
	private Destination endDestination;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public Date getLandingTime() {
		return landingTime;
	}

	public void setLandingTime(Date landingTime) {
		this.landingTime = landingTime;
	}

	public Integer getFlightDuration() {
		return flightDuration;
	}

	public void setFlightDuration(Integer flightDuration) {
		this.flightDuration = flightDuration;
	}

	public Integer getFlightLength() {
		return flightLength;
	}

	public void setFlightLength(Integer flightLength) {
		this.flightLength = flightLength;
	}

	public Integer getNumberOfFlightConnections() {
		return numberOfFlightConnections;
	}

	public void setNumberOfFlightConnections(Integer numberOfFlightConnections) {
		this.numberOfFlightConnections = numberOfFlightConnections;
	}

	public Integer getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(Integer ticketPrice) {
		this.ticketPrice = ticketPrice;
	}

	public Airline getAirline() {
		return airline;
	}

	public void setAirline(Airline airline) {
		this.airline = airline;
	}

	public Set<FlightReservation> getReservations() {
		return reservations;
	}

	public void setReservations(Set<FlightReservation> reservations) {
		this.reservations = reservations;
	}

	public Destination getStartDestination() {
		return startDestination;
	}

	public void setStartDestination(Destination startDestination) {
		this.startDestination = startDestination;
	}

	public Destination getEndDestination() {
		return endDestination;
	}

	public void setEndDestination(Destination endDestination) {
		this.endDestination = endDestination;
	}

	public Set<String> getLocationsOfConnecting() {
		return locationsOfConnecting;
	}

	public void setLocationsOfConnecting(Set<String> locationsOfConnecting) {
		this.locationsOfConnecting = locationsOfConnecting;
	}

}
