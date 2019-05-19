package isamrs.tim1.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "HotelReservations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class HotelReservation implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "reservation_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "fromDate", unique = false, nullable = false)
	private Date fromDate;

	@Column(name = "toDate", unique = false, nullable = false)
	private Date toDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hotelRoom")
	private HotelRoom hotelRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "additionalService")
	private HotelAdditionalService additionalService;

	@OneToOne(mappedBy = "hotelReservation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private FlightReservation flightReservation;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public HotelRoom getHotelRoom() {
		return hotelRoom;
	}

	public void setHotelRoom(HotelRoom hotelRoom) {
		this.hotelRoom = hotelRoom;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public HotelAdditionalService getAdditionalService() {
		return additionalService;
	}

	public void setAdditionalService(HotelAdditionalService additionalService) {
		this.additionalService = additionalService;
	}

	public FlightReservation getFlightReservation() {
		return flightReservation;
	}

	public void setFlightReservation(FlightReservation flightReservation) {
		this.flightReservation = flightReservation;
	}

	private static final long serialVersionUID = 2735432254411939871L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
