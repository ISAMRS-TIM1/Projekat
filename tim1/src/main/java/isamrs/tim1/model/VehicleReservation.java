package isamrs.tim1.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "VehicleReservations")
public class VehicleReservation extends Reservation {

	
	@Column(name = "fromDate", unique = false, nullable = false)
	private Date fromDate;
	
	@Column(name = "toDate", unique = false, nullable = false)
	private Date toDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle")
	private Vehicle vehicle;
	
	
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



	public Vehicle getVehicle() {
		return vehicle;
	}



	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	private static final long serialVersionUID = -4050333346528736714L;

}
