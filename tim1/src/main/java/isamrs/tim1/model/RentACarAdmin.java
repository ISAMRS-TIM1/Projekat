package isamrs.tim1.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class RentACarAdmin extends User implements Serializable {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rentacar")
	private RentACar rentACar;

	public RentACarAdmin() {
		super();
	}

	public RentACar getRentACar() {
		return rentACar;
	}

	public void setRentACar(RentACar rentACar) {
		this.rentACar = rentACar;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 1892679582107777957L;

}
