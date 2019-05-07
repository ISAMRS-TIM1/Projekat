package isamrs.tim1.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import isamrs.tim1.dto.PlaneSeatsDTO;
import isamrs.tim1.dto.ServiceViewDTO;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.Location;
import isamrs.tim1.model.PlaneSegment;
import isamrs.tim1.model.QuickFlightReservation;
import isamrs.tim1.model.Reservation;
import isamrs.tim1.model.Seat;
import isamrs.tim1.repository.AirlineRepository;
import isamrs.tim1.repository.SeatRepository;
import isamrs.tim1.repository.ServiceRepository;

@Service
public class AirlineService {

	@Autowired
	private AirlineRepository airlineRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private ServiceRepository serviceRepository;

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

	public MessageDTO saveSeats(String[] savedSeats, AirlineAdmin a) {
		Set<PlaneSegment> planeSegments = a.getAirline().getPlaneSegments();
		for (PlaneSegment p : planeSegments) {
			if (p.getSeats().size() == 0) {
				for (String s : savedSeats) {
					String[] idx = s.split("_");
					if (idx[2].equalsIgnoreCase(p.getSegmentClass().toString().substring(0, 1))) {
						Seat st = new Seat();
						st.setRow(Integer.parseInt(idx[0]));
						st.setColumn(Integer.parseInt(idx[1]));
						st.setPlaneSegment(p);
						p.getSeats().add(st);
					}
				}
			}
		}
		for (String s : savedSeats) {
			String[] idx = s.split("_");
			int row = Integer.parseInt(idx[0]);
			int column = Integer.parseInt(idx[1]);
			for (PlaneSegment p : planeSegments) {
				if (idx[2].equalsIgnoreCase(p.getSegmentClass().toString().substring(0, 1))
						&& (!p.checkSeatExistence(row, column))) {
					Seat st = new Seat();
					st.setRow(row);
					st.setColumn(column);
					st.setPlaneSegment(p);
					p.getSeats().add(st);
				}
			}
		}
		ArrayList<Seat> seatForDelete = new ArrayList<Seat>();
		for (PlaneSegment p : planeSegments) {
			Iterator<Seat> it = p.getSeats().iterator();
			while (it.hasNext()) {
				Seat s = it.next();
				List<String> st = Arrays.asList(savedSeats);
				String ste = s.getRow() + "_" + s.getColumn() + "_"
						+ p.getSegmentClass().toString().toLowerCase().charAt(0);
				if (!(st.contains(ste))) {
					seatForDelete.add(s);
					it.remove();
				}
			}
		}
		a.getAirline().setPlaneSegments(planeSegments);
		airlineRepository.save(a.getAirline());
		for (Seat s : seatForDelete) {
			seatRepository.delete(s);
		}
		return new MessageDTO("Seats saved successfully", ToasterType.SUCCESS.toString());
	}

	public PlaneSeatsDTO getPlaneSeats() {
		Airline a = airlineRepository.findOneByName("AirSerbia");
		return new PlaneSeatsDTO(a);
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
		for (Reservation r : airline.getNormalReservations()) {
			if (!(r.getDateOfReservation().before(startDate)) && !(r.getDateOfReservation().after(endDate)) && r.isDone()) {
				income += r.getPrice();
			}
		}
		for (Reservation r : airline.getQuickReservations()) {
			if (!(r.getDateOfReservation().before(startDate)) && !(r.getDateOfReservation().after(endDate)) && r.isDone()) {
				income += r.getPrice();
			}
		}
		return new ResponseEntity<Double>(income, HttpStatus.OK);
	}
	
	public Map<String, Long> getDailyGraphData() {
		Airline airline = ((AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getAirline();

		ArrayList<FlightReservation> doneNormalReservations = new ArrayList<FlightReservation>();
		
		
		for (FlightReservation fr : airline.getNormalReservations()) {
			if (fr.isDone()) {
				doneNormalReservations.add(fr);
			}
		}

		ArrayList<QuickFlightReservation> doneQuickReservations = new ArrayList<QuickFlightReservation>();

		for (QuickFlightReservation qr : airline.getQuickReservations()) {
			if (qr.isDone()) {
				doneQuickReservations.add(qr);
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Long> normalCounts = doneNormalReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));
		Map<String, Long> quickCounts = doneQuickReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));

		Map<String, Long> returnValue = new HashMap<String, Long>(normalCounts);
		quickCounts.forEach((key, value) -> returnValue.merge(key, value, (v1, v2) -> v1 + v2));

		return returnValue;
	}

	public Map<String, Long> getWeeklyGraphData() {
		Airline airline = ((AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getAirline();

		ArrayList<FlightReservation> doneNormalReservations = new ArrayList<FlightReservation>();
		
		for (FlightReservation fr : airline.getNormalReservations()) {
			if (fr.isDone()) {
				doneNormalReservations.add(fr);
			}
		}

		ArrayList<QuickFlightReservation> doneQuickReservations = new ArrayList<QuickFlightReservation>();

		for (QuickFlightReservation qr : airline.getQuickReservations()) {
			if (qr.isDone()) {
				doneQuickReservations.add(qr);
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy 'week: 'W");

		Map<String, Long> normalCounts = doneNormalReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));
		Map<String, Long> quickCounts = doneQuickReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));

		Map<String, Long> returnValue = new HashMap<String, Long>(normalCounts);
		quickCounts.forEach((key, value) -> returnValue.merge(key, value, (v1, v2) -> v1 + v2));

		return returnValue;
	}

	public Map<String, Long> getMonthlyGraphData() {
		Airline airline = ((AirlineAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getAirline();

		ArrayList<FlightReservation> doneNormalReservations = new ArrayList<FlightReservation>();
		
		for (FlightReservation fr : airline.getNormalReservations()) {
			if (fr.isDone()) {
				doneNormalReservations.add(fr);
			}
		}

		ArrayList<QuickFlightReservation> doneQuickReservations = new ArrayList<QuickFlightReservation>();

		for (QuickFlightReservation qr : airline.getQuickReservations()) {
			if (qr.isDone()) {
				doneQuickReservations.add(qr);
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

		Map<String, Long> normalCounts = doneNormalReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));
		Map<String, Long> quickCounts = doneQuickReservations.stream()
				.collect(Collectors.groupingBy(r -> sdf.format(r.getDateOfReservation()), Collectors.counting()));

		Map<String, Long> returnValue = new HashMap<String, Long>(normalCounts);
		quickCounts.forEach((key, value) -> returnValue.merge(key, value, (v1, v2) -> v1 + v2));

		return returnValue;

	}
}
