package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;

import org.hibernate.proxy.HibernateProxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import isamrs.tim1.model.Airline;
import isamrs.tim1.model.Destination;
import isamrs.tim1.model.Flight;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.QuickFlightReservation;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AirlineDTO implements Serializable {

	private String name;
	private String description;
	private double averageGrade;
	private double latitude;
	private double longitude;
	private ArrayList<DestinationDTO> destinations;
	private ArrayList<FlightDTO> flights;
	private ArrayList<QuickFlightReservationDTO> quickReservations;


	public AirlineDTO() {
		super();
	}

	public AirlineDTO(Airline airline) {
		super();
		this.name = airline.getName();
		this.description = airline.getDescription();
		this.averageGrade = airline.getAverageGrade();
		this.latitude = airline.getLocation().getLatitude();
		this.longitude = airline.getLocation().getLongitude();
		this.destinations = new ArrayList<DestinationDTO>();
		for (Destination d : airline.getDestinations()) {
			this.destinations.add(new DestinationDTO(d));
		}
		this.flights = new ArrayList<FlightDTO>();
		for (Flight f : airline.getFlights()) {
			this.flights.add(new FlightDTO(f));
		}
		this.quickReservations = new ArrayList<QuickFlightReservationDTO>();
		for (FlightReservation f : airline.getReservations()) {
			try {
				f = (FlightReservation) ((HibernateProxy) f).getHibernateLazyInitializer()
		                .getImplementation();
				if (f instanceof QuickFlightReservation && f.getUser() == null) {
					quickReservations.add(new QuickFlightReservationDTO((QuickFlightReservation) f));
				}
			}
			catch(Exception e) {
				if (f instanceof QuickFlightReservation && f.getUser() == null) {
					quickReservations.add(new QuickFlightReservationDTO((QuickFlightReservation) f));
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAverageGrade() {
		return averageGrade;
	}

	public void setAverageGrade(double averageGrade) {
		this.averageGrade = averageGrade;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public ArrayList<DestinationDTO> getDestinations() {
		return destinations;
	}

	public void setDestinations(ArrayList<DestinationDTO> destinations) {
		this.destinations = destinations;
	}

	public ArrayList<FlightDTO> getFlights() {
		return flights;
	}

	public void setFlights(ArrayList<FlightDTO> flights) {
		this.flights = flights;
	}

	public ArrayList<QuickFlightReservationDTO> getQuickReservations() {
		return quickReservations;
	}

	public void setQuickReservations(ArrayList<QuickFlightReservationDTO> quickReservations) {
		this.quickReservations = quickReservations;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 2401745303204690548L;

}
