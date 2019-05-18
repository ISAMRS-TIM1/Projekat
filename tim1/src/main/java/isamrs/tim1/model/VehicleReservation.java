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
@Table(name = "VehicleReservations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class VehicleReservation implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "reservation_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "fromDate", unique = false, nullable = false)
	private Date fromDate;

	@Column(name = "toDate", unique = false, nullable = false)
	private Date toDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle")
	private Vehicle vehicle;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branchOffice")
	private BranchOffice branchOffice;

	@OneToOne(mappedBy = "vehicleReservation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public BranchOffice getBranchOffice() {
		return branchOffice;
	}

	public void setBranchOffice(BranchOffice branchOffice) {
		this.branchOffice = branchOffice;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FlightReservation getFlightReservation() {
		return flightReservation;
	}

	public void setFlightReservation(FlightReservation flightReservation) {
		this.flightReservation = flightReservation;
	}
	
	private static final long serialVersionUID = 4730544312048644658L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
