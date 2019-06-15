package isamrs.tim1.controller;

import java.util.ArrayList;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.VehicleDTO;
import isamrs.tim1.dto.VehicleSearchDTO;
import isamrs.tim1.model.FuelType;
import isamrs.tim1.model.VehicleType;
import isamrs.tim1.service.VehicleService;

@RestController
public class VehicleController {
	@Autowired
	private VehicleService vehicleService;

	@PreAuthorize("hasRole('RENTADMIN') or hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getVehicleTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VehicleType[]> getVehicleTypes() {
		return new ResponseEntity<VehicleType[]>(VehicleType.values(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN') or hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getFuelTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FuelType[]> getFuelTypes() {
		return new ResponseEntity<FuelType[]>(FuelType.values(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN') or hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getVehicles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<VehicleDTO>> getAllVehicles() {
		return new ResponseEntity<ArrayList<VehicleDTO>>(vehicleService.getAllVehicles(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/searchVehicles", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<VehicleDTO>> searchVehicles(@RequestBody VehicleSearchDTO searchFields) {
		return new ResponseEntity<ArrayList<VehicleDTO>>(vehicleService.searchVehicles(searchFields), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/addVehicle/{quantity}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addVehicle(@RequestBody @Valid VehicleDTO vehicle,
			@PathVariable("quantity") int quantity) {
		return new ResponseEntity<MessageDTO>(vehicleService.addVehicle(vehicle, quantity), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/editVehicle/{producer}/{model}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> editVehicle(@RequestBody @Valid VehicleDTO vehicle,
			@PathVariable("producer") String producer, @PathVariable("model") String model) {
		return new ResponseEntity<MessageDTO>(vehicleService.editVehicle(producer, model, vehicle), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/deleteVehicle/{producer}/{model}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> deleteVehicle(@PathVariable("producer") String producer,
			@PathVariable("model") String model) {
		return new ResponseEntity<MessageDTO>(vehicleService.deleteVehicle(producer, model), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getVehicleProducers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<String>> getAllProducers() {
		return new ResponseEntity<ArrayList<String>>(vehicleService.getAllProducers(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getModels/{producer}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<String>> getModelsForProducer(@PathVariable("producer") String producer) {
		return new ResponseEntity<ArrayList<String>>(vehicleService.getModelsForProducer(producer), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getBranchOfficesForVehicle/{vehicle}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<String>> getBranchOfficesForVehicle(@PathVariable("vehicle") Integer id) {
		return new ResponseEntity<Set<String>>(vehicleService.getBranchOfficesForVehicle(id), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/checkCountry/{branch}/{vehicle}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> checkCountry(@PathVariable("branch") String branch,
			@PathVariable("vehicle") Integer vehicle) {
		return new ResponseEntity<String>(vehicleService.checkCountry(branch, vehicle), HttpStatus.OK);
	}
}
