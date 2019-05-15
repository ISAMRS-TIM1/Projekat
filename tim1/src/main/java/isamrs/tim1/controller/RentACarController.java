package isamrs.tim1.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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

import isamrs.tim1.dto.BranchOfficeDTO;
import isamrs.tim1.dto.DetailedServiceDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.QuickVehicleReservationDTO;
import isamrs.tim1.dto.RentACarDTO;
import isamrs.tim1.dto.ServiceDTO;
import isamrs.tim1.dto.ServiceViewDTO;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.service.RentACarService;

@RestController
public class RentACarController {
	@Autowired
	private RentACarService rentACarService;

	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(value = "/api/addRentACar", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> addRentACar(@RequestBody ServiceDTO rentACar) {
		return new ResponseEntity<MessageDTO>(rentACarService.addRentACar(new RentACar(rentACar)), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/editRentACar", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> editRentACarProfile(@RequestBody RentACar rentACar,
			@RequestParam(required = true) String oldName) {
		return new ResponseEntity<String>(rentACarService.editProfile(rentACar, oldName), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN') or hasRole('SYSADMIN')")
	@RequestMapping(value = "/api/getRentACarInfo", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, params = "rentACarName")
	public ResponseEntity<RentACarDTO> getRentACarInfo(@PathVariable String rentACarName) {
		return new ResponseEntity<RentACarDTO>(rentACarService.getRentACarInfo(rentACarName), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/getRentACarInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RentACarDTO> getRentACar() {
		RentACarAdmin admin = (RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return new ResponseEntity<RentACarDTO>(new RentACarDTO(admin.getRentACar()), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(value = "/api/getRentACars", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<ServiceViewDTO>> getRentACars() {
		return new ResponseEntity<ArrayList<ServiceViewDTO>>(rentACarService.getRentACars(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('SYSADMIN') or hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/getRentACar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DetailedServiceDTO> getRentACar(@RequestParam String name) {
		return new ResponseEntity<DetailedServiceDTO>(rentACarService.getRentACar(name), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/getBranchOffices", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<BranchOfficeDTO>> getBranchOffices() {
		return new ResponseEntity<ArrayList<BranchOfficeDTO>>(rentACarService.getBranchOffices(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/getDailyGraphData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Long>> getDailyGraphData() {
		return new ResponseEntity<Map<String, Long>>(rentACarService.getDailyGraphData(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/getWeeklyGraphData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Long>> getWeeklyGraphData() {
		return new ResponseEntity<Map<String, Long>>(rentACarService.getWeeklyGraphData(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/getMonthlyGraphData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Long>> getMonthlyGraphData() {
		return new ResponseEntity<Map<String, Long>>(rentACarService.getMonthlyGraphData(), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/getIncomeOfRentACar", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Double> getIncomeOfRentACar(@RequestParam Date fromDate, @RequestParam Date toDate) {
		return new ResponseEntity<Double>(rentACarService.getIncomeOfRentACar(fromDate, toDate), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/createQuickVehicleReservation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageDTO> createQuickVehicleReservation(
			@RequestBody QuickVehicleReservationDTO quickReservation) {
		return new ResponseEntity<MessageDTO>(rentACarService.createQuickVehicleReservation(quickReservation),
				HttpStatus.OK);
	}

	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(value = "/api/getQuickVehicleReservations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<QuickVehicleReservationDTO>> getQuickVehicleReservations() {
		return new ResponseEntity<ArrayList<QuickVehicleReservationDTO>>(rentACarService.getQuickVehicleReservations(),
				HttpStatus.OK);
	}

}
