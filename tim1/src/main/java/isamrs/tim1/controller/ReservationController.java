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
import org.springframework.web.bind.annotation.RestController;

import isamrs.tim1.dto.FlightHotelReservationDTO;
import isamrs.tim1.dto.FlightReservationDTO;
import isamrs.tim1.dto.FlightVehicleReservationDTO;
import isamrs.tim1.dto.InvitingReservationDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.QuickHotelReservationDTO;
import isamrs.tim1.dto.QuickVehicleReservationDTO;
import isamrs.tim1.service.ReservationService;

@RestController
public class ReservationController {

	@Autowired
	ReservationService reservationService;

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/reserveFlight", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> reserveFlight(@RequestBody FlightReservationDTO flightRes) {
		return new ResponseEntity<MessageDTO>(reservationService.reserveFlight(flightRes), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/reserveFlightHotel", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> reserveFlightHotel(@RequestBody FlightHotelReservationDTO flightHotelRes) {
		return new ResponseEntity<MessageDTO>(reservationService.reserveFlightHotel(flightHotelRes), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/reserveFlightVehicle", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> reserveFlightVehicle(@RequestBody FlightVehicleReservationDTO flightVehicleRes) {
		return new ResponseEntity<MessageDTO>(reservationService.reserveFlightVehicle(flightVehicleRes), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getReservations", method = RequestMethod.GET)
	public ArrayList<FlightReservationDTO> getReservations() {
		return reservationService.getReservations();
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getInvitingReservations", method = RequestMethod.GET)
	public ArrayList<InvitingReservationDTO> getInvitingReservations() {
		return reservationService.getInvitingReservations();
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/acceptFlightInvitation", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> acceptFlightInvitation(@RequestBody String resID) {
		return reservationService.acceptFlightInvitation(resID);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/declineFlightInvitation", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> declineFlightInvitation(@RequestBody String resID) {
		return reservationService.declineFlightInvitation(resID);
	}

	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(value = "/api/createQuickHotelReservation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> createQuickHotelReservation(QuickHotelReservationDTO hotelRes) {
		return new ResponseEntity<MessageDTO>(reservationService.createQuickHotelReservation(hotelRes), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/createQuickVehicleReservation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> createQuickVehicleReservation(
			@RequestBody QuickVehicleReservationDTO quickReservation) {
		return new ResponseEntity<MessageDTO>(reservationService.createQuickVehicleReservation(quickReservation),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/getQuickVehicleReservations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<QuickVehicleReservationDTO>> getQuickVehicleReservations() {
		return new ResponseEntity<ArrayList<QuickVehicleReservationDTO>>(
				reservationService.getQuickVehicleReservations(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('REGISTEREDUSER')")
	@RequestMapping(value = "/api/getQuickReservationsForVehicle/{vehicleId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<QuickVehicleReservationDTO>> getQuickVehicleReservationsForVehicle(
			@PathVariable("vehicleId") int vehicleId) {
		return new ResponseEntity<ArrayList<QuickVehicleReservationDTO>>(
				reservationService.getQuickVehicleReservationsForVehicle(vehicleId), HttpStatus.OK);
	}
}
