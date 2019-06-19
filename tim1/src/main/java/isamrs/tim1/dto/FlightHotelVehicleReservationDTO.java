package isamrs.tim1.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class FlightHotelVehicleReservationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6538294253395903027L;
	@NotNull
	private FlightReservationDTO flightReservation;
	@NotNull
	private HotelReservationDTO hotelReservation;
	@NotNull
	private VehicleReservationDTO vehicleReservation;

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

	public VehicleReservationDTO getVehicleReservation() {
		return vehicleReservation;
	}

	public void setVehicleReservation(VehicleReservationDTO vehicleReservation) {
		this.vehicleReservation = vehicleReservation;
	}

}
