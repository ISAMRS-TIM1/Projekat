package isamrs.tim1.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.PassengerSeat;

public class DetailedReservationDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2839478733086154576L;
	private String startDestination;
	private String endDestination;
	private String departureTime;
	private String landingTime;
	private String airlineName;
	private Integer flightDistance;
	private ArrayList<String> connections;
	private Integer numOfFlightSeats;
	private ArrayList<String> seats;
	private boolean roundTrip;
	private String returningDepartureTime;
	private String returningLandingTime;
	private boolean done;
	private Double grade;
	private HotelReservationDTO hotelRes;
	private VehicleReservationDTO vehicleRes;

	public DetailedReservationDTO() {
		super();
	}

	public DetailedReservationDTO(FlightReservation flightRes) {
		this.startDestination = flightRes.getFlight().getStartDestination().getName();
		this.endDestination = flightRes.getFlight().getEndDestination().getName();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		this.departureTime = sdf.format(flightRes.getFlight().getDepartureTime());
		this.landingTime = sdf.format(flightRes.getFlight().getLandingTime());
		this.roundTrip = flightRes.getFlight().isRoundTrip();
		this.grade = flightRes.getGrade();

		if (flightRes.getDone() != null) {
			this.done = flightRes.getDone().booleanValue();
		}

		if (this.roundTrip) {
			this.returningDepartureTime = sdf.format(flightRes.getFlight().getReturningDepartureTime());
			this.returningLandingTime = sdf.format(flightRes.getFlight().getReturningLandingTime());
		}
		this.airlineName = flightRes.getFlight().getAirline().getName();
		this.flightDistance = flightRes.getFlight().getFlightLength();
		this.numOfFlightSeats = flightRes.getPassengerSeats().size();
		this.connections = flightRes.getFlight().getLocationsOfConnecting();
		this.seats = new ArrayList<String>();
		String seat;
		for (PassengerSeat p : flightRes.getPassengerSeats()) {
			seat = p.getSeat().getRow() + "_" + p.getSeat().getColumn() + " - "
					+ p.getSeat().getPlaneSegment().getSegmentClass();
			this.seats.add(seat);
		}
		this.hotelRes = flightRes.getHotelReservation() == null ? null
				: new HotelReservationDTO(flightRes.getHotelReservation());

		this.vehicleRes = flightRes.getVehicleReservation() == null ? null
				: new VehicleReservationDTO(flightRes.getVehicleReservation());
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

	public String getAirlineName() {
		return airlineName;
	}

	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}

	public Integer getFlightDistance() {
		return flightDistance;
	}

	public void setFlightDistance(Integer flightDistance) {
		this.flightDistance = flightDistance;
	}

	public ArrayList<String> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<String> connections) {
		this.connections = connections;
	}

	public Integer getNumOfFlightSeats() {
		return numOfFlightSeats;
	}

	public void setNumOfFlightSeats(Integer numOfFlightSeats) {
		this.numOfFlightSeats = numOfFlightSeats;
	}

	public ArrayList<String> getSeats() {
		return seats;
	}

	public void setSeats(ArrayList<String> seats) {
		this.seats = seats;
	}

	public HotelReservationDTO getHotelRes() {
		return hotelRes;
	}

	public void setHotelRes(HotelReservationDTO hotelRes) {
		this.hotelRes = hotelRes;
	}

	public VehicleReservationDTO getVehicleRes() {
		return vehicleRes;
	}

	public void setVehicleRes(VehicleReservationDTO vehicleRes) {
		this.vehicleRes = vehicleRes;
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

	public boolean getDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}
}
