package isamrs.tim1.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.AirlineDTO;
import isamrs.tim1.dto.DetailedServiceDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.QuickFlightReservationDTO;
import isamrs.tim1.dto.ServiceViewDTO;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.Flight;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.Location;
import isamrs.tim1.model.PassengerSeat;
import isamrs.tim1.model.QuickFlightReservation;
import isamrs.tim1.repository.AirlineRepository;
import isamrs.tim1.repository.FlightRepository;
import isamrs.tim1.repository.ServiceRepository;

@Service
public class AirlineService {

	@Autowired
	private AirlineRepository airlineRepository;

	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private FlightRepository flightRepository;

	public String editProfile(Airline airline, String oldName) {
		Airline airlineToEdit = airlineRepository.findOneByName(oldName);
		if (airlineToEdit == null) {
			return "Airline with given name does not exist.";
		}

		String newName = airline.getName();
		if (serviceRepository.findOneByName(newName) != null)
			return "Name is already in use by some other service.";

		if (newName != null) {
			airlineToEdit.setName(newName);
		}

		String newDescription = airline.getDescription();
		if (newDescription != null) {
			airlineToEdit.setDescription(newDescription);
		}

		Location newLocation = airline.getLocation();
		if (newLocation != null) {
			airlineToEdit.getLocation().setLatitude(airline.getLocation().getLatitude());
			airlineToEdit.getLocation().setLongitude(airline.getLocation().getLongitude());
		}

		try {
			airlineRepository.save(airlineToEdit);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "Database error.";
		}

		return null;
	}

	public AirlineDTO getAirlineOfAdmin(AirlineAdmin admin) {
		return new AirlineDTO(admin.getAirline());
	}

	public ArrayList<ServiceViewDTO> getAirlines() {
		ArrayList<ServiceViewDTO> retval = new ArrayList<ServiceViewDTO>();
		for (Airline a : airlineRepository.findAll())
			retval.add(new ServiceViewDTO(a));
		return retval;
	}

	public DetailedServiceDTO getAirline(String name) {
		return new DetailedServiceDTO(airlineRepository.findOneByName(name));
	}

	public MessageDTO addAirline(Airline airline) {
		if (serviceRepository.findOneByName(airline.getName()) != null)
			return new MessageDTO("Service with the same name already exists.", ToasterType.ERROR.toString());
		airline.setId(null); // to ensure INSERT command
		airlineRepository.save(airline);
		return new MessageDTO("Airline successfully added", ToasterType.SUCCESS.toString());
	}

	public ResponseEntity<Double> getIncomeOfAirline(String fromDate, String toDate) {
		Airline airline = ((AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAirline();
		Double income = 0.0;
		Date startDate = null;
		Date endDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			startDate = sdf.parse(fromDate);
			endDate = sdf.parse(toDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		for (FlightReservation r : airline.getReservations()) {
			if (!(r.getDateOfReservation().before(startDate)) && !(r.getDateOfReservation().after(endDate)) && r.getDone()) {
				income += r.getPrice();
			}
		}
		return new ResponseEntity<Double>(income, HttpStatus.OK);
	}
	
	public Map<String, Long> getDailyGraphData() {
		Airline airline = ((AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getAirline();

		ArrayList<FlightReservation> doneReservations = new ArrayList<FlightReservation>();

		for (FlightReservation fr : airline.getReservations()) {
			if (fr.getDone()) {
				doneReservations.add(fr);
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Long> normalCounts = doneReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));
		Map<String, Long> returnValue = new HashMap<String, Long>(normalCounts);

		return returnValue;
	}

	public Map<String, Long> getWeeklyGraphData() {
		Airline airline = ((AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getAirline();

		ArrayList<FlightReservation> doneReservations = new ArrayList<FlightReservation>();

		for (FlightReservation fr : airline.getReservations()) {
			if (fr.getDone()) {
				doneReservations.add(fr);
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy 'week: 'W");

		Map<String, Long> normalCounts = doneReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));
		Map<String, Long> returnValue = new HashMap<String, Long>(normalCounts);

		return returnValue;
	}

	public Map<String, Long> getMonthlyGraphData() {
		Airline airline = ((AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getAirline();

		ArrayList<FlightReservation> doneReservations = new ArrayList<FlightReservation>();

		for (FlightReservation fr : airline.getReservations()) {
			if (fr.getDone()) {
				doneReservations.add(fr);
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

		Map<String, Long> normalCounts = doneReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));
		Map<String, Long> returnValue = new HashMap<String, Long>(normalCounts);

		return returnValue;

	}
}
