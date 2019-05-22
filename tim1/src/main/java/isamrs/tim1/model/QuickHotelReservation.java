package isamrs.tim1.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import java.util.HashSet;

import isamrs.tim1.dto.QuickHotelReservationDTO;

@Entity
public class QuickHotelReservation extends HotelReservation {

	private static final long serialVersionUID = -5431545631194865506L;

	@Column(name = "discount", unique = false, nullable = true)
	private Integer discount;

	public QuickHotelReservation() {
		super();
	}

	public QuickHotelReservation(QuickHotelReservationDTO hotelRes, HotelRoom room,
			HashSet<HotelAdditionalService> additionalServices) {
		super();
		this.id = null;
		this.fromDate = hotelRes.getFromDate();
		this.toDate = hotelRes.getToDate();
		this.hotelRoom = room;
		this.additionalServices = additionalServices;
		this.flightReservation = null;
		this.discount = hotelRes.getDiscount();
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
}
