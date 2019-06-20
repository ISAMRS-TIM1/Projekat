package isamrs.tim1.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import isamrs.tim1.dto.ServiceDTO;

@Entity
@Table(name = "RentACars")
public class RentACar extends Service implements Serializable {

	@OneToMany(mappedBy = "rentACar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<BranchOffice> branchOffices;

	@OneToMany(mappedBy = "rentACar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Vehicle> vehicles;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<VehicleReservation> reservations = new HashSet<VehicleReservation>();

	@OneToMany(mappedBy = "rentACar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<RentACarAdmin> admins;

	private static final long serialVersionUID = 8960219458856435612L;

	public Set<BranchOffice> getBranchOffices() {
		return branchOffices;
	}

	public void setBranchOffices(Set<BranchOffice> branchOffices) {
		this.branchOffices = branchOffices;
	}

	public Set<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(Set<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public RentACar() {
		super();
		branchOffices = new HashSet<BranchOffice>();
		admins = new HashSet<RentACarAdmin>();
		vehicles = new HashSet<Vehicle>();
		reservations = new HashSet<VehicleReservation>();

	}

	public RentACar(ServiceDTO rentACar) {
		super(rentACar);
		branchOffices = new HashSet<BranchOffice>();
		admins = new HashSet<RentACarAdmin>();
		vehicles = new HashSet<Vehicle>();
		reservations = new HashSet<VehicleReservation>();
	}

	public Set<VehicleReservation> getReservations() {
		return reservations;
	}

	public void setReservations(Set<VehicleReservation> reservations) {
		this.reservations = reservations;
	}

	public Set<RentACarAdmin> getAdmins() {
		return admins;
	}

	public void setAdmins(Set<RentACarAdmin> admins) {
		this.admins = admins;
	}

	public Set<VehicleReservation> getNormalReservations() {
		return reservations;
	}

	public void setNormalReservations(Set<VehicleReservation> normalReservations) {
		this.reservations = normalReservations;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
