package isamrs.tim1.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.dto.FlightHotelReservationDTO;
import isamrs.tim1.dto.FlightHotelVehicleReservationDTO;
import isamrs.tim1.dto.FlightReservationDTO;
import isamrs.tim1.dto.FlightVehicleReservationDTO;
import isamrs.tim1.dto.HotelReservationDTO;
import isamrs.tim1.dto.InvitingReservationDTO;
import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.dto.PassengerDTO;
import isamrs.tim1.dto.QuickFlightReservationDTO;
import isamrs.tim1.dto.QuickHotelReservationDTO;
import isamrs.tim1.dto.QuickVehicleReservationDTO;
import isamrs.tim1.dto.VehicleReservationDTO;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.BranchOffice;
import isamrs.tim1.model.Flight;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelAdditionalService;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.HotelRoom;
import isamrs.tim1.model.PassengerSeat;
import isamrs.tim1.model.PlaneSegment;
import isamrs.tim1.model.PlaneSegmentClass;
import isamrs.tim1.model.QuickFlightReservation;
import isamrs.tim1.model.QuickHotelReservation;
import isamrs.tim1.model.QuickVehicleReservation;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.Seat;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.repository.BranchOfficeRepository;
import isamrs.tim1.repository.FlightRepository;
import isamrs.tim1.repository.FlightReservationRepository;
import isamrs.tim1.repository.HotelAdditionalServicesRepository;
import isamrs.tim1.repository.HotelRoomRepository;
import isamrs.tim1.repository.PassengerSeatRepository;
import isamrs.tim1.repository.QuickFlightReservationRepository;
import isamrs.tim1.repository.QuickHotelReservationRepository;
import isamrs.tim1.repository.QuickVehicleReservationRepository;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.repository.ServiceRepository;
import isamrs.tim1.repository.UserRepository;
import isamrs.tim1.repository.VehicleRepository;
import isamrs.tim1.repository.VehicleReservationRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class ReservationService {

	@Autowired
	FlightReservationRepository flightReservationRepository;

	@Autowired
	FlightRepository flightRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ServiceRepository serviceRepository;

	@Autowired
	PassengerSeatRepository passengerSeatRepository;

	@Autowired
	EmailService mailService;

	@Autowired
	HotelRoomRepository hotelRoomRepository;

	@Autowired
	HotelAdditionalServicesRepository hotelAdditionalServicesRepository;

	@Autowired
	HotelReservationService hotelReservationService;

	@Autowired
	QuickHotelReservationRepository quickHotelReservationRepository;

	@Autowired
	QuickFlightReservationRepository quickFlightReservationRepository;

	@Autowired
	QuickVehicleReservationRepository quickVehicleReservationRepository;

	@Autowired
	BranchOfficeRepository branchOfficeRepository;

	@Autowired
	VehicleRepository vehicleRepository;

	@Autowired
	VehicleReservationRepository vehicleReservationRepository;

	@Autowired
	RentACarRepository rentACarRepository;

	public ArrayList<FlightReservationDTO> getReservations() {
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Set<FlightReservation> frs = flightReservationRepository.getByUser(ru.getId());
		ArrayList<FlightReservationDTO> frDTOs = new ArrayList<FlightReservationDTO>();
		for (FlightReservation flightRes : frs) {
			String res = flightRes.getFlight().getStartDestination().getName() + "-"
					+ flightRes.getFlight().getEndDestination().getName();
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			String date = sdf.format(flightRes.getFlight().getDepartureTime());
			frDTOs.add(new FlightReservationDTO(flightRes.getId(), res, date, flightRes.getPrice(), flightRes.getGrade()));
		}
		return frDTOs;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public MessageDTO reserveFlight(FlightReservationDTO flightRes) {
		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		MessageDTO retval = reserveFlightNoSave(flightRes, fr, ru);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;
		
		userRepository.save(ru);
		mailService.sendFlightReservationMail(ru, fr);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public MessageDTO reserveFlightHotel(FlightHotelReservationDTO flightHotelRes) {
		FlightReservationDTO flightRes = flightHotelRes.getFlightReservation();
		HotelReservationDTO hotelRes = flightHotelRes.getHotelReservation();
		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		MessageDTO retval = reserveFlightNoSave(flightRes, fr, ru);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;

		retval = reserveHotelNoSave(hotelRes, fr);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;
		
		userRepository.save(ru);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public MessageDTO reserveFlightVehicle(FlightVehicleReservationDTO flightVehicleRes) {
		FlightReservationDTO flightRes = flightVehicleRes.getFlightReservation();
		VehicleReservationDTO vehicleRes = flightVehicleRes.getVehicleReservation();
		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		MessageDTO retval = reserveFlightNoSave(flightRes, fr, ru);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;

		retval = reserveVehicleNoSave(vehicleRes, fr);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;

		userRepository.save(ru);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public MessageDTO reserveFlightHotelVehicle(FlightHotelVehicleReservationDTO flightHotelVehicleRes) {
		FlightReservationDTO flightRes = flightHotelVehicleRes.getFlightReservation();
		HotelReservationDTO hotelRes = flightHotelVehicleRes.getHotelReservation();
		VehicleReservationDTO vehicleRes = flightHotelVehicleRes.getVehicleReservation();

		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		MessageDTO retval = reserveFlightNoSave(flightRes, fr, ru);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;

		retval = reserveHotelNoSave(hotelRes, fr);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;

		retval = reserveVehicleNoSave(vehicleRes, fr);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString()))
			return retval;

		userRepository.save(ru);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private MessageDTO reserveFlightNoSave(FlightReservationDTO flightRes, FlightReservation fr,
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
			int row = Integer.parseInt(idx[0]);
			int column = Integer.parseInt(idx[1]);
			if (checkIfSeatIsReserved(f, row, column)) {
				return new MessageDTO("One of the seats is already reserved.", ToasterType.ERROR.toString());
			}
			Seat st = new Seat();
			st.setRow(row);
			st.setColumn(column);
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
			ps.setReservation(fr);
			fr.getPassengerSeats().add(ps);
			counter++;
		}
		for (String email : flightRes.getInvitedFriends()) {
			inviteFriendToFlight(email, flightRes, f, counter, ru.getEmail());
			counter++;
		}
		fr.setPrice(price);
		fr.setUser(ru);
		ru.getFlightReservations().add(fr);
		f.getAirline().getReservations().add(fr);
		return new MessageDTO("", ToasterType.SUCCESS.toString());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private MessageDTO reserveHotelNoSave(HotelReservationDTO hotelRes, FlightReservation fr) {
		if (hotelRes.getQuickReservationID() != null) {
			QuickHotelReservation qhr = quickHotelReservationRepository.findOneById(hotelRes.getQuickReservationID());
			if (qhr == null)
				return new MessageDTO("Quick hotel reservation does not exist", ToasterType.ERROR.toString());
			if (qhr.getFlightReservation() != null)
				return new MessageDTO("Quick hotel reservation is already taken", ToasterType.ERROR.toString());
			qhr.setFlightReservation(fr);
			fr.setHotelReservation(qhr);
			return new MessageDTO("", ToasterType.SUCCESS.toString());
		}

		HotelRoom room = hotelRoomRepository.findOneByNumberAndHotelName(hotelRes.getHotelRoomNumber(),
				hotelRes.getHotelName());
		if (checkRoomReservations(room, hotelRes.getFromDate(), hotelRes.getToDate())) {
			return new MessageDTO("This hotel room already has reservations in this period",
					ToasterType.ERROR.toString());
		}

		HashSet<HotelAdditionalService> additionalServices = new HashSet<HotelAdditionalService>();
		for (String asName : hotelRes.getAdditionalServiceNames()) {
			additionalServices
					.add(hotelAdditionalServicesRepository.findOneByNameAndHotelName(asName, hotelRes.getHotelName()));
		}
		HotelReservation hr = new HotelReservation(hotelRes, room, additionalServices, fr);
		hr.setPrice(hotelReservationService.calculateReservationPrice(hr));
		room.getReservations().add(hr);
		room.getHotel().getReservations().add(hr);
		for (HotelAdditionalService has : additionalServices) {
			has.getReservations().add(hr);
		}
		fr.setHotelReservation(hr);
		return new MessageDTO("", ToasterType.SUCCESS.toString());
	}

	private boolean checkRoomReservations(HotelRoom room, Date fromDate, Date toDate) {
		for (HotelReservation res : room.getReservations()) {
			// (StartA < EndB) and (EndA > StartB)
			if (res.getFromDate().before(toDate) && res.getToDate().after(fromDate)) {
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private MessageDTO reserveVehicleNoSave(VehicleReservationDTO vehicleRes, FlightReservation fr) {
		if (vehicleRes.getQuickVehicleReservationID() != null) {
			QuickVehicleReservation qvr = quickVehicleReservationRepository
					.findOneById(vehicleRes.getQuickVehicleReservationID());

			if (qvr == null) {
				return new MessageDTO("Quick vehicle reservation does not exist", ToasterType.ERROR.toString());
			}
			fr.setVehicleReservation(qvr);
			qvr.setFlightReservation(fr);
			return new MessageDTO("", ToasterType.SUCCESS.toString());
		}

		BranchOffice bo = branchOfficeRepository.findOneByName(vehicleRes.getBranchOfficeName());

		if (bo == null) {
			return new MessageDTO("Branch office does not exist", ToasterType.ERROR.toString());
		}

		Vehicle v = vehicleRepository.findOneByModelAndProducer(vehicleRes.getVehicleModel(),
				vehicleRes.getVehicleProducer());

		if (v == null) {
			return new MessageDTO("Vehicle does not exist", ToasterType.ERROR.toString());
		}

		if (!this.checkVehicleForPeriod(v.getId(), vehicleRes.getFromDate(), vehicleRes.getToDate())) {
			return new MessageDTO("Vehicle is taken in given period", ToasterType.ERROR.toString());
		}

		VehicleReservation vr = new VehicleReservation();
		vr.setId(null);
		vr.setBranchOffice(bo);
		vr.setFlightReservation(fr);
		vr.setFromDate(vehicleRes.getFromDate());
		vr.setToDate(vehicleRes.getToDate());
		vr.setVehicle(v);
		fr.setVehicleReservation(vr);

		return new MessageDTO("", ToasterType.SUCCESS.toString());
	}

	public Boolean checkVehicleForPeriod(Integer vehicleID, Date start, Date end) {
		return vehicleReservationRepository.findByDates(start, end, vehicleID).isEmpty();
	}

	private boolean checkIfSeatIsReserved(Flight flight, int row, int column) {
		for (FlightReservation r : flight.getAirline().getReservations()) {
			if (r.getFlight().getFlightCode().equals(flight.getFlightCode())) {
				for (PassengerSeat ps : r.getPassengerSeats()) {
					if (ps.getSeat() != null && (ps.getSeat().getRow() == row && ps.getSeat().getColumn() == column)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	private void inviteFriendToFlight(String email, FlightReservationDTO flightRes, Flight f, int counter,
			String inviter) {
		RegisteredUser friend = (RegisteredUser) userRepository.findOneByEmail(email);
		FlightReservation fRes = new FlightReservation();
		fRes.setFlight(f);
		fRes.setDone(false);
		fRes.setDateOfReservation(new Date());
		double price = 0.0;
		String[] idx = flightRes.getSeats()[counter].split("_");
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
		}
		PassengerSeat ps = new PassengerSeat(new PassengerDTO(friend.getFirstName(), friend.getLastName(), "", 0), st);
		ps.setReservation(fRes);
		fRes.setPrice(price);
		fRes.getPassengerSeats().add(ps);
		friend.getInvitingReservations().add(fRes);
		f.getAirline().getReservations().add(fRes);
		flightReservationRepository.save(fRes);
		userRepository.save(friend);
		mailService.sendMailToFriend(friend, fRes, inviter);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<MessageDTO> acceptFlightInvitation(String id) {
		Long resID = Long.parseLong(id);
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		FlightReservation fr = null;
		Set<FlightReservation> res = ru.getInvitingReservations();
		for (FlightReservation fRes : res) {
			if (fRes.getId().longValue() == resID) {
				fr = fRes;
				res.remove(fRes);
				break;
			}
		}
		if (fr == null) {
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Reservation does not exist.", ToasterType.ERROR.toString()), HttpStatus.OK);
		}
		
		fr.setUser(ru);
		ru.getFlightReservations().add(fr);
		userRepository.save(ru);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Successfully accepted reservation.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}

	public ArrayList<InvitingReservationDTO> getInvitingReservations() {
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ArrayList<InvitingReservationDTO> invitingReservations = new ArrayList<InvitingReservationDTO>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		for (FlightReservation fr : ru.getInvitingReservations()) {
			String description = fr.getFlight().getStartDestination().getName() + "-"
					+ fr.getFlight().getEndDestination().getName() + " "
					+ sdf.format(fr.getFlight().getDepartureTime());
			invitingReservations.add(new InvitingReservationDTO(fr.getId(), description));
		}
		return invitingReservations;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<MessageDTO> declineFlightInvitation(String id) {
		long resID = Long.parseLong(id);
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		FlightReservation fr = null;
		Set<FlightReservation> res = ru.getInvitingReservations();
		for (FlightReservation fRes : res) {
			if (fRes.getId().longValue() == resID) {
				fr = fRes;
				res.remove(fRes);
				break;
			}
		}
		if (fr == null) {
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Reservation does not exist.", ToasterType.ERROR.toString()), HttpStatus.OK);
		}
		Airline a = fr.getFlight().getAirline();
		a.getReservations().removeIf(r -> r.getId().longValue() == resID);
		userRepository.save(ru);
		serviceRepository.save(a);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Successfully declined reservation.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO createQuickFlightReservation(QuickFlightReservationDTO quickDTO) {
		Flight f = flightRepository.findOneByFlightCode(quickDTO.getFlightCode());
		if (f == null) {
			return new MessageDTO("Flight does not exist.", ToasterType.ERROR.toString());
		}
		QuickFlightReservation qfr = new QuickFlightReservation();
		double price = 0.0;
		String[] idx = quickDTO.getSeat().split("_");
		int row = Integer.parseInt(idx[0]);
		int column = Integer.parseInt(idx[1]);
		if (checkIfSeatIsReserved(f, row, column)) {
			return new MessageDTO("One of the seats is already reserved.", ToasterType.ERROR.toString());
		}
		Seat st = new Seat();
		st.setRow(row);
		st.setColumn(column);
		if (idx[2].equalsIgnoreCase(PlaneSegmentClass.FIRST.toString().substring(0, 1))) {
			st.setPlaneSegment(new PlaneSegment(PlaneSegmentClass.FIRST));
			price = f.getFirstClassPrice();
		} else if (idx[2].equalsIgnoreCase(PlaneSegmentClass.BUSINESS.toString().substring(0, 1))) {
			st.setPlaneSegment(new PlaneSegment(PlaneSegmentClass.BUSINESS));
			price = f.getBusinessClassPrice();
		} else if (idx[2].equalsIgnoreCase(PlaneSegmentClass.ECONOMY.toString().substring(0, 1))) {
			st.setPlaneSegment(new PlaneSegment(PlaneSegmentClass.ECONOMY));
			price = f.getEconomyClassPrice();
		}
		PassengerSeat ps = new PassengerSeat(new PassengerDTO("", "", "", 0), st);
		qfr.setFlight(f);
		qfr.setDone(false);
		qfr.setDiscount(Integer.parseInt(quickDTO.getDiscount()));
		qfr.setPrice((1.0 - qfr.getDiscount() / 100.0) * price);
		qfr.setUser(null);
		ps.setReservation(qfr);
		qfr.getPassengerSeats().add(ps);
		f.getAirline().getReservations().add(qfr);
		quickFlightReservationRepository.save(qfr);
		return new MessageDTO("Quick flight reservation successfully created", ToasterType.SUCCESS.toString());
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO createQuickHotelReservation(QuickHotelReservationDTO hotelRes) {
		Hotel hotel = ((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel();
		HotelRoom room = hotelRoomRepository.findOneByNumberAndHotelName(hotelRes.getHotelRoomNumber(),
				hotel.getName());
		if (checkRoomReservations(room, hotelRes.getFromDate(), hotelRes.getToDate())) {
			return new MessageDTO("This hotel room already has reservations in this period",
					ToasterType.ERROR.toString());
		}

		HashSet<HotelAdditionalService> additionalServices = new HashSet<HotelAdditionalService>();
		for (String asName : hotelRes.getAdditionalServiceNames()) {
			additionalServices
					.add(hotelAdditionalServicesRepository.findOneByNameAndHotelName(asName, hotel.getName()));
		}
		QuickHotelReservation qhr = new QuickHotelReservation(hotelRes, room, additionalServices);
		qhr.setPrice((1.0 - qhr.getDiscount() / 100.0) * hotelReservationService.calculateReservationPrice(qhr));
		room.getReservations().add(qhr);
		room.getHotel().getReservations().add(qhr);
		for (HotelAdditionalService has : additionalServices) {
			has.getReservations().add(qhr);
		}
		quickHotelReservationRepository.save(qhr);
		return new MessageDTO("Quick hotel reservation successfully created", ToasterType.SUCCESS.toString());
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO createQuickVehicleReservation(QuickVehicleReservationDTO quickReservation) {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String branchName = quickReservation.getBranchOfficeName();
		String producer = quickReservation.getVehicleProducer();
		String model = quickReservation.getVehicleModel();

		Date from = null;
		try {
			from = sdf.parse(quickReservation.getFromDate());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		Date to = null;
		try {
			to = sdf.parse(quickReservation.getToDate());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		for (VehicleReservation vr : rentACar.getReservations()) {
			if (vr.getBranchOffice().getName().equals(branchName) && vr.getVehicle().getProducer().equals(producer)
					&& vr.getVehicle().getModel().equals(model) && vr.getFromDate().compareTo(from) <= 0
					&& vr.getToDate().compareTo(to) >= 0) {
				return new MessageDTO("Vehicle is taken in given period", ToasterType.ERROR.toString());
			}
		}

		QuickVehicleReservation newQuickReservation = new QuickVehicleReservation();

		BranchOffice br = rentACar.getBranchOffices().stream()
				.filter(bo -> bo.getName().equals(quickReservation.getBranchOfficeName())).findFirst().orElse(null);
		newQuickReservation.setBranchOffice(br);
		newQuickReservation.setDiscount(quickReservation.getDiscount());
		try {
			newQuickReservation.setFromDate(sdf.parse(quickReservation.getFromDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			newQuickReservation.setToDate(sdf.parse(quickReservation.getToDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		newQuickReservation.setId(null);
		Vehicle v = rentACar.getVehicles().stream()
				.filter(ve -> ve.getProducer().equals(quickReservation.getVehicleProducer())
						&& ve.getModel().equals(quickReservation.getVehicleModel()))
				.findFirst().orElse(null);
		newQuickReservation.setVehicle(v);
		newQuickReservation.setFlightReservation(null);

		int numberOfDays = (int) ((newQuickReservation.getFromDate().getTime()
				- newQuickReservation.getToDate().getTime()) / (1000 * 60 * 60 * 24));

		if (numberOfDays == 0) {
			numberOfDays = 1;
		}
		newQuickReservation.setPrice(numberOfDays * newQuickReservation.getDiscount() / 100.0);
		rentACar.getReservations().add(newQuickReservation);

		rentACarRepository.save(rentACar);

		return new MessageDTO("Quick vehicle reservation added successfully", ToasterType.SUCCESS.toString());
	}

	public ArrayList<QuickVehicleReservationDTO> getQuickVehicleReservations() {
		RentACar rentACar = ((RentACarAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				.getRentACar();

		ArrayList<QuickVehicleReservationDTO> quickReservations = new ArrayList<QuickVehicleReservationDTO>();

		for (VehicleReservation vr : rentACar.getReservations()) {
			if (vr instanceof QuickVehicleReservation) {
				QuickVehicleReservation qvr = (QuickVehicleReservation) vr;

				if (qvr.getFlightReservation() == null) {
					quickReservations.add(new QuickVehicleReservationDTO(qvr));
				}
			}
		}

		return quickReservations;
	}

	public ArrayList<VehicleReservationDTO> getQuickVehicleReservationsForVehicle(int vehicleId) {
		ArrayList<QuickVehicleReservation> quickReservations = quickVehicleReservationRepository
				.findAllByVehicle(vehicleId);

		ArrayList<VehicleReservationDTO> dtos = new ArrayList<VehicleReservationDTO>();
		for (QuickVehicleReservation qvr : quickReservations) {
			if (qvr.getFlightReservation() == null) {
				dtos.add(new VehicleReservationDTO(qvr));
			}
		}

		return dtos;
	}

	public ArrayList<QuickFlightReservationDTO> getQuickFlightReservations() {
		AirlineAdmin airlineAdmin = (AirlineAdmin) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Airline a = airlineAdmin.getAirline();
		ArrayList<QuickFlightReservationDTO> quickRes = new ArrayList<QuickFlightReservationDTO>();
		Set<FlightReservation> fRes = a.getReservations();
		for (FlightReservation fr : fRes) {
			if (fr instanceof QuickFlightReservation && fr.getUser() == null) {
				quickRes.add(new QuickFlightReservationDTO((QuickFlightReservation) fr));
			}
		}
		return quickRes;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<MessageDTO> cancelReservation(String resID) {
		Long id = Long.parseLong(resID);
		QuickFlightReservation qfr = quickFlightReservationRepository.findById(id).orElse(null);
		if (qfr != null) {
			if (qfr.getDone()) {
				return new ResponseEntity<MessageDTO>(
						new MessageDTO("You can not cancel flight reservation which is done.",
								ToasterType.ERROR.toString()),
						HttpStatus.OK);
			}
			RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			ru.getFlightReservations().removeIf(u -> u.getId().longValue() == qfr.getId().longValue());
			userRepository.save(ru);
			qfr.setUser(null);
			qfr.setDateOfReservation(null);
			qfr.getPassengerSeats().forEach(p -> {
				p.setName("");
				p.setSurname("");
				p.setPassport("");
			});
			quickFlightReservationRepository.save(qfr);
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Successfully canceled reservation.", ToasterType.SUCCESS.toString()),
					HttpStatus.OK);
		}
		FlightReservation fr = flightReservationRepository.findById(id).orElse(null);
		if (fr == null) {
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Reservation does not exist.", ToasterType.ERROR.toString()), HttpStatus.OK);
		}
		if (fr.getDone()) {
			return new ResponseEntity<MessageDTO>(new MessageDTO("You can not cancel flight reservation which is done.",
					ToasterType.ERROR.toString()), HttpStatus.OK);
		}
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Airline a = fr.getFlight().getAirline();
		a.getReservations().removeIf(f -> f.getId().longValue() == fr.getId().longValue());

		if (fr.getHotelReservation() != null) {
			Hotel hotel = fr.getHotelReservation().getHotelRoom().getHotel();
			hotel.getReservations().removeIf(h -> {
				if (h.getId().longValue() == fr.getHotelReservation().getId().longValue()) {
					if (quickHotelReservationRepository.existsById(h.getId())) {
						h.setFlightReservation(null);
						return false;
					} else {
						return true;
					}
				}
				return false;
			});
			serviceRepository.save(hotel);
		}

		if (fr.getVehicleReservation() != null) {
			RentACar rac = fr.getVehicleReservation().getVehicle().getRentACar();
			rac.getReservations().removeIf(r -> {
				if (r.getId().longValue() == fr.getVehicleReservation().getId().longValue()) {
					if (quickVehicleReservationRepository.existsById(r.getId())) {
						r.setFlightReservation(null);
						return false;
					} else {
						return true;
					}
				}
				return false;
			});
			serviceRepository.save(rac);
		}

		ru.getFlightReservations().removeIf(u -> u.getId().longValue() == fr.getId().longValue());
		userRepository.save(ru);
		serviceRepository.save(a);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Successfully canceled reservation.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}

	public MessageDTO reserveQuickFlightReservation(FlightReservationDTO flightRes) {
		QuickFlightReservation qfr = quickFlightReservationRepository.findById(flightRes.getQuickReservationID())
				.orElse(null);
		if (qfr == null)
			return new MessageDTO("Quick flight reservation does not exist.", ToasterType.ERROR.toString());
		if (qfr.getUser() != null) {
			return new MessageDTO("Quick flight reservation is already taken.", ToasterType.ERROR.toString());
		}
		qfr.setDone(false);
		qfr.setDateOfReservation(new Date());
		String userPassport = flightRes.getPassengers()[0].getPassport();
		int numOfBags = flightRes.getPassengers()[0].getNumberOfBags();
		double price = numOfBags * qfr.getFlight().getPricePerBag();
		qfr.getPassengerSeats().forEach(p -> {
			p.setName(flightRes.getPassengers()[0].getFirstName());
			p.setSurname(flightRes.getPassengers()[0].getLastName());
			p.setPassport(userPassport);
			p.setReservation(qfr);
		});
		qfr.setPrice(qfr.getPrice() + price);
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ru.getFlightReservations().add(qfr);
		qfr.setUser(ru);
		userRepository.save(ru);
		mailService.sendFlightReservationMail(ru, qfr);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}
}
