package isamrs.tim1.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import isamrs.tim1.model.Flight;

public class FlightUserViewDTO implements Serializable {
	
	private String departureTime;
	private String landingTime;
	private Double firstClassPrice;
	private Double businessClassPrice;
	private Double economyClassPrice;
	private String airline;
	private Integer numberOfConnections;
	
	public FlightUserViewDTO() {
		super();
	}

	public FlightUserViewDTO(Flight f) {
		super();
		this.firstClassPrice = f.getFirstClassPrice();
		this.businessClassPrice = f.getBusinessClassPrice();
		this.economyClassPrice = f.getEconomyClassPrice();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		this.landingTime = sdf.format(f.getLandingTime());
		this.departureTime = sdf.format(f.getDepartureTime());
		this.airline = f.getAirline().getName();
		this.numberOfConnections = f.getNumberOfFlightConnections();
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

	public String getAirline() {
		return airline;
	}

	public void setAirline(String airline) {
		this.airline = airline;
	}

	public Integer getNumberOfConnections() {
		return numberOfConnections;
	}

	public void setNumberOfConnections(Integer numberOfConnections) {
		this.numberOfConnections = numberOfConnections;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = -8733756966532954933L;

}
