package isamrs.tim1.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.VehicleDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.FuelType;
import isamrs.tim1.model.QuickVehicleReservation;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.model.VehicleType;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.repository.VehicleRepository;

@Service
public class VehicleService {
	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private RentACarRepository rentACarRepository;

	public ArrayList<VehicleDTO> searchVehicles(Vehicle vehicle) {
		String model = vehicle.getModel();

		if (model == null) {
			model = "%";
		}

		String producer = vehicle.getProducer();

		if (producer == null) {
			producer = "%";
		}

		String year = vehicle.getYearOfProduction();

		if (year == null) {
			year = "%";
		}

		Integer num = vehicle.getNumberOfSeats();
		String numStr;
		if (num == null) {
			numStr = "%";
		} else {
			numStr = num.toString();
		}

		FuelType ft = vehicle.getFuelType();
		String ftStr;
		if (ft == null) {
			ftStr = "%";
		} else {
			ftStr = ft.toString();
		}

		VehicleType vt = vehicle.getVehicleType();
		String vtStr;
		if (vt == null) {
			vtStr = "%";
		} else {
			vtStr = vt.toString();
		}

		ArrayList<Vehicle> searchResults = this.vehicleRepository.findByParameters(vehicle.getRentACar().getId(), model,
				producer, year, numStr, ftStr, vtStr);

		ArrayList<VehicleDTO> toBeReturned = new ArrayList<VehicleDTO>();
		for (Vehicle v : searchResults) {
			toBeReturned.add(new VehicleDTO(v));
		}

		return toBeReturned;
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
		v.setNormalReservations(new HashSet<VehicleReservation>());
		v.setNumberOfSeats(vehicle.getNumberOfSeats());
		v.setPricePerDay(vehicle.getPricePerDay());
		v.setProducer(vehicle.getProducer());
		v.setQuantity(quantity);
		v.setQuickReservations(new HashSet<QuickVehicleReservation>());
		v.setVehicleType(vehicle.getVehicleType());
		v.setYearOfProduction(vehicle.getYearOfProduction());
		v.setRentACar(rentACar);

		rentACar.getVehicles().add(v);

		rentACarRepository.save(rentACar);

		return new MessageDTO("Vehicle successfully added.", ToasterType.SUCCESS.toString());
	}

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

		if (!activeReservations) {
			for (QuickVehicleReservation qr : rentACar.getQuickReservations()) {
				if (qr.getVehicle().getId().equals(vehicle.getId()) && qr.getToDate().compareTo(now) > 0) {
					activeReservations = true;
					break;
				}
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
}
