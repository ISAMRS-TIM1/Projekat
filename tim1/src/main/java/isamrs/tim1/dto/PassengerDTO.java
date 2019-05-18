package isamrs.tim1.dto;

public class PassengerDTO {
	private String firstName;
	private String lastName;
	private String passport;
	private Integer numberOfBags;
	
	public PassengerDTO() {}
	public PassengerDTO(String firstName, String lastName, String passport, int numberOfBags) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.passport = passport;
		this.numberOfBags = numberOfBags;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPassport() {
		return passport;
	}
	public void setPassport(String passport) {
		this.passport = passport;
	}
	public Integer getNumberOfBags() {
		return numberOfBags;
	}
	public void setNumberOfBags(Integer numberOfBags) {
		this.numberOfBags = numberOfBags;
	}
}
