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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.dto.AirlineDTO;
import isamrs.tim1.dto.DetailedServiceDTO;
import isamrs.tim1.dto.FlightDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.ServiceDTO;
import isamrs.tim1.dto.ServiceViewDTO;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.Flight;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.repository.AirlineRepository;
import isamrs.tim1.repository.ServiceRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class AirlineService {

	@Autowired
	private AirlineRepository airlineRepository;

	@Autowired
	private ServiceRepository serviceRepository;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO editAirline(ServiceDTO airline) {
		AirlineAdmin admin = (AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Airline airlineToEdit = admin.getAirline();
		String newName = airline.getName();
		if (!newName.equals(airlineToEdit.getName()))
			if (serviceRepository.findOneByName(newName) != null)
				return new MessageDTO("Name is already in use by some other service.", ToasterType.ERROR.toString());

		if (newName != null) {
			airlineToEdit.setName(newName);
		}

		String newDescription = airline.getDescription();
		if (newDescription != null) {
			airlineToEdit.setDescription(newDescription);
		}
		
		airlineToEdit.getLocation().setLatitude(airline.getLatitude());
		airlineToEdit.getLocation().setLongitude(airline.getLongitude());

		try {
			airlineRepository.save(airlineToEdit);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new MessageDTO("Database error.", ToasterType.ERROR.toString());
		}

		return new MessageDTO("Airline edited successfully", ToasterType.SUCCESS.toString());
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

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
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

	public ArrayList<FlightDTO> getFlights() {
		AirlineAdmin admin = (AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Airline a = airlineRepository.findOneByName(admin.getAirline().getName());
		ArrayList<FlightDTO> flights = new ArrayList<FlightDTO>();
		for (Flight f : a.getFlights()) {
			flights.add(new FlightDTO(f));
		}
		return flights;
	}

	public AirlineDTO getDetailedAirline(String name) {
		return new AirlineDTO(airlineRepository.findOneByName(name));
	}
}
