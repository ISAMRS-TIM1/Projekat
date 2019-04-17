package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import isamrs.tim1.model.Airline;
import isamrs.tim1.model.Destination;
import isamrs.tim1.model.Flight;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.PassengerSeat;
import isamrs.tim1.model.PlaneSegment;
import isamrs.tim1.model.PlaneSegmentClass;
import isamrs.tim1.model.QuickFlightReservation;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AirlineDTO implements Serializable {

	private String name;
	private String description;
	private double averageGrade;
	private double latitude;
	private double longitude;
	private ArrayList<Destination> destinations;
	private ArrayList<Flight> flights;
	private ArrayList<PlaneSegment> planeSegments;
	private ArrayList<QuickFlightReservation> quickReservations;
	private ArrayList<String> reservedSeats;

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
		this.destinations = new ArrayList<Destination>(airline.getDestinations());
		this.flights = new ArrayList<Flight>(airline.getFlights());
		this.planeSegments = new ArrayList<PlaneSegment>(airline.getPlaneSegments());
		this.quickReservations = new ArrayList<QuickFlightReservation>(airline.getQuickReservations());
		this.reservedSeats = new ArrayList<String>();
		for (FlightReservation r : airline.getNormalReservations()) {
			for (PassengerSeat ps : r.getPassengerSeats()) {
				if (ps.getSeat() != null) {
					this.reservedSeats.add(ps.getSeat().getRow() + "_" + ps.getSeat().getColumn());
				}
			}
		}
		for (QuickFlightReservation r : airline.getQuickReservations()) {
			for (PassengerSeat ps : r.getPassengerSeats()) {
				if (ps.getSeat() != null) {
					this.reservedSeats.add(ps.getSeat().getRow() + "_" + ps.getSeat().getColumn());
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

	public ArrayList<Destination> getDestinations() {
		return destinations;
	}

	public void setDestinations(ArrayList<Destination> destinations) {
		this.destinations = destinations;
	}

	public ArrayList<Flight> getFlights() {
		return flights;
	}

	public void setFlights(ArrayList<Flight> flights) {
		this.flights = flights;
	}

	public ArrayList<PlaneSegment> getPlaneSegments() {
		return planeSegments;
	}

	public void setPlaneSegments(ArrayList<PlaneSegment> planeSegments) {
		this.planeSegments = planeSegments;
	}

	public ArrayList<QuickFlightReservation> getQuickReservations() {
		return quickReservations;
	}

	public void setQuickReservations(ArrayList<QuickFlightReservation> quickReservations) {
		this.quickReservations = quickReservations;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 2401745303204690548L;

	public PlaneSegment getPlaneSegmentByClass(PlaneSegmentClass segmentClass) {
		for (PlaneSegment p : this.getPlaneSegments()) {
			if (p.getSegmentClass() == segmentClass) {
				return p;
			}
		}
		return null;
	}

}
