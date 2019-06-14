package isamrs.tim1.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class FlightInvitation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "flightInv_id", unique = true, nullable = false)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invited")
	private RegisteredUser invited;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inviter")
	private RegisteredUser inviter;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flightReservation")
	private FlightReservation flightReservation;
	
	@Column(name = "dateOfInviting", unique = false, nullable = false)
	protected Date dateOfInviting;

	public RegisteredUser getInvited() {
		return invited;
	}

	public void setInvited(RegisteredUser invited) {
		this.invited = invited;
	}

	public RegisteredUser getInviter() {
		return inviter;
	}

	public void setInviter(RegisteredUser inviter) {
		this.inviter = inviter;
	}

	public FlightReservation getFlightReservation() {
		return flightReservation;
	}

	public void setFlightReservation(FlightReservation flightReservation) {
		this.flightReservation = flightReservation;
	}

	public Date getDateOfInviting() {
		return dateOfInviting;
	}

	public void setDateOfInviting(Date dateOfInviting) {
		this.dateOfInviting = dateOfInviting;
	}
}
