package isamrs.tim1.dto;

import java.io.Serializable;

public class InvitingReservationDTO implements Serializable {

	private static final long serialVersionUID = 1144071285555998856L;
	private Long reservationID;
	private String description;
	
	public InvitingReservationDTO() {}
	
	public InvitingReservationDTO(Long resID, String description) {
		this.reservationID = resID;
		this.description = description;
	}
	
	public Long getReservationID() {
		return reservationID;
	}
	public void setReservationID(Long reservationID) {
		this.reservationID = reservationID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
