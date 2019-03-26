package isamrs.tim1.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2140092751859938725L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
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
	private FuelType fuelType;

	@Column(name = "vehicleType", unique = false, nullable = false)
	private VehicleType vehicleType;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "rentACar")
	private RentACar rentACar;

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

	public Vehicle() {
		super();
	}

	public RentACar getRentACar() {
		return rentACar;
	}

	public void setRentACar(RentACar rentACar) {
		this.rentACar = rentACar;
	}

}
