package isamrs.tim1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.HotelRoomDetailedDTO;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.service.HotelRoomService;

@RestController
public class HotelRoomController {
	
	@Autowired
	private HotelRoomService hotelRoomService;
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/api/getHotelRoom",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HotelRoomDetailedDTO> getHotelRoom(@RequestParam String roomNumber) {
		return new ResponseEntity<HotelRoomDetailedDTO>(
				hotelRoomService
						.getHotelRoom(roomNumber, ((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel().getId()),
				HttpStatus.OK);
	}
}
