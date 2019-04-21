package isamrs.tim1.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import isamrs.tim1.model.Flight;

public class FlightDTO implements Serializable {

	private String departureTime;
	private String landingTime;
	private Integer flightDistance;
	private String[] connections;
	private Double ticketPrice;
	private Double pricePerBag;
	private String airlineName;
	private String startDestination;
	private String endDestination;
	
	public FlightDTO() {
		super();
	}
	
	public FlightDTO(Flight f) {
		this.startDestination = f.getStartDestination().getName();
		this.endDestination = f.getEndDestination().getName();
		this.airlineName = f.getAirline().getName();
		this.pricePerBag = f.getPricePerBag();
		this.ticketPrice = f.getTicketPrice();
		this.flightDistance = f.getFlightLength();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		this.landingTime = sdf.format(f.getLandingTime());
		this.departureTime = sdf.format(f.getDepartureTime());
		this.connections = new String[f.getLocationsOfConnecting().size()];
		this.connections = f.getLocationsOfConnecting().toArray(this.connections);
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


	private static final long serialVersionUID = 3798933624021732767L;

}
