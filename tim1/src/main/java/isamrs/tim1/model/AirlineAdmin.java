package isamrs.tim1.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class AirlineAdmin extends User {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "airline")
	private Airline airline;

	public AirlineAdmin() {
		super();
	}

	public Airline getAirline() {
		return airline;
	}

	public void setAirline(Airline airline) {
		this.airline = airline;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = -8126525417349461517L;

}
