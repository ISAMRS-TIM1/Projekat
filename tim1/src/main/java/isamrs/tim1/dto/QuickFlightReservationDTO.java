package isamrs.tim1.dto;

import java.io.Serializable;
import java.util.ArrayList;

import isamrs.tim1.model.PassengerSeat;
import isamrs.tim1.model.PlaneSegmentClass;
import isamrs.tim1.model.QuickFlightReservation;

public class QuickFlightReservationDTO implements Serializable {

	private static final long serialVersionUID = 8308369767514619103L;
	
	private String flightCode;
	private String seat;
	private String discount;
	private Double realPrice;
	private PlaneSegmentClass seatClass;
	
	public QuickFlightReservationDTO() {
		super();
	}
	public QuickFlightReservationDTO(QuickFlightReservation fr) {
		this.flightCode = fr.getFlight().getFlightCode();
		this.discount = fr.getDiscount().toString();
		this.realPrice = fr.getPrice();
		ArrayList<PassengerSeat> ps = new ArrayList<PassengerSeat>(fr.getPassengerSeats());
		this.seat = ps.get(0).getSeat().getRow() + "_" + ps.get(0).getSeat().getColumn();
		this.seatClass = ps.get(0).getSeat().getPlaneSegment().getSegmentClass();
	}
	public String getFlightCode() {
		return flightCode;
	}
	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}
	public String getSeat() {
		return seat;
	}
	public void setSeat(String seat) {
		this.seat = seat;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public Double getRealPrice() {
		return realPrice;
	}
	public void setRealPrice(Double realPrice) {
		this.realPrice = realPrice;
	}
	public PlaneSegmentClass getSeatClass() {
		return seatClass;
	}
	public void setSeatClass(PlaneSegmentClass seatClass) {
		this.seatClass = seatClass;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
