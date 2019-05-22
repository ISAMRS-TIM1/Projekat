package isamrs.tim1.dto;

import java.io.Serializable;

public class FlightVehicleReservationDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4001868159308901791L;
	private FlightReservationDTO flightReservation;
	private VehicleReservationDTO vehicleReservation;

	public FlightVehicleReservationDTO() {
		super();
	}

	public FlightReservationDTO getFlightReservation() {
		return flightReservation;
	}

	public void setFlightReservation(FlightReservationDTO flightReservation) {
		this.flightReservation = flightReservation;
	}

	public VehicleReservationDTO getVehicleReservation() {
		return vehicleReservation;
	}

	public void setVehicleReservation(VehicleReservationDTO vehicleReservation) {
		this.vehicleReservation = vehicleReservation;
	}

}
