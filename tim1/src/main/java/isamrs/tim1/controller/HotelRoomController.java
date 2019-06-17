package isamrs.tim1.controller;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.HotelRoomDTO;
import isamrs.tim1.dto.HotelRoomDetailedDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.SeasonalPriceDTO;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.service.HotelRoomService;

@RestController
public class HotelRoomController {

	@Autowired
	private HotelRoomService hotelRoomService;

	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(value = "/api/getHotelRoom", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HotelRoomDetailedDTO> getHotelRoom(@RequestParam String roomNumber) {
		return new ResponseEntity<HotelRoomDetailedDTO>(hotelRoomService.getHotelRoom(roomNumber,
				((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(value = "/api/addHotelRoom", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addHotelRoom(@RequestBody HotelRoomDTO hotelRoom) {
		return new ResponseEntity<MessageDTO>(hotelRoomService.addHotelRoom(hotelRoom,
				((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(value = "/api/deleteHotelRoom/{roomNumber}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> deleteHotelRoom(@PathVariable("roomNumber") String roomNumber) {
		return new ResponseEntity<MessageDTO>(hotelRoomService.deleteHotelRoom(roomNumber,
				((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(value = "/api/editHotelRoom/{roomNumber}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> editHotelRoom(@PathVariable("roomNumber") String roomNumber,
			@RequestBody HotelRoomDTO hotelRoom) {
		return new ResponseEntity<MessageDTO>(hotelRoomService.editHotelRoom(hotelRoom, roomNumber,
				((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(value = "/api/addSeasonalPrice/{roomNumber}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addSeasonalPrice(@RequestBody SeasonalPriceDTO seasonalPrice,
			@PathVariable("roomNumber") String roomNumber) {
		return new ResponseEntity<MessageDTO>(hotelRoomService.addSeasonalPrice(seasonalPrice, roomNumber,
				((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel()),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(value = "/api/deleteSeasonalPrice/{roomNumber}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> deleteSeasonalPrice(@RequestBody SeasonalPriceDTO seasonalPrice,
			@PathVariable("roomNumber") String roomNumber) {
		return new ResponseEntity<MessageDTO>(hotelRoomService.deleteSeasonalPrice(seasonalPrice, roomNumber,
				((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel()),
				HttpStatus.OK);
	}

	// everyone
	@RequestMapping(value = "api/searchRooms/{hotel}", method = RequestMethod.GET)
	public ArrayList<HotelRoomDTO> searchRooms(@PathVariable("hotel") String hotel, Date fromDate, Date toDate,
			int forPeople, double fromPrice, double toPrice, double fromGrade, double toGrade) {
		return hotelRoomService.searchRooms(hotel, fromDate, toDate, forPeople, fromPrice, toPrice, fromGrade, toGrade);
	}

	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(value = "api/searchRoomsAdmin", method = RequestMethod.GET)
	public ArrayList<HotelRoomDTO> searchRoomsAdmin(Date fromDate, Date toDate, int forPeople, double fromPrice,
			double toPrice, double fromGrade, double toGrade) {
		return hotelRoomService
				.searchRooms(((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
						.getHotel().getName(), fromDate, toDate, forPeople, fromPrice, toPrice, fromGrade, toGrade);
	}

}
