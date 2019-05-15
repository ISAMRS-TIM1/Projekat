package isamrs.tim1.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "QuickHotelReservation")
public class QuickHotelReservation extends QuickReservation implements Serializable {

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

	private static final long serialVersionUID = -5431545631194865506L;

	public QuickHotelReservation() {
		super();
	}

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

	public HotelAdditionalService getAdditionalService() {
		return additionalService;
	}

	public void setAdditionalService(HotelAdditionalService additionalService) {
		this.additionalService = additionalService;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
