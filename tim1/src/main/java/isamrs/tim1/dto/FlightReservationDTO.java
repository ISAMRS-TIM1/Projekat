package isamrs.tim1.dto;

public class FlightReservationDTO {
	private String flightCode;
	private String[] invitedFriends;
	private Integer numberOfPassengers;
	private PassengerDTO[] passengers;
	private Long QuickReservationID;
	private String[] seats;
	private String reservationInf;
	private String dateOfReservation;
	private Double price;
	private Integer grade;
	
	public FlightReservationDTO() {}
	
	public FlightReservationDTO(String res, String date, double price, Integer grade) {
		this.reservationInf = res;
		this.dateOfReservation = date;
		this.price = price;
		this.grade = grade;
	}
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

	public String getReservationInf() {
		return reservationInf;
	}

	public void setReservationInf(String reservationInf) {
		this.reservationInf = reservationInf;
	}

	public String getDateOfReservation() {
		return dateOfReservation;
	}

	public void setDateOfReservation(String dateOfReservation) {
		this.dateOfReservation = dateOfReservation;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}
}