package isamrs.tim1.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class QuickVehicleReservation extends VehicleReservation {
	private static final long serialVersionUID = 1180459435781814006L;

	@Column(name = "discount", unique = false, nullable = true)
	private Integer discount;

	public QuickVehicleReservation() {
		super();
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
}
