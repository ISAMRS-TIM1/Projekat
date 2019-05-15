package isamrs.tim1.dto;

public class FlightReservationDTO {
	private String flightCode;
	private String[] invitedFriends;
	private Integer numberOfPassengers;
	private PassengerDTO[] passengers;
	private Long QuickReservationID;
	private String[] seats;
	
	public String getFlightCode() {
		return flightCode;
	}
	public void setFlightCode(String flightCode) {
		this.flightCode = flightCode;
	}
	public String[] getInvitedFriends() {
		return invitedFriends;
	}
	public void setInvitedFriends(String[] invitedFriends) {
		this.invitedFriends = invitedFriends;
	}
	public Integer getNumberOfPassengers() {
		return numberOfPassengers;
	}
	public void setNumberOfPassengers(Integer numberOfPassengers) {
		this.numberOfPassengers = numberOfPassengers;
	}
	public PassengerDTO[] getPassengers() {
		return passengers;
	}
	public void setPassengers(PassengerDTO[] passengers) {
		this.passengers = passengers;
	}
	public Long getQuickReservationID() {
		return QuickReservationID;
	}
	public void setQuickReservationID(Long quickReservationID) {
		QuickReservationID = quickReservationID;
	}
	public String[] getSeats() {
		return seats;
	}
	public void setSeats(String[] seats) {
		this.seats = seats;
	}
}
