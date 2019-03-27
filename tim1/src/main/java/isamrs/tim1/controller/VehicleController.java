package isamrs.tim1.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.model.Vehicle;
import isamrs.tim1.service.VehicleService;

@RestController
public class VehicleController {
	@Autowired
	private VehicleService vehicleService;

	@RequestMapping(
			value = "/api/searchVehicles",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<Vehicle>> searchVehicles(@RequestBody Vehicle vehicle) {
		return new ResponseEntity<ArrayList<Vehicle>>(vehicleService.searchVehicles(vehicle), HttpStatus.OK);
	}
}
