package isamrs.tim1.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PassengerSeats")
public class PassengerSeat implements Serializable {

	private static final long serialVersionUID = 2760793982505559478L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "passengerSeat_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "normalReservation")
	private FlightReservation normalReservation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quickReservation")
	private QuickFlightReservation quickReservation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seat")
	private Seat seat;

	@Column(name = "name", unique = false, nullable = false)
	private String name;

	@Column(name = "surname", unique = false, nullable = false)
	private String surname;

	@Column(name = "passport", unique = false, nullable = false)
	private String passport;

	public PassengerSeat() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FlightReservation getNormalReservation() {
		return normalReservation;
	}

	public void setNormalReservation(FlightReservation normalReservation) {
		this.normalReservation = normalReservation;
	}

	public QuickFlightReservation getQuickReservation() {
		return quickReservation;
	}

	public void setQuickReservation(QuickFlightReservation quickReservation) {
		this.quickReservation = quickReservation;
	}

	public Seat getSeat() {
		return seat;
	}

	public void setSeat(Seat seat) {
		this.seat = seat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
