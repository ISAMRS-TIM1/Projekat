package isamrs.tim1.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle implements Serializable {

	private static final long serialVersionUID = 2140092751859938725L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "vehicle_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "model", unique = false, nullable = false)
	private String model;

	@Column(name = "producer", unique = false, nullable = false)
	private String producer;

	@Column(name = "yearOfProduction", unique = false, nullable = false)
	private String yearOfProduction;

	@Column(name = "numberOfSeats", unique = false, nullable = false)
	private Integer numberOfSeats;

	@Column(name = "fuelType", unique = false, nullable = false)
	@Enumerated(EnumType.STRING)
	private FuelType fuelType;

	@Column(name = "averageGrade", unique = false, nullable = false)
	private Double averageGrade;

	@Column(name = "pricePerDay", unique = false, nullable = false)
	private Integer pricePerDay;

	@Column(name = "vehicleType", unique = false, nullable = false)
	@Enumerated(EnumType.STRING)
	private VehicleType vehicleType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rentACar")
	private RentACar rentACar;

	@OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<QuickVehicleReservation> quickReservations;

	@OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<VehicleReservation> normalReservations;
	

	public Vehicle() {
		super();
		quickReservations = new HashSet<QuickVehicleReservation>();
		normalReservations = new HashSet<VehicleReservation>();
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getYearOfProduction() {
		return yearOfProduction;
	}

	public void setYearOfProduction(String yearOfProduction) {
		this.yearOfProduction = yearOfProduction;
	}

	public Integer getNumberOfSeats() {
		return numberOfSeats;
	}

	public void setNumberOfSeats(Integer numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}

	public FuelType getFuelType() {
		return fuelType;
	}

	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public RentACar getRentACar() {
		return rentACar;
	}

	public void setRentACar(RentACar rentACar) {
		this.rentACar = rentACar;
	}

	public Double getAverageGrade() {
		return averageGrade;
	}

	public void setAverageGrade(Double averageGrade) {
		this.averageGrade = averageGrade;
	}

	public Integer getPricePerDay() {
		return pricePerDay;
	}

	public void setPricePerDay(Integer pricePerDay) {
		this.pricePerDay = pricePerDay;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Set<QuickVehicleReservation> getQuickReservations() {
		return quickReservations;
	}

	public void setQuickReservations(Set<QuickVehicleReservation> quickReservations) {
		this.quickReservations = quickReservations;
	}

	public Set<VehicleReservation> getNormalReservations() {
		return normalReservations;
	}

	public void setNormalReservations(Set<VehicleReservation> normalReservations) {
		this.normalReservations = normalReservations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vehicle other = (Vehicle) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
