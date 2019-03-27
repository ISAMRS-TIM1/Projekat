package isamrs.tim1.dto;

import java.io.Serializable;

import isamrs.tim1.model.FuelType;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleType;

public class VehicleDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6347126607915218236L;
	private String model;
	private String producer;
	private String yearOfProduction;
	private Integer numberOfSeats;
	private FuelType fuelType;
	private VehicleType vehicleType;

	public VehicleDTO(Vehicle v) {
		this.model = v.getModel();
		this.producer = v.getProducer();
		this.yearOfProduction = v.getYearOfProduction();
		this.numberOfSeats = v.getNumberOfSeats();
		this.fuelType = v.getFuelType();
		this.vehicleType = v.getVehicleType();
	}

	public VehicleDTO(String model, String producer, String yearOfProduction, Integer numberOfSeats, FuelType fuelType,
			VehicleType vehicleType) {
		super();
		this.model = model;
		this.producer = producer;
		this.yearOfProduction = yearOfProduction;
		this.numberOfSeats = numberOfSeats;
		this.fuelType = fuelType;
		this.vehicleType = vehicleType;
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

}
