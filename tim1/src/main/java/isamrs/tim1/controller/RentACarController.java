package isamrs.tim1.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.DetailedServiceDTO;
import isamrs.tim1.dto.RentACarDTO;
import isamrs.tim1.dto.ServiceDTO;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.service.RentACarService;

@RestController
public class RentACarController {
	@Autowired
	private RentACarService rentACarService;

	@RequestMapping(value = "/api/editRentACar", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> editRentACarProfile(@RequestBody RentACar rentACar,
			@RequestParam(required = true) String oldName) {
		return new ResponseEntity<String>(rentACarService.editProfile(rentACar, oldName), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/getRentACarInfo", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RentACarDTO> getRentACarInfo(@PathVariable String rentACarName) {
		return new ResponseEntity<RentACarDTO>(rentACarService.getRentACarInfo(rentACarName), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(value = "/api/getRentACars", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<ServiceDTO>> getRentACars() {
		return new ResponseEntity<ArrayList<ServiceDTO>>(rentACarService.getRentACars(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(value = "/api/getRentACar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DetailedServiceDTO> getRentACar(@RequestParam String name) {
		return new ResponseEntity<DetailedServiceDTO>(rentACarService.getRentACar(name), HttpStatus.OK);
	}
}
