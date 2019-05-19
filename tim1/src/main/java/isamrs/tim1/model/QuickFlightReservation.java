package isamrs.tim1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
public class QuickFlightReservation extends FlightReservation {
	private static final long serialVersionUID = 1439054945103368401L;
	
	@Column(name = "discount", unique = false, nullable = false)
	private Integer discount;

	public QuickFlightReservation() {
		super();
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
}
