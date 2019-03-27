package isamrs.tim1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.model.Airline;
import isamrs.tim1.service.AirlineService;

@RestController
public class AirlineController {
	
	@Autowired
	private AirlineService airlineService;
	
	@RequestMapping(
			value = "/api/airlines",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> editUserProfile(
			@RequestBody Airline airline, @RequestParam String name) throws Exception {
		return new ResponseEntity<Boolean>(airlineService.editProfile(airline, name), HttpStatus.OK);
	}
}
