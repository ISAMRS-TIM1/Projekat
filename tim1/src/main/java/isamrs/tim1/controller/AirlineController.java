package isamrs.tim1.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.AirlineDTO;
import isamrs.tim1.dto.DetailedServiceDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.PlaneSeatsDTO;
import isamrs.tim1.dto.ServiceDTO;
import isamrs.tim1.dto.ServiceViewDTO;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.service.AirlineService;

@RestController
public class AirlineController {

	@Autowired
	private AirlineService airlineService;

	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(value = "/api/addAirline", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addAirline(@RequestBody ServiceDTO airline) {
		return new ResponseEntity<MessageDTO>(airlineService.addAirline(new Airline(airline)), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('AIRADMIN')")
	@RequestMapping(value = "/api/editAirline", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> editAirline(@RequestBody Airline airline,
			@RequestParam(required = true) String oldName) throws Exception {
		return new ResponseEntity<String>(airlineService.editProfile(airline, oldName), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/getAirlineOfAdmin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AirlineDTO> getAirlineOfAdmin() {
		return new ResponseEntity<AirlineDTO>(
				airlineService.getAirlineOfAdmin(
						(AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(value = "/api/getAirlines", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<ServiceViewDTO>> getAirlines() {
		return new ResponseEntity<ArrayList<ServiceViewDTO>>(airlineService.getAirlines(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(value = "/api/getAirline", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DetailedServiceDTO> getAirline(@RequestParam String name) {
		return new ResponseEntity<DetailedServiceDTO>(airlineService.getAirline(name), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/saveSeats", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> saveSeats(@RequestBody String[] savedSeats) {
		AirlineAdmin a = (AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return new ResponseEntity<MessageDTO>(airlineService.saveSeats(savedSeats, a), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/getPlaneSeats", method = RequestMethod.GET)
	public ResponseEntity<PlaneSeatsDTO> getPlaneSeats() {
		return new ResponseEntity<PlaneSeatsDTO>(airlineService.getPlaneSeats(), HttpStatus.OK);
	}
}
