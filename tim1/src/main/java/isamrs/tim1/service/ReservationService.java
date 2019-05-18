package isamrs.tim1.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.FlightHotelReservationDTO;
import isamrs.tim1.dto.FlightReservationDTO;
import isamrs.tim1.dto.HotelReservationDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.PassengerDTO;
import isamrs.tim1.model.Flight;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.PassengerSeat;
import isamrs.tim1.model.PlaneSegment;
import isamrs.tim1.model.PlaneSegmentClass;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.Seat;
import isamrs.tim1.model.UserReservation;
import isamrs.tim1.repository.FlightRepository;
import isamrs.tim1.repository.ReservationRepository;
import isamrs.tim1.repository.ServiceRepository;
import isamrs.tim1.repository.UserRepository;
import isamrs.tim1.repository.UserReservationRepository;

@Service
public class ReservationService {

	@Autowired
	ReservationRepository reservationRepository;

	@Autowired
	FlightRepository flightRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserReservationRepository userReservationRepository;

	@Autowired
	ServiceRepository serviceRepository;

	public MessageDTO reserveFlight(FlightReservationDTO flightRes) {
		UserReservation ur = new UserReservation();
		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		MessageDTO retval = reserveFlightNoSave(flightRes, ur, fr, ru);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;
		userReservationRepository.save(ur);
		reservationRepository.save(fr);
		userRepository.save(ru);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	public ArrayList<FlightReservationDTO> getReservations() {
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Set<UserReservation> ur = userReservationRepository.getByUser(ru.getId());
		ArrayList<FlightReservationDTO> fr = new ArrayList<FlightReservationDTO>();
		for (UserReservation u : ur) {
			if (u.getReservation() instanceof FlightReservation) {
				FlightReservation flightRes = (FlightReservation) u.getReservation();
				String res = flightRes.getFlight().getStartDestination().getName() + "-"
						+ flightRes.getFlight().getEndDestination().getName();
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
				String date = sdf.format(flightRes.getFlight().getDepartureTime());
				fr.add(new FlightReservationDTO(res, date, flightRes.getPrice(), u.getGrade()));
			}
		}
		return fr;
	}

	public MessageDTO reserveFlightHotel(FlightHotelReservationDTO flightHotelRes) {
		FlightReservationDTO flightRes = flightHotelRes.getFlightReservation();
		HotelReservationDTO hotelRes = flightHotelRes.getHotelReservation();
		UserReservation ur = new UserReservation();
		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		MessageDTO retval = reserveFlightNoSave(flightRes, ur, fr, ru);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;
		
		HotelReservation hr = new HotelReservation();
		retval = reserveHotelNoSave(flightHotelRes, fr, hr);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;
		
		
		
		userReservationRepository.save(ur); // proveri jel radi samo sa jednim save-om, trebalo bi
		reservationRepository.save(fr);
		userRepository.save(ru);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	private MessageDTO reserveFlightNoSave(FlightReservationDTO flightRes, UserReservation ur, FlightReservation fr,
			RegisteredUser ru) {
		Flight f = flightRepository.findOneByFlightCode(flightRes.getFlightCode());
		if (f == null) {
			return new MessageDTO("Flight does not exist.", ToasterType.ERROR.toString());
		}
		fr.setFlight(f);
		fr.setDone(false);
		fr.setDateOfReservation(new Date());
		int counter = 0;
		double price = 0.0;
		String userPassport = flightRes.getPassengers()[0].getPassport();
		int numOfBags = flightRes.getPassengers()[0].getNumberOfBags();
		ArrayList<PassengerDTO> passengerList = new ArrayList<PassengerDTO>(
				Arrays.asList(Arrays.copyOfRange(flightRes.getPassengers(), 1, flightRes.getPassengers().length)));
		passengerList.add(0, new PassengerDTO(ru.getFirstName(), ru.getLastName(), userPassport, numOfBags));
		for (PassengerDTO p : passengerList) {
			String[] idx = flightRes.getSeats()[counter].split("_");
			price += p.getNumberOfBags() * f.getPricePerBag();
			Seat st = new Seat();
			st.setRow(Integer.parseInt(idx[0]));
			st.setColumn(Integer.parseInt(idx[1]));
			if (idx[2].equalsIgnoreCase(PlaneSegmentClass.FIRST.toString().substring(0, 1))) {
				st.setPlaneSegment(new PlaneSegment(PlaneSegmentClass.FIRST));
				price += f.getFirstClassPrice();
			} else if (idx[2].equalsIgnoreCase(PlaneSegmentClass.BUSINESS.toString().substring(0, 1))) {
				st.setPlaneSegment(new PlaneSegment(PlaneSegmentClass.BUSINESS));
				price += f.getBusinessClassPrice();
			} else if (idx[2].equalsIgnoreCase(PlaneSegmentClass.ECONOMY.toString().substring(0, 1))) {
				st.setPlaneSegment(new PlaneSegment(PlaneSegmentClass.ECONOMY));
				price += f.getEconomyClassPrice();
			} else {
				return null;
			}
			PassengerSeat ps = new PassengerSeat(p, st);
			ps.setNormalReservation(fr);
			fr.getPassengerSeats().add(ps);
			counter++;
		}
		fr.setPrice(price);

		ur.setGrade(0);
		ur.setReservation(fr);
		ur.setUser(ru);
		ru.getUserReservations().add(ur);
		fr.setUser(ur);
		f.getAirline().getNormalReservations().add(fr);
		return new MessageDTO("", ToasterType.SUCCESS.toString());
	}
}
