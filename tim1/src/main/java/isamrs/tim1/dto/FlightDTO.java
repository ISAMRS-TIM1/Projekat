package isamrs.tim1.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import isamrs.tim1.model.Flight;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.PassengerSeat;
import isamrs.tim1.model.PlaneSegment;
import isamrs.tim1.model.PlaneSegmentClass;

public class FlightDTO implements Serializable {

	private String departureTime;
	private String landingTime;
	private Integer flightDistance;
	private String[] connections;
	private Double firstClassPrice;
	private Double businessClassPrice;
	private Double economyClassPrice;
	private Double pricePerBag;
	private String airlineName;
	private String startDestination;
	private String endDestination;
	private Double averageGrade;
	private String flightCode;
	private String countryName;
	private ArrayList<String> reservedSeats;
	private ArrayList<PlaneSegment> planeSegments;
	private boolean roundTrip;
	private String returningDepartureTime;
	private String returningLandingTime;
	private boolean multiCity;
	
	public FlightDTO() {
		super();
	}
	
	public FlightDTO(Flight f) {
		this.startDestination = f.getStartDestination().getName();
		this.endDestination = f.getEndDestination().getName();
		this.airlineName = f.getAirline().getName();
		this.pricePerBag = f.getPricePerBag();
		this.firstClassPrice = f.getFirstClassPrice();
		this.businessClassPrice = f.getBusinessClassPrice();
		this.economyClassPrice = f.getEconomyClassPrice();
		this.flightDistance = f.getFlightLength();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		this.landingTime = sdf.format(f.getLandingTime());
		this.departureTime = sdf.format(f.getDepartureTime());
		this.roundTrip = f.isRoundTrip();
		if (f.isRoundTrip()) {
			this.returningDepartureTime = sdf.format(f.getReturningDepartureTime());
			this.returningLandingTime = sdf.format(f.getReturningLandingTime());
		}
		this.connections = new String[f.getLocationsOfConnecting().size()];
		this.connections = f.getLocationsOfConnecting().toArray(this.connections);
		this.averageGrade = f.getAverageGrade();
		this.flightCode = f.getFlightCode();
		this.countryName = f.getEndDestination().getLocation().getCountry();
		this.planeSegments = new ArrayList<PlaneSegment>(f.getPlaneSegments());
		this.reservedSeats = new ArrayList<String>();
		for (FlightReservation r : f.getAirline().getReservations()) {
			if (r.getFlight().getFlightCode().equals(f.getFlightCode())) {
				for (PassengerSeat ps : r.getPassengerSeats()) {
					if (ps.getSeat() != null) {
						this.reservedSeats.add(ps.getSeat().getRow() + "_" + ps.getSeat().getColumn());
					}
				}
			}
		}
	}

	public String getDepartureTime() {
		return departureTime;
	}


	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}


	public String getLandingTime() {
		return landingTime;
	}


	public void setLandingTime(String landingTime) {
		this.landingTime = landingTime;
	}


	public Integer getFlightDistance() {
		return flightDistance;
	}


	public void setFlightDistance(Integer flightDistance) {
		this.flightDistance = flightDistance;
	}


	public String[] getConnections() {
		return connections;
	}


	public void setConnections(String[] connections) {
		this.connections = connections;
	}

	public Double getFirstClassPrice() {
		return firstClassPrice;
	}

	public void setFirstClassPrice(Double firstClassPrice) {
		this.firstClassPrice = firstClassPrice;
	}

	public Double getBusinessClassPrice() {
		return businessClassPrice;
	}

	public void setBusinessClassPrice(Double businessClassPrice) {
		this.businessClassPrice = businessClassPrice;
	}

	public Double getEconomyClassPrice() {
		return economyClassPrice;
	}

	public void setEconomyClassPrice(Double economyClassPrice) {
		this.economyClassPrice = economyClassPrice;
	}

	public Double getPricePerBag() {
		return pricePerBag;
	}


	public void setPricePerBag(Double pricePerBag) {
		this.pricePerBag = pricePerBag;
	}


	public String getAirlineName() {
		return airlineName;
	}


	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}


	public String getStartDestination() {
		return startDestination;
	}


	public void setStartDestination(String startDestination) {
		this.startDestination = startDestination;
	}


	public String getEndDestination() {
		return endDestination;
	}


	public void setEndDestination(String endDestination) {
		this.endDestination = endDestination;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Double getAverageGrade() {
		return averageGrade;
	}

	public void setAverageGrade(Double averageGrade) {
		this.averageGrade = averageGrade;
	}

	public String getFlightCode() {
		return flightCode;
	}

	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}
	
	public ArrayList<String> getReservedSeats() {
		return reservedSeats;
	}

	public void setReservedSeats(ArrayList<String> reservedSeats) {
		this.reservedSeats = reservedSeats;
	}

	public ArrayList<PlaneSegment> getPlaneSegments() {
		return planeSegments;
	}

	public void setPlaneSegments(ArrayList<PlaneSegment> planeSegments) {
		this.planeSegments = planeSegments;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public boolean isRoundTrip() {
		return roundTrip;
	}

	public void setRoundTrip(boolean roundTrip) {
		this.roundTrip = roundTrip;
	}

	public String getReturningDepartureTime() {
		return returningDepartureTime;
	}

	public void setReturningDepartureTime(String returningDepartureTime) {
		this.returningDepartureTime = returningDepartureTime;
	}

	public String getReturningLandingTime() {
		return returningLandingTime;
	}

	public void setReturningLandingTime(String returningLandingTime) {
		this.returningLandingTime = returningLandingTime;
	}

	public boolean isMultiCity() {
		return multiCity;
	}

	public void setMultiCity(boolean multiCity) {
		this.multiCity = multiCity;
	}

	public PlaneSegment getPlaneSegmentByClass(PlaneSegmentClass segmentClass) {
		for (PlaneSegment p : this.getPlaneSegments()) {
			if (p.getSegmentClass() == segmentClass) {
				return p;
			}
		}
		return null;
	}
	
	private static final long serialVersionUID = 3798933624021732767L;

}
