package isamrs.tim1.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.VehicleDTO;
import isamrs.tim1.dto.VehicleSearchDTO;
import isamrs.tim1.model.FuelType;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.model.VehicleType;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.repository.VehicleRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class VehicleService {
	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private RentACarRepository rentACarRepository;

	public ArrayList<VehicleDTO> searchVehicles(VehicleSearchDTO vehicle) {
		ArrayList<String> producers;

		if (vehicle.getProducer().equals("all")) {
			producers = vehicleRepository.getAllProducers();
		} else {
			producers = new ArrayList<String>();
			producers.add(vehicle.getProducer());
		}

		ArrayList<String> models;
		if (vehicle.getModels().isEmpty()) {
			models = vehicleRepository.getAllModels();
		} else {
			models = vehicle.getModels();
		}

		ArrayList<String> vehicleTypes;
		if (vehicle.getVehicleTypes().isEmpty()) {
			vehicleTypes = this.getAllVehicleTypes();
		} else {
			vehicleTypes = vehicle.getVehicleTypes();
		}

		ArrayList<String> fuelTypes;
		if (vehicle.getFuelTypes().isEmpty()) {
			fuelTypes = this.getAllFuelTypes();
		} else {
			fuelTypes = vehicle.getFuelTypes();
		}

		int maxPrice;
		if (vehicle.getPrice() == null) {
			maxPrice = Integer.MAX_VALUE;
		} else {
			maxPrice = vehicle.getPrice().intValue();
		}

		int seats;
		if (vehicle.getSeats() == null) {
			seats = 0;
		} else {
			seats = vehicle.getSeats().intValue();
		}

		int minYear;
		if (vehicle.getStartDate() == null) {
			minYear = 1900;
		} else {
			minYear = vehicle.getStartDate().intValue();
		}

		int maxYear;
		if (vehicle.getEndDate() == null) {
			maxYear = 2050;
		} else {
			maxYear = vehicle.getEndDate().intValue();
		}

		ArrayList<Vehicle> searchResults = vehicleRepository.searchByParameters(producers, models, fuelTypes,
				vehicleTypes, maxPrice, minYear, maxYear, vehicle.getMinGrade(), vehicle.getMaxGrade(), seats);

		ArrayList<VehicleDTO> returnValue = new ArrayList<VehicleDTO>();
		for (Vehicle v : searchResults) {
			returnValue.add(new VehicleDTO(v));
		}

		return returnValue;
	}

	public boolean alreadyExists(String model, String producer) {
		return vehicleRepository.findOneByModelAndProducer(model, producer) != null;
	}

	public ArrayList<VehicleDTO> getAllVehicles() {
		Set<Vehicle> vehicles = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar().getVehicles();

		ArrayList<VehicleDTO> returnValues = new ArrayList<VehicleDTO>();

		for (Vehicle v : vehicles) {
			returnValues.add(new VehicleDTO(v));
		}

		return returnValues;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO addVehicle(VehicleDTO vehicle, int quantity) {
		if (this.alreadyExists(vehicle.getModel(), vehicle.getProducer())) {
			return new MessageDTO("Vehicle already exists", ToasterType.ERROR.toString());
		}

		if (quantity <= 0) {
			return new MessageDTO("Quantity must be greater than zero", ToasterType.ERROR.toString());
		}

		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		Vehicle v = new Vehicle();
		v.setId(null);
		v.setAvailable(quantity);
		v.setAverageGrade(0.0);
		v.setFuelType(vehicle.getFuelType());
		v.setModel(vehicle.getModel());
		v.setReservations(new HashSet<VehicleReservation>());
		v.setNumberOfSeats(vehicle.getNumberOfSeats());
		v.setPricePerDay(vehicle.getPricePerDay());
		v.setProducer(vehicle.getProducer());
		v.setQuantity(quantity);
		v.setVehicleType(vehicle.getVehicleType());
		v.setYearOfProduction(vehicle.getYearOfProduction());
		v.setRentACar(rentACar);

		rentACar.getVehicles().add(v);

		rentACarRepository.save(rentACar);

		return new MessageDTO("Vehicle successfully added.", ToasterType.SUCCESS.toString());
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO editVehicle(String producer, String model, VehicleDTO editedVehicle) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		boolean producerModelChanged = false;
		if (!producer.equals(editedVehicle.getProducer()) || !model.equals(editedVehicle.getModel())) {
			producerModelChanged = true;
		}

		boolean vehicleExists = false;
		Vehicle vehicle = null;
		for (Vehicle v : rentACar.getVehicles()) {
			if (producerModelChanged && v.getModel().equals(editedVehicle.getModel())
					&& v.getProducer().equals(editedVehicle.getProducer())) {
				return new MessageDTO("Producer model pair is already in use.", ToasterType.ERROR.toString());
			}

			if (v.getModel().equals(model) && v.getProducer().equals(producer)) {
				vehicleExists = true;
				vehicle = v;
				break;
			}
		}

		if (!vehicleExists) {
			return new MessageDTO("Vehicle requested for editing does not exist.", ToasterType.ERROR.toString());
		} else if (vehicle.isDeleted()) {
			return new MessageDTO("Vehicle is deleted.", ToasterType.ERROR.toString());
		}

		Date now = new Date();
		boolean activeReservations = false;
		for (VehicleReservation vr : rentACar.getNormalReservations()) {
			if (vr.getVehicle().getId().equals(vehicle.getId()) && vr.getToDate().compareTo(now) > 0) {
				activeReservations = true;
				break;
			}
		}

		if (activeReservations) {
			return new MessageDTO("Vehicle cannot be edited due to active reservations.", ToasterType.ERROR.toString());
		}

		vehicle.setModel(editedVehicle.getModel());
		vehicle.setProducer(editedVehicle.getProducer());
		vehicle.setYearOfProduction(editedVehicle.getYearOfProduction());
		vehicle.setNumberOfSeats(editedVehicle.getNumberOfSeats());
		vehicle.setFuelType(editedVehicle.getFuelType());
		vehicle.setVehicleType(editedVehicle.getVehicleType());
		vehicle.setPricePerDay(editedVehicle.getPricePerDay());
		vehicleRepository.save(vehicle);

		return new MessageDTO("Vehicle successfully edited.", ToasterType.SUCCESS.toString());
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO deleteVehicle(String producer, String model) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		boolean vehicleExists = false;
		Vehicle vehicle = null;
		for (Vehicle v : rentACar.getVehicles()) {
			if (v.getModel().equals(model) && v.getProducer().equals(producer)) {
				vehicleExists = true;
				vehicle = v;
				break;
			}
		}

		if (!vehicleExists) {
			return new MessageDTO("Vehicle requested for deletion does not exist.", ToasterType.ERROR.toString());
		} else if (vehicle.isDeleted()) {
			return new MessageDTO("Vehicle is already deleted.", ToasterType.ERROR.toString());
		}

		Date now = new Date();
		boolean activeReservations = false;
		for (VehicleReservation vr : rentACar.getNormalReservations()) {
			if (vr.getVehicle().getId().equals(vehicle.getId()) && vr.getToDate().compareTo(now) > 0) {
				activeReservations = true;
				break;
			}
		}

		if (activeReservations) {
			return new MessageDTO("Vehicle requested for deletion has active reservations.",
					ToasterType.ERROR.toString());
		}

		vehicle.setDeleted(true);
		vehicleRepository.save(vehicle);
		return new MessageDTO("Vehicle deleted.", ToasterType.SUCCESS.toString());
	}

	public ArrayList<String> getAllProducers() {
		return vehicleRepository.getAllProducers();
	}

	public ArrayList<String> getModelsForProducer(String producer) {
		return vehicleRepository.getModelsForProducer(producer);
	}

	public ArrayList<String> getAllVehicleTypes() {
		VehicleType[] types = VehicleType.class.getEnumConstants();

		ArrayList<String> typesAsStrings = new ArrayList<String>();

		for (VehicleType type : types) {
			typesAsStrings.add(type.toString());
		}

		return typesAsStrings;
	}

	public ArrayList<String> getAllFuelTypes() {
		FuelType[] types = FuelType.class.getEnumConstants();

		ArrayList<String> typesAsStrings = new ArrayList<String>();

		for (FuelType type : types) {
			typesAsStrings.add(type.toString());
		}

		return typesAsStrings;
	}
}
