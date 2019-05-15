package isamrs.tim1.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
public class FlightService {

	@Autowired
	ServiceRepository serviceRepository;
	
	@Autowired
	DestinationRepository destinationRepository;
	
	@Autowired
	FlightRepository flightRepository;
	
	@Autowired
	SeatRepository seatRepository;
	
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
		flight.setAirline(a);
		a.getFlights().add(flight);
		flightRepository.save(flight);
		return new ResponseEntity<String>(flightCode, HttpStatus.OK);
	}

	public ArrayList<FlightUserViewDTO> searchFlights(FlightDTO flight) {
		Long startID = destinationRepository.findOneByName(flight.getStartDestination()).getId();
		Long endID = destinationRepository.findOneByName(flight.getEndDestination()).getId();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Set<Flight> flights = flightRepository.searchFlightsByDestinations(startID, endID);
		ArrayList<FlightUserViewDTO> flightsList = new ArrayList<FlightUserViewDTO>();
		for (Flight f : flights) {
			if (flight.getDepartureTime().equals(sdf.format(f.getDepartureTime())) && 
					flight.getLandingTime().equals(sdf.format(f.getLandingTime()))) {
				flightsList.add(new FlightUserViewDTO(f));
			}
		}
		return flightsList;
	}
	
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
					p.getSeats().add(st);
				}
			}
		}
		ArrayList<Seat> seatForDelete = new ArrayList<Seat>();
		for (PlaneSegment p : planeSegments) {
			Iterator<Seat> it = p.getSeats().iterator();
			while (it.hasNext()) {
				Seat s = it.next();
				List<String> st = Arrays.asList(seats.getSavedSeats());
				String ste = s.getRow() + "_" + s.getColumn() + "_"
						+ p.getSegmentClass().toString().toLowerCase().charAt(0);
				if (!(st.contains(ste))) {
					seatForDelete.add(s);
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
}
