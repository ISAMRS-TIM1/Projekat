package isamrs.tim1.controller;

import java.util.ArrayList;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.VehicleDTO;
import isamrs.tim1.model.FuelType;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleType;
import isamrs.tim1.service.VehicleService;

@RestController
public class VehicleController {
	@Autowired
	private VehicleService vehicleService;

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
		return new ResponseEntity<ArrayList<VehicleDTO>>(vehicleService.getAllVehicles(), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/searchVehicles", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<VehicleDTO>> searchVehicles(@RequestBody Vehicle vehicle) {
		return new ResponseEntity<ArrayList<VehicleDTO>>(vehicleService.searchVehicles(vehicle), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/addVehicle/{quantity}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addVehicle(@RequestBody @Valid VehicleDTO vehicle,
			@PathVariable("quantity") int quantity) {
		return new ResponseEntity<MessageDTO>(vehicleService.addVehicle(vehicle, quantity), HttpStatus.OK);
	}
}
