package isamrs.tim1.dto;

public class FlightReservationDTO {
	private long id;
	private String flightCode;
	private String[] invitedFriends;
	private Integer numberOfPassengers;
	private PassengerDTO[] passengers;
	private String[] seats;
	private String reservationInf;
	private String dateOfReservation;
	private Double price;
	private Integer grade;
	private Long quickReservationID;

	public FlightReservationDTO() {
	}

	public FlightReservationDTO(long id, String res, String date, double price, Integer grade) {
		this.id = id;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getQuickReservationID() {
		return quickReservationID;
	}

	public void setQuickReservationID(Long quickReservationID) {
		this.quickReservationID = quickReservationID;
	}

}
