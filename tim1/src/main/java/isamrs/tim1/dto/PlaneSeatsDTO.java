package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;

import isamrs.tim1.model.Airline;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.PassengerSeat;
import isamrs.tim1.model.PlaneSegment;
import isamrs.tim1.model.QuickFlightReservation;

public class PlaneSeatsDTO implements Serializable {
	
	private static final long serialVersionUID = -8886693041430317406L;
	private ArrayList<PlaneSegment> planeSegments;
	private ArrayList<String> reservedSeats;
	private Double firstClassPrice;
	private Double businessClassPrice;
	private Double economyClassPrice;
	
	public PlaneSeatsDTO() {
		super();
	}

	public PlaneSeatsDTO(Airline airline) {
		super();
		this.planeSegments = new ArrayList<PlaneSegment>(airline.getPlaneSegments());
		this.reservedSeats = new ArrayList<String>();
		this.firstClassPrice = 50.0; // until the reservation feature is made
		this.businessClassPrice = 40.0; // until the reservation feature is made
		this.economyClassPrice = 30.0; // until the reservation feature is made
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
