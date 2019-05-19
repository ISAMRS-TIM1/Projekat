package isamrs.tim1.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class QuickHotelReservation extends HotelReservation {

	private static final long serialVersionUID = -5431545631194865506L;

	@Column(name = "discount", unique = false, nullable = true)
	private Integer discount;

	public QuickHotelReservation() {
		super();
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
}
