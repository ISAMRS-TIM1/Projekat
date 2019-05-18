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
public class QuickVehicleReservation extends VehicleReservation {
	private static final long serialVersionUID = 1180459435781814006L;

	@Column(name = "discount", unique = false, nullable = false)
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
