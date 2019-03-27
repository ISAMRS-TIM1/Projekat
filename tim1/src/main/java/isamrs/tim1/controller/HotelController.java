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

import isamrs.tim1.model.Hotel;
import isamrs.tim1.service.HotelService;

@RestController
public class HotelController {
	
	@Autowired
	private HotelService hotelService;
	
	@RequestMapping(
			value = "/api/addHotel",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> addHotel(
			@RequestBody Hotel hotel) {
		return new ResponseEntity<Boolean>(hotelService.addHotel(hotel), HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/api/editHotel",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> editHotel(
			@RequestBody Hotel hotel, @RequestParam(required = true) String oldName) {
		return new ResponseEntity<String>(hotelService.editHotel(hotel, oldName), HttpStatus.OK);
	}
}
