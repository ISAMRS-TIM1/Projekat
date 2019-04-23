package isamrs.tim1.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.VehicleDTO;
import isamrs.tim1.model.FuelType;
import isamrs.tim1.model.QuickVehicleReservation;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.model.VehicleType;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.service.VehicleService;

@RestController
public class VehicleController {
	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private RentACarRepository rentACarRepository;

	@RequestMapping(value = "/api/getVehicleTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VehicleType[]> getVehicleTypes() {
		return new ResponseEntity<VehicleType[]>(VehicleType.values(), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/getFuelTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FuelType[]> getFuelTypes() {
		return new ResponseEntity<FuelType[]>(FuelType.values(), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/getVehicles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<VehicleDTO>> getAllVehicles() {
		Set<Vehicle> vehicles = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar().getVehicles();

		ArrayList<VehicleDTO> returnValues = new ArrayList<VehicleDTO>();

		for (Vehicle v : vehicles) {
			returnValues.add(new VehicleDTO(v));
		}

		return new ResponseEntity<ArrayList<VehicleDTO>>(returnValues, HttpStatus.OK);
	}

	@RequestMapping(value = "/api/searchVehicles", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<VehicleDTO>> searchVehicles(@RequestBody Vehicle vehicle) {
		return new ResponseEntity<ArrayList<VehicleDTO>>(vehicleService.searchVehicles(vehicle), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/addVehicle/{quantity}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addVehicle(@RequestBody @Valid VehicleDTO vehicle,
			@PathVariable("quantity") int quantity) {
		if (vehicleService.alreadyExists(vehicle.getModel(), vehicle.getProducer())) {
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Vehicle already exists", ToasterType.ERROR.toString()), HttpStatus.OK);
		}

		if (quantity <= 0) {
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Quantity must be greater than zero", ToasterType.ERROR.toString()), HttpStatus.OK);
		}

		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		Vehicle v = new Vehicle();
		v.setId(null);
		v.setAvailable(quantity);
		v.setAverageGrade(0.0);
		v.setFuelType(vehicle.getFuelType());
		v.setModel(v.getModel());
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

		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Vehicle successfully added.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}
}
