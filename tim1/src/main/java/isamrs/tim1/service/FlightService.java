package isamrs.tim1.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.dto.FlightDTO;
import isamrs.tim1.dto.FlightUserViewDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.PlaneSeatsDTO;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.Flight;
import isamrs.tim1.model.PlaneSegment;
import isamrs.tim1.model.PlaneSegmentClass;
import isamrs.tim1.model.Seat;
import isamrs.tim1.repository.DestinationRepository;
import isamrs.tim1.repository.FlightRepository;
import isamrs.tim1.repository.SeatRepository;
import isamrs.tim1.repository.ServiceRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class FlightService {

	@Autowired
	ServiceRepository serviceRepository;
	
	@Autowired
	DestinationRepository destinationRepository;
	
	@Autowired
	FlightRepository flightRepository;
	
	@Autowired
	SeatRepository seatRepository;
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<String> addFlight(FlightDTO flightDTO) {
		Airline a = (Airline) serviceRepository.findOneByName(flightDTO.getAirlineName());
		if (a == null)
			return new ResponseEntity<String>("Airline does not exist.", HttpStatus.BAD_REQUEST);
		Flight flight = new Flight();
		Random rand = new Random();
		int n;
		String flightCode;
		while(true) {
			n = rand.nextInt(101);
			flightCode = flightDTO.getStartDestination().substring(0, 2).toUpperCase() + 
					flightDTO.getEndDestination().substring(0, 2).toUpperCase() + n;
			if (flightRepository.findOneByFlightCode(flightCode) == null) {
				break;
			}
		}
		flight.setFlightCode(flightCode);
		PlaneSegment f = new PlaneSegment(PlaneSegmentClass.FIRST);
		PlaneSegment b = new PlaneSegment(PlaneSegmentClass.BUSINESS);
		PlaneSegment ec = new PlaneSegment(PlaneSegmentClass.ECONOMY);
		f.setFlight(flight);
		b.setFlight(flight);
		ec.setFlight(flight);
		flight.getPlaneSegments().add(f);
		flight.getPlaneSegments().add(b);
		flight.getPlaneSegments().add(ec);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		try {
			flight.setDepartureTime(sdf.parse(flightDTO.getDepartureTime()));
			flight.setLandingTime(sdf.parse(flightDTO.getLandingTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		flight.setStartDestination(destinationRepository.findOneByName(flightDTO.getStartDestination()));
		flight.setEndDestination(destinationRepository.findOneByName(flightDTO.getEndDestination()));
		flight.setFlightLength(flightDTO.getFlightDistance());
		long diffInMillies = Math.abs(flight.getDepartureTime().getTime() - flight.getLandingTime().getTime());
		int flightDuration = (int) TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
		flight.setFlightDuration(flightDuration);
		flight.setFirstClassPrice(flightDTO.getFirstClassPrice());
		flight.setBusinessClassPrice(flightDTO.getBusinessClassPrice());
		flight.setEconomyClassPrice(flightDTO.getEconomyClassPrice());
		flight.setPricePerBag(flightDTO.getPricePerBag());
		flight.setNumberOfFlightConnections(flightDTO.getConnections().length);
		flight.setLocationsOfConnecting(new ArrayList<String>(Arrays.asList(flightDTO.getConnections())));
		flight.setAverageGrade(0.0);
		if (flightDTO.isRoundTrip()) {
			flight.setRoundTrip(true);
			try {
				flight.setReturningDepartureTime(sdf.parse(flightDTO.getReturningDepartureTime()));
				flight.setReturningLandingTime(sdf.parse(flightDTO.getReturningLandingTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else {
			flight.setRoundTrip(false);
		}
		flight.setAirline(a);
		a.getFlights().add(flight);
		flightRepository.save(flight);
		return new ResponseEntity<String>(flightCode, HttpStatus.OK);
	}

	public ArrayList<FlightUserViewDTO> searchFlights(FlightDTO flight) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		Date t = null;
		try {
			d = sdf.parse(flight.getDepartureTime());
			t = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		if (d.compareTo(t) < 0) {
			return null;
		}
		Long startID = destinationRepository.findOneByName(flight.getStartDestination()).getId();
		Long endID = destinationRepository.findOneByName(flight.getEndDestination()).getId();
		boolean roundTrip = flight.isRoundTrip();
		boolean multiCity = flight.isMultiCity();
		Set<Flight> flights = null;
		try {
			flights = flightRepository.searchFlights(startID, 
					endID, sdf.parse(flight.getDepartureTime()), roundTrip);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (multiCity) {
			flights.removeIf(f -> {
				if (f.getNumberOfFlightConnections() > 0) {
					return false;
				}
				return true;
			});
		}
		if (roundTrip) {
			flights.removeIf(f -> {
				if (flight.getReturningDepartureTime().equals(sdf.format(f.getReturningDepartureTime()))) {
					return false;
				}
				return true;
			});
		}
		ArrayList<FlightUserViewDTO> flightsList = new ArrayList<FlightUserViewDTO>();
		for (Flight f : flights) {
			flightsList.add(new FlightUserViewDTO(f));
		}
		return flightsList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO saveSeats(PlaneSeatsDTO seats) {
		Flight f = flightRepository.findOneByFlightCode(seats.getFlightCode());
		if (f == null) {
			return new MessageDTO("Flight does not exist.", ToasterType.ERROR.toString());
		}
		Set<PlaneSegment> planeSegments = f.getPlaneSegments();
		for (PlaneSegment p : planeSegments) {
			if (p.getSeats().size() == 0) {
				for (String s : seats.getSavedSeats()) {
					String[] idx = s.split("_");
					if (idx[2].equalsIgnoreCase(p.getSegmentClass().toString().substring(0, 1))) {
						Seat st = new Seat();
						st.setRow(Integer.parseInt(idx[0]));
						st.setColumn(Integer.parseInt(idx[1]));
						st.setPlaneSegment(p);
						st.setPassengerSeat(null);
						p.getSeats().add(st);
					}
				}
			}
		}
		for (String s : seats.getSavedSeats()) {
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
					st.setPassengerSeat(null);
					p.getSeats().add(st);
				}
			}
		}
		ArrayList<Seat> seatForDelete = new ArrayList<Seat>();
		Iterator<Seat> it;
		Seat seat;
		List<String> st;
		String ste;
		for (PlaneSegment p : planeSegments) {
			it = p.getSeats().iterator();
			while (it.hasNext()) {
				seat = it.next();
				if (seat.getPassengerSeat() != null) {
					continue;
				}
				st = Arrays.asList(seats.getSavedSeats());
				ste = seat.getRow() + "_" + seat.getColumn() + "_"
						+ p.getSegmentClass().toString().toLowerCase().charAt(0);
				if (!(st.contains(ste))) {
					seatForDelete.add(seat);
					it.remove();
				}
			}
		}
		f.setPlaneSegments(planeSegments);
		flightRepository.save(f);
		for (Seat s : seatForDelete) {
			seatRepository.delete(s);
		}
		return new MessageDTO("Seats saved successfully", ToasterType.SUCCESS.toString());
	}
	
	public PlaneSeatsDTO getPlaneSeats(String flightCode) {
		Flight f = flightRepository.findOneByFlightCode(flightCode);
		return new PlaneSeatsDTO(f);
	}

	public FlightDTO getDetailedFlight(String flightCode) {
		Flight f = flightRepository.findOneByFlightCode(flightCode);
		return new FlightDTO(f);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO editFlight(FlightDTO flightDTO) {
		Flight flight = flightRepository.findOneByFlightCode(flightDTO.getFlightCode());
		if (flight == null) {
			return new MessageDTO("Flight does not exist.", ToasterType.ERROR.toString());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		try {
			flight.setDepartureTime(sdf.parse(flightDTO.getDepartureTime()));
			flight.setLandingTime(sdf.parse(flightDTO.getLandingTime()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		flight.setStartDestination(destinationRepository.findOneByName(flightDTO.getStartDestination()));
		flight.setEndDestination(destinationRepository.findOneByName(flightDTO.getEndDestination()));
		flight.setFlightLength(flightDTO.getFlightDistance());
		long diffInMillies = Math.abs(flight.getDepartureTime().getTime() - flight.getLandingTime().getTime());
		int flightDuration = (int) TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
		flight.setFlightDuration(flightDuration);
		flight.setFirstClassPrice(flightDTO.getFirstClassPrice());
		flight.setBusinessClassPrice(flightDTO.getBusinessClassPrice());
		flight.setEconomyClassPrice(flightDTO.getEconomyClassPrice());
		flight.setPricePerBag(flightDTO.getPricePerBag());
		flight.setNumberOfFlightConnections(flightDTO.getConnections().length);
		flight.setLocationsOfConnecting(new ArrayList<String>(Arrays.asList(flightDTO.getConnections())));
		if (flightDTO.isRoundTrip()) {
			flight.setRoundTrip(true);
			try {
				flight.setReturningDepartureTime(sdf.parse(flightDTO.getReturningDepartureTime()));
				flight.setReturningLandingTime(sdf.parse(flightDTO.getReturningLandingTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else {
			flight.setRoundTrip(false);
		}
		flightRepository.save(flight);
		return new MessageDTO("Flight successfully edited.", ToasterType.SUCCESS.toString());
	}
}
