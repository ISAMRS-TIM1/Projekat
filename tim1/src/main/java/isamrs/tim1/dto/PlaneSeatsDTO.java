package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import isamrs.tim1.model.Flight;
import isamrs.tim1.model.PlaneSegment;
import isamrs.tim1.model.Seat;

public class PlaneSeatsDTO implements Serializable {
	
	private static final long serialVersionUID = -8886693041430317406L;
	
	@NotEmpty
	private String[] savedSeats;
	
	@NotBlank
	private String flightCode;
	
	private ArrayList<PlaneSegment> planeSegments;
	private ArrayList<String> reservedSeats;
	private Double firstClassPrice;
	private Double businessClassPrice;
	private Double economyClassPrice;
	
	public PlaneSeatsDTO() {
		super();
	}

	public PlaneSeatsDTO(Flight flight) {
		super();
		this.flightCode = flight.getFlightCode();
		this.planeSegments = new ArrayList<PlaneSegment>(flight.getPlaneSegments());
		this.reservedSeats = new ArrayList<String>();
		this.firstClassPrice = flight.getFirstClassPrice();
		this.businessClassPrice = flight.getBusinessClassPrice();
		this.economyClassPrice = flight.getEconomyClassPrice();
		for (PlaneSegment pSeg : flight.getPlaneSegments()) {
			for (Seat s : pSeg.getSeats()) {
				if (s.getPassengerSeat() != null) {
					this.reservedSeats.add(s.getRow() + "_" + s.getColumn());
				}
			}
		}
	}

	public ArrayList<PlaneSegment> getPlaneSegments() {
		return planeSegments;
	}

	public void setPlaneSegments(ArrayList<PlaneSegment> planeSegments) {
		this.planeSegments = planeSegments;
	}

	public ArrayList<String> getReservedSeats() {
		return reservedSeats;
	}

	public void setReservedSeats(ArrayList<String> reservedSeats) {
		this.reservedSeats = reservedSeats;
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
	
	public String[] getSavedSeats() {
		return savedSeats;
	}

	public void setSavedSeats(String[] savedSeats) {
		this.savedSeats = savedSeats;
	}

	public String getFlightCode() {
		return flightCode;
	}

	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
