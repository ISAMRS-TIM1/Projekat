package isamrs.tim1.dto;

import java.io.Serializable;

public class FlightHotelReservationDTO implements Serializable {

	private FlightReservationDTO flightReservation;
	private HotelReservationDTO hotelReservation;

	public FlightHotelReservationDTO() {
		super();
	}

	public FlightReservationDTO getFlightReservation() {
		return flightReservation;
	}

	public void setFlightReservation(FlightReservationDTO flightReservation) {
		this.flightReservation = flightReservation;
	}

	public HotelReservationDTO getHotelReservation() {
		return hotelReservation;
	}

	public void setHotelReservation(HotelReservationDTO hotelReservation) {
		this.hotelReservation = hotelReservation;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = -261659636235816579L;
}
