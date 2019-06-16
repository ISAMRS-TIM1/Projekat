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
import javax.persistence.Version;

@Entity
@Table(name = "VehicleReservations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class VehicleReservation implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "reservation_id", unique = true, nullable = false)
	protected Long id;

	@Column(name = "fromDate", unique = false, nullable = false)
	protected Date fromDate;

	@Column(name = "toDate", unique = false, nullable = false)
	protected Date toDate;

	@Column(name = "done", unique = false, nullable = false)
	protected Boolean done = false;

	@Column(name = "grade", unique = false, nullable = true)
	protected Integer grade = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle")
	protected Vehicle vehicle;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branchOffice")
	protected BranchOffice branchOffice;

	@OneToOne(mappedBy = "vehicleReservation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	protected FlightReservation flightReservation;

	@Column(name = "price", unique = false, nullable = true)
	protected Double price;
	
	@Version
	private Integer version;

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

	public Boolean getDone() {
		return done;
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public FlightReservation getFlightReservation() {
		return flightReservation;
	}

	public void setFlightReservation(FlightReservation flightReservation) {
		this.flightReservation = flightReservation;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	private static final long serialVersionUID = 4730544312048644658L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
