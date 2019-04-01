package isamrs.tim1.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Flights")
public class Flight {

	public Flight() {
		super();
		locationsOfConnecting = new ArrayList<String>();
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
	private ArrayList<String> locationsOfConnecting;

	@Column(name = "ticketPrice", unique = false, nullable = false)
	private Double ticketPrice;

	@Column(name = "pricePerBag", unique = false, nullable = false)
	private Double pricePerBag;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "airline")
	private Airline airline;

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

	public Double getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(Double ticketPrice) {
		this.ticketPrice = ticketPrice;
	}

	public Double getPricePerBag() {
		return pricePerBag;
	}

	public void setPricePerBag(Double pricePerBag) {
		this.pricePerBag = pricePerBag;
	}

	public Airline getAirline() {
		return airline;
	}

	public void setAirline(Airline airline) {
		this.airline = airline;
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

	public ArrayList<String> getLocationsOfConnecting() {
		return locationsOfConnecting;
	}

	public void setLocationsOfConnecting(ArrayList<String> locationsOfConnecting) {
		this.locationsOfConnecting = locationsOfConnecting;
	}

	
}
