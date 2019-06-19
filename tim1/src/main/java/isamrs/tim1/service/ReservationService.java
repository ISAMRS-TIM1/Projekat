package isamrs.tim1.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import isamrs.tim1.dto.DetailedReservationDTO;
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
import isamrs.tim1.model.DiscountInfo;
import isamrs.tim1.model.Flight;
import isamrs.tim1.model.FlightInvitation;
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
import isamrs.tim1.repository.DiscountInfoRepository;
import isamrs.tim1.repository.FlightInvitationRepository;
import isamrs.tim1.repository.FlightRepository;
import isamrs.tim1.repository.FlightReservationRepository;
import isamrs.tim1.repository.HotelAdditionalServicesRepository;
import isamrs.tim1.repository.HotelRepository;
import isamrs.tim1.repository.HotelReservationRepository;
import isamrs.tim1.repository.HotelRoomRepository;
import isamrs.tim1.repository.QuickFlightReservationRepository;
import isamrs.tim1.repository.QuickHotelReservationRepository;
import isamrs.tim1.repository.QuickVehicleReservationRepository;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.repository.SeatRepository;
import isamrs.tim1.repository.ServiceRepository;
import isamrs.tim1.repository.UserRepository;
import isamrs.tim1.repository.VehicleRepository;
import isamrs.tim1.repository.VehicleReservationRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class ReservationService {

	@Autowired
	private FlightReservationRepository flightReservationRepository;

	@Autowired
	private FlightRepository flightRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ServiceRepository serviceRepository;

	@Autowired
	private EmailService mailService;

	@Autowired
	private HotelRoomRepository hotelRoomRepository;

	@Autowired
	private HotelAdditionalServicesRepository hotelAdditionalServicesRepository;

	@Autowired
	private HotelReservationService hotelReservationService;

	@Autowired
	private VehicleReservationService vehicleReservationService;

	@Autowired
	private QuickHotelReservationRepository quickHotelReservationRepository;

	@Autowired
	private QuickFlightReservationRepository quickFlightReservationRepository;

	@Autowired
	private QuickVehicleReservationRepository quickVehicleReservationRepository;

	@Autowired
	private BranchOfficeRepository branchOfficeRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private VehicleReservationRepository vehicleReservationRepository;

	@Autowired
	private RentACarRepository rentACarRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private FlightInvitationRepository flightInvitationRepository;

	@Autowired
	private DiscountInfoRepository discountInfoRepository;
	
	@Autowired
	private HotelRepository hotelRepository;
	
	@Autowired
	private HotelReservationRepository hotelReservationRepository;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	public ArrayList<FlightReservationDTO> getReservations() {
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Set<FlightReservation> frs = flightReservationRepository.getByUser(ru.getId());
		ArrayList<FlightReservationDTO> frDTOs = new ArrayList<FlightReservationDTO>();
		for (FlightReservation flightRes : frs) {
			String res = flightRes.getFlight().getStartDestination().getName() + "-"
					+ flightRes.getFlight().getEndDestination().getName();
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
			String date = sdf.format(flightRes.getFlight().getDepartureTime());
			frDTOs.add(
					new FlightReservationDTO(flightRes.getId(), res, date, flightRes.getPrice(), flightRes.getGrade()));
		}
		return frDTOs;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public MessageDTO reserveFlight(int discountPoints, FlightReservationDTO flightRes) {

		if (discountPoints < 0)
			return new MessageDTO("Discount points cannot be negative", ToasterType.ERROR.toString());
		DiscountInfo di = discountInfoRepository.findAll().get(0);
		if (discountPoints > di.getMaxDiscountPoints())
			return new MessageDTO("Too much discount points used, max is " + di.getMaxDiscountPoints(),
					ToasterType.ERROR.toString());

		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (discountPoints > ru.getDiscountPoints())
			return new MessageDTO("Not enough discount points.", ToasterType.ERROR.toString());

		MessageDTO retval = reserveFlightNoSave(flightRes, fr, ru);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString())) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return retval;
		}

		double discountPercentage = discountPoints * di.getDiscountPercentagePerPoint();

		fr.setPrice(fr.getPrice() * (1 - discountPercentage / 100));
		fr.setUsedPoints(discountPoints);
		userRepository.save(ru);
		mailService.sendFlightReservationMail(ru, fr);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public MessageDTO reserveFlightHotel(int discountPoints, FlightHotelReservationDTO flightHotelRes) {
		if (discountPoints < 0)
			return new MessageDTO("Discount points cannot be negative.", ToasterType.ERROR.toString());
		DiscountInfo di = discountInfoRepository.findAll().get(0);
		if (discountPoints > di.getMaxDiscountPoints())
			return new MessageDTO("Too much discount points used, max is " + di.getMaxDiscountPoints(),
					ToasterType.ERROR.toString());

		FlightReservationDTO flightRes = flightHotelRes.getFlightReservation();
		HotelReservationDTO hotelRes = flightHotelRes.getHotelReservation();
		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (discountPoints > ru.getDiscountPoints())
			return new MessageDTO("Not enough discount points.", ToasterType.ERROR.toString());

		MessageDTO retval = reserveFlightNoSave(flightRes, fr, ru);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString())) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return retval;
		}

		retval = reserveHotelNoSave(hotelRes, fr);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString())) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return retval;
		}

		double discountPercentage = discountPoints * di.getDiscountPercentagePerPoint();
		discountPercentage += di.getDiscountPerExtraReservation();

		fr.setPrice(fr.getPrice() * (1 - discountPercentage / 100));
		if (hotelRes.getQuickReservationID() == null) // discount only on non quick res
			fr.getHotelReservation().setPrice(fr.getHotelReservation().getPrice() * (1 - discountPercentage / 100));
		fr.setUsedPoints(discountPoints);
		userRepository.save(ru);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public MessageDTO reserveFlightVehicle(int discountPoints, FlightVehicleReservationDTO flightVehicleRes) {
		if (discountPoints < 0)
			return new MessageDTO("Discount points cannot be negative.", ToasterType.ERROR.toString());
		DiscountInfo di = discountInfoRepository.findAll().get(0);
		if (discountPoints > di.getMaxDiscountPoints())
			return new MessageDTO("Too much discount points used, max is " + di.getMaxDiscountPoints(),
					ToasterType.ERROR.toString());
		FlightReservationDTO flightRes = flightVehicleRes.getFlightReservation();
		VehicleReservationDTO vehicleRes = flightVehicleRes.getVehicleReservation();
		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (discountPoints > ru.getDiscountPoints())
			return new MessageDTO("Not enough discount points.", ToasterType.ERROR.toString());

		MessageDTO retval = reserveFlightNoSave(flightRes, fr, ru);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString())) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return retval;
		}

		retval = reserveVehicleNoSave(vehicleRes, fr);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString())) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return retval;
		}
		double discountPercentage = discountPoints * di.getDiscountPercentagePerPoint();
		discountPercentage += di.getDiscountPerExtraReservation();

		fr.setPrice(fr.getPrice() * (1 - discountPercentage / 100));
		if (vehicleRes.getQuickVehicleReservationID() == null) // discount only on non quick res
			fr.getVehicleReservation().setPrice(fr.getVehicleReservation().getPrice() * (1 - discountPercentage / 100));
		fr.setUsedPoints(discountPoints);
		userRepository.save(ru);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public MessageDTO reserveFlightHotelVehicle(int discountPoints,
			FlightHotelVehicleReservationDTO flightHotelVehicleRes) {
		if (discountPoints < 0)
			return new MessageDTO("Discount points cannot be negative.", ToasterType.ERROR.toString());
		DiscountInfo di = discountInfoRepository.findAll().get(0);
		if (discountPoints > di.getMaxDiscountPoints())
			return new MessageDTO("Too much discount points used, max is " + di.getMaxDiscountPoints(),
					ToasterType.ERROR.toString());
		FlightReservationDTO flightRes = flightHotelVehicleRes.getFlightReservation();
		HotelReservationDTO hotelRes = flightHotelVehicleRes.getHotelReservation();
		VehicleReservationDTO vehicleRes = flightHotelVehicleRes.getVehicleReservation();

		FlightReservation fr = new FlightReservation();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (discountPoints > ru.getDiscountPoints())
			return new MessageDTO("Not enough discount points.", ToasterType.ERROR.toString());

		MessageDTO retval = reserveFlightNoSave(flightRes, fr, ru);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString())) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return retval;
		}

		retval = reserveHotelNoSave(hotelRes, fr);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString())) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return retval;
		}

		retval = reserveVehicleNoSave(vehicleRes, fr);
		if (retval.getToastType().toString().equals(ToasterType.ERROR.toString())) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return retval;
		}

		double discountPercentage = discountPoints * di.getDiscountPercentagePerPoint();
		discountPercentage += 2 * di.getDiscountPerExtraReservation();

		fr.setPrice(fr.getPrice() * (1 - discountPercentage / 100));
		if (hotelRes.getQuickReservationID() == null) // discount only on non quick res
			fr.getHotelReservation().setPrice(fr.getHotelReservation().getPrice() * (1 - discountPercentage / 100));
		if (vehicleRes.getQuickVehicleReservationID() == null) // discount only on non quick res
			fr.getVehicleReservation().setPrice(fr.getVehicleReservation().getPrice() * (1 - discountPercentage / 100));
		fr.setUsedPoints(discountPoints);
		userRepository.save(ru);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private MessageDTO reserveFlightNoSave(FlightReservationDTO flightRes, FlightReservation fr, RegisteredUser ru) {
		Flight f = flightRepository.findOneByFlightCodeForRead(flightRes.getFlightCode());
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
		int row;
		int column;
		for (PassengerDTO p : passengerList) {
			String[] idx = flightRes.getSeats()[counter].split("_");
			price += p.getNumberOfBags() * f.getPricePerBag();
			row = Integer.parseInt(idx[0]);
			column = Integer.parseInt(idx[1]);
			Seat st = null;
			PlaneSegment planeSegment;
			if (idx[2].equalsIgnoreCase(PlaneSegmentClass.FIRST.toString().substring(0, 1))) {
				planeSegment = f.getPlaneSegments().stream()
						.filter(planeSeg -> planeSeg.getSegmentClass() == PlaneSegmentClass.FIRST).findFirst().get();
				price += f.getFirstClassPrice();
			} else if (idx[2].equalsIgnoreCase(PlaneSegmentClass.BUSINESS.toString().substring(0, 1))) {
				planeSegment = f.getPlaneSegments().stream()
						.filter(planeSeg -> planeSeg.getSegmentClass() == PlaneSegmentClass.BUSINESS).findFirst().get();
				price += f.getBusinessClassPrice();
			} else if (idx[2].equalsIgnoreCase(PlaneSegmentClass.ECONOMY.toString().substring(0, 1))) {
				planeSegment = f.getPlaneSegments().stream()
						.filter(planeSeg -> planeSeg.getSegmentClass() == PlaneSegmentClass.ECONOMY).findFirst().get();
				price += f.getEconomyClassPrice();
			} else {
				return null;
			}
			st = seatRepository.findOneByRowAndColumnAndPlaneSegment(row, column, planeSegment);
			if (st.getPassengerSeat() != null) {
				return new MessageDTO("Seat " + row + "_" + column + " is already reserved.",
						ToasterType.ERROR.toString());
			}
			try {
				PassengerSeat ps = new PassengerSeat(p, st);
				st.setPassengerSeat(ps);
				ps.setReservation(fr);
				fr.getPassengerSeats().add(ps);
				seatRepository.flush();
			} catch (OptimisticLockingFailureException ex) {
				return new MessageDTO("Seat " + row + "_" + column + " has just been taken", ToasterType.ERROR.toString());
			}
			counter++;
		}
		for (String email : flightRes.getInvitedFriends()) {
			MessageDTO m = inviteFriendToFlight(email, flightRes, f.getFlightCode(), counter, ru.getEmail());
			if (m.getToastType().toString().equals(ToasterType.ERROR.toString())) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return m;
			}
			counter++;
		}
		fr.setPrice(price);
		fr.setUser(ru);
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
			if (!sdf.format(qhr.getFromDate()).equals(sdf.format(fr.getFlight().getLandingTime())))
				return new MessageDTO("Quick hotel reservation start day must be same as flight landing day",
						ToasterType.ERROR.toString());
			if (!qhr.getHotelRoom().getHotel().getLocation().getCountry()
					.equals(fr.getFlight().getEndDestination().getLocation().getCountry()))
				return new MessageDTO("Hotel country must be same as flight destination country",
						ToasterType.ERROR.toString());
			try {
				qhr.setFlightReservation(fr);
				fr.setHotelReservation(qhr);
				quickHotelReservationRepository.flush();
			} catch (OptimisticLockingFailureException ex) {
				return new MessageDTO("Quick hotel reservation has just been taken", ToasterType.ERROR.toString());
			}
			return new MessageDTO("", ToasterType.SUCCESS.toString());
		}

		Hotel hotel = hotelRepository.findOneByName(hotelRes.getHotelName());
		HotelRoom room = hotelRoomRepository.findOneByNumberAndHotel(hotelRes.getHotelRoomNumber(),
				hotel);

		if (!sdf.format(hotelRes.getFromDate()).equals(sdf.format(fr.getFlight().getLandingTime())))
			return new MessageDTO("Hotel reservation start day must be same as flight landing day",
					ToasterType.ERROR.toString());
		if (!room.getHotel().getLocation().getCountry()
				.equals(fr.getFlight().getEndDestination().getLocation().getCountry()))
			return new MessageDTO("Hotel country must be same as flight destination country",
					ToasterType.ERROR.toString());
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
			QuickVehicleReservation qvr;

			try {
				qvr = quickVehicleReservationRepository.findOneById(vehicleRes.getQuickVehicleReservationID());
			} catch (OptimisticLockException e) {
				return new MessageDTO("Quick vehicle reservation has already been reserved",
						ToasterType.ERROR.toString());
			}
			if (qvr == null) {
				return new MessageDTO("Quick vehicle reservation does not exist", ToasterType.ERROR.toString());
			} else if (!sdf.format(qvr.getFromDate()).equals(sdf.format(fr.getFlight().getLandingTime()))) {
				return new MessageDTO("Vehicle reservation start day must be same as flight landing day",
						ToasterType.ERROR.toString());
			} else if (!qvr.getBranchOffice().getLocation().getCountry()
					.equals(fr.getFlight().getEndDestination().getLocation().getCountry())) {
				return new MessageDTO("Branch office country must be same as flight destination country",
						ToasterType.ERROR.toString());
			}
			fr.setVehicleReservation(qvr);
			qvr.setFlightReservation(fr);
			return new MessageDTO("", ToasterType.SUCCESS.toString());
		}

		BranchOffice bo = branchOfficeRepository.findOneByName(vehicleRes.getBranchOfficeName());

		if (bo == null) {
			return new MessageDTO("Branch office does not exist", ToasterType.ERROR.toString());
		} else if (!bo.getLocation().getCountry()
				.equals(fr.getFlight().getEndDestination().getLocation().getCountry())) {
			return new MessageDTO("Branch office country must be same as flight destination country",
					ToasterType.ERROR.toString());
		}

		Vehicle v = vehicleRepository.findOneByModelAndProducer(vehicleRes.getVehicleModel(),
				vehicleRes.getVehicleProducer());

		if (v == null) {
			return new MessageDTO("Vehicle does not exist", ToasterType.ERROR.toString());
		}

		if (!this.checkVehicleForPeriod(v.getId(), vehicleRes.getFromDate(), vehicleRes.getToDate())) {
			return new MessageDTO("Vehicle is taken in given period", ToasterType.ERROR.toString());
		} else if (!sdf.format(vehicleRes.getFromDate()).equals(sdf.format(fr.getFlight().getLandingTime()))) {
			return new MessageDTO("Vehicle reservation start day must be same as flight landing day",
					ToasterType.ERROR.toString());
		}

		VehicleReservation vr = new VehicleReservation();
		vr.setId(null);
		vr.setBranchOffice(bo);
		vr.setFlightReservation(fr);
		vr.setFromDate(vehicleRes.getFromDate());
		vr.setToDate(vehicleRes.getToDate());
		vr.setVehicle(v);
		vr.setPrice(vehicleReservationService.calculateReservationPrice(vr));
		fr.setVehicleReservation(vr);

		return new MessageDTO("", ToasterType.SUCCESS.toString());
	}

	public Boolean checkVehicleForPeriod(Integer vehicleID, Date start, Date end) {
		return vehicleReservationRepository.findByDates(start, end, vehicleID).isEmpty();
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	private MessageDTO inviteFriendToFlight(String email, FlightReservationDTO flightRes, String flightCode, int counter,
			String inviter) {
		RegisteredUser friend = (RegisteredUser) userRepository.findOneByEmail(email);
		FlightReservation fRes = new FlightReservation();
		Flight f = flightRepository.findOneByFlightCodeForRead(flightCode);
		fRes.setFlight(f);
		fRes.setDone(false);
		fRes.setDateOfReservation(new Date());
		double price = 0.0;
		String[] idx = flightRes.getSeats()[counter].split("_");
		int row = Integer.parseInt(idx[0]);
		int column = Integer.parseInt(idx[1]);
		Seat st = null;
		PlaneSegment planeSegment = null;
		if (idx[2].equalsIgnoreCase(PlaneSegmentClass.FIRST.toString().substring(0, 1))) {
			planeSegment = f.getPlaneSegments().stream()
					.filter(planeSeg -> planeSeg.getSegmentClass() == PlaneSegmentClass.FIRST).findFirst().get();
			price += f.getFirstClassPrice();
		} else if (idx[2].equalsIgnoreCase(PlaneSegmentClass.BUSINESS.toString().substring(0, 1))) {
			planeSegment = f.getPlaneSegments().stream()
					.filter(planeSeg -> planeSeg.getSegmentClass() == PlaneSegmentClass.BUSINESS).findFirst().get();
			price += f.getBusinessClassPrice();
		} else if (idx[2].equalsIgnoreCase(PlaneSegmentClass.ECONOMY.toString().substring(0, 1))) {
			planeSegment = f.getPlaneSegments().stream()
					.filter(planeSeg -> planeSeg.getSegmentClass() == PlaneSegmentClass.ECONOMY).findFirst().get();
			price += f.getEconomyClassPrice();
		}
		st = seatRepository.findOneByRowAndColumnAndPlaneSegment(row, column, planeSegment);
		try {
			PassengerSeat ps = new PassengerSeat(new PassengerDTO(friend.getFirstName(), friend.getLastName(), "", 0), st);
			ps.setReservation(fRes);
			st.setPassengerSeat(ps);
			fRes.setPrice(price);
			fRes.getPassengerSeats().add(ps);
			seatRepository.flush();
		} catch (OptimisticLockingFailureException ex) {
			return new MessageDTO("Seat " + row + "_" + column + " for friend has just been taken", ToasterType.ERROR.toString());
		}
		
		FlightInvitation flightInv = new FlightInvitation();
		flightInv.setDateOfInviting(new Date());
		flightInv.setFlightReservation(fRes);
		flightInv.setInviter((RegisteredUser) userRepository.findOneByEmail(inviter));
		flightInv.setInvited(friend);
		friend.getInvitingReservations().add(flightInv);
		f.getAirline().getReservations().add(fRes);
		flightReservationRepository.save(fRes);
		userRepository.save(friend);
		mailService.sendMailToFriend(friend, fRes, inviter);
		return new MessageDTO("Friend is successfully invited to flight", ToasterType.SUCCESS.toString());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
	public ResponseEntity<MessageDTO> acceptFlightInvitation(FlightReservationDTO fRes) {
		Long resID = fRes.getId();
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		FlightReservation fr = null;
		FlightInvitation fInvit = null;
		Set<FlightInvitation> res = ru.getInvitingReservations();
		for (FlightInvitation fInv : res) {
			if (fInv.getFlightReservation().getId().longValue() == resID) {
				fr = fInv.getFlightReservation();
				fInvit = fInv;
				res.remove(fInv);
				break;
			}
		}
		if (fr == null) {
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Reservation does not exist.", ToasterType.ERROR.toString()), HttpStatus.OK);
		}

		fr.getPassengerSeats().forEach(p -> {
			p.setPassport(fRes.getPassengers()[0].getPassport());
		});
		fr.setPrice(fr.getPrice() + fRes.getPassengers()[0].getNumberOfBags() * fr.getFlight().getPricePerBag());
		fr.setUser(ru);
		ru.getFlightReservations().add(fr);
		userRepository.save(ru);
		flightInvitationRepository.delete(fInvit);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Successfully accepted reservation.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}

	public ArrayList<InvitingReservationDTO> getInvitingReservations() {
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ArrayList<InvitingReservationDTO> invitingReservations = new ArrayList<InvitingReservationDTO>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		for (FlightInvitation fInv : ru.getInvitingReservations()) {
			String description = fInv.getFlightReservation().getFlight().getStartDestination().getName() + "-"
					+ fInv.getFlightReservation().getFlight().getEndDestination().getName() + " "
					+ sdf.format(fInv.getFlightReservation().getFlight().getDepartureTime());
			invitingReservations.add(new InvitingReservationDTO(fInv.getFlightReservation().getId(), description));
		}
		return invitingReservations;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
	public ResponseEntity<MessageDTO> declineFlightInvitation(String id) {
		long resID = Long.parseLong(id);
		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		FlightReservation fr = null;
		FlightInvitation fInvit = null;
		Set<FlightInvitation> res = ru.getInvitingReservations();
		for (FlightInvitation fInv : res) {
			if (fInv.getFlightReservation().getId().longValue() == resID) {
				fr = fInv.getFlightReservation();
				fInvit = fInv;
				res.remove(fInv);
				break;
			}
		}
		if (fr == null) {
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Reservation does not exist.", ToasterType.ERROR.toString()), HttpStatus.OK);
		}
		fr.getPassengerSeats().stream().forEach(pSt -> {
			pSt.getSeat().setPassengerSeat(null);
			seatRepository.save(pSt.getSeat());
		});
		Airline a = fr.getFlight().getAirline();
		a.getReservations().removeIf(r -> r.getId().longValue() == resID);
		userRepository.save(ru);
		serviceRepository.save(a);
		flightInvitationRepository.delete(fInvit);
		flightReservationRepository.delete(fr);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Successfully declined reservation.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO createQuickFlightReservation(QuickFlightReservationDTO quickDTO) {
		Flight f = flightRepository.findOneByFlightCodeForRead(quickDTO.getFlightCode());
		if (f == null) {
			return new MessageDTO("Flight does not exist.", ToasterType.ERROR.toString());
		}
		QuickFlightReservation qfr = new QuickFlightReservation();
		double price = 0.0;
		String[] idx = quickDTO.getSeat().split("_");
		int row = Integer.parseInt(idx[0]);
		int column = Integer.parseInt(idx[1]);
		Seat st = null;
		PlaneSegment planeSegment;
		if (idx[2].equalsIgnoreCase(PlaneSegmentClass.FIRST.toString().substring(0, 1))) {
			planeSegment = f.getPlaneSegments().stream()
					.filter(planeSeg -> planeSeg.getSegmentClass() == PlaneSegmentClass.FIRST).findFirst().get();
			price += f.getFirstClassPrice();
		} else if (idx[2].equalsIgnoreCase(PlaneSegmentClass.BUSINESS.toString().substring(0, 1))) {
			planeSegment = f.getPlaneSegments().stream()
					.filter(planeSeg -> planeSeg.getSegmentClass() == PlaneSegmentClass.BUSINESS).findFirst().get();
			price += f.getBusinessClassPrice();
		} else if (idx[2].equalsIgnoreCase(PlaneSegmentClass.ECONOMY.toString().substring(0, 1))) {
			planeSegment = f.getPlaneSegments().stream()
					.filter(planeSeg -> planeSeg.getSegmentClass() == PlaneSegmentClass.ECONOMY).findFirst().get();
			price += f.getEconomyClassPrice();
		} else {
			return null;
		}
		st = seatRepository.findOneByRowAndColumnAndPlaneSegment(row, column, planeSegment);
		if (st.getPassengerSeat() != null) {
			return new MessageDTO("Seat is already reserved.", ToasterType.ERROR.toString());
		}
		try {
			PassengerSeat ps = new PassengerSeat(new PassengerDTO("", "", "", 0), st);
			st.setPassengerSeat(ps);
			ps.setReservation(qfr);
			qfr.getPassengerSeats().add(ps);
			qfr.setDone(false);
			seatRepository.flush();
		} catch (OptimisticLockingFailureException ex) {
			return new MessageDTO("Seat " + row + "_" + column + " has just been taken", ToasterType.ERROR.toString());
		}
		
		qfr.setFlight(f);
		qfr.setDiscount(Integer.parseInt(quickDTO.getDiscount()));
		qfr.setPrice((1.0 - qfr.getDiscount() / 100.0) * price);
		qfr.setUser(null);
		f.getAirline().getReservations().add(qfr);
		quickFlightReservationRepository.save(qfr);
		return new MessageDTO("Quick flight reservation successfully created", ToasterType.SUCCESS.toString());
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MessageDTO createQuickHotelReservation(QuickHotelReservationDTO hotelRes) {
		Hotel hotel = ((HotelAdmin) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getHotel();
		HotelRoom room = hotelRoomRepository.findOneByNumberAndHotel(hotelRes.getHotelRoomNumber(),
				hotel);
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

		fr.getPassengerSeats().stream().forEach(pSt -> {
			pSt.getSeat().setPassengerSeat(null);
			seatRepository.save(pSt.getSeat());
		});
		serviceRepository.save(a);
		flightReservationRepository.delete(fr);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Successfully canceled reservation.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public MessageDTO reserveQuickFlightReservation(int discountPoints, FlightReservationDTO flightRes) {
		QuickFlightReservation qfr = quickFlightReservationRepository.findOneById(flightRes.getQuickReservationID());
		if (qfr == null)
			return new MessageDTO("Quick flight reservation does not exist.", ToasterType.ERROR.toString());
		if (qfr.getUser() != null) {
			return new MessageDTO("Quick flight reservation is already taken.", ToasterType.ERROR.toString());
		}

		if (discountPoints != 0)
			return new MessageDTO("Discount points cannot be used on quick reservation", ToasterType.ERROR.toString());

		RegisteredUser ru = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userPassport = flightRes.getPassengers()[0].getPassport();
		int numOfBags = flightRes.getPassengers()[0].getNumberOfBags();
		double price = numOfBags * qfr.getFlight().getPricePerBag();
		
		try {
			qfr.setDone(false);
			qfr.setDateOfReservation(new Date());
			qfr.getPassengerSeats().forEach(p -> {
				p.setName(ru.getFirstName());
				p.setSurname(ru.getLastName());
				p.setPassport(userPassport);
				p.setReservation(qfr);
			});
			qfr.setPrice(qfr.getPrice() + price);
			ru.getFlightReservations().add(qfr);
			qfr.setUser(ru);
			quickFlightReservationRepository.flush();
		} catch (OptimisticLockingFailureException ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return new MessageDTO("Quick flight reservation has just been taken", ToasterType.ERROR.toString());
		}
		userRepository.save(ru);
		mailService.sendFlightReservationMail(ru, qfr);
		return new MessageDTO("Reservation successfully made.", ToasterType.SUCCESS.toString());
	}

	public DetailedReservationDTO getDetailedReservation(String resID) {
		Long id = Long.parseLong(resID);
		FlightReservation flightRes = flightReservationRepository.findById(id).orElse(null);
		if (flightRes == null) {
			return null;
		}
		return new DetailedReservationDTO(flightRes);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
	public ResponseEntity<MessageDTO> cancelHotelReservation(String resID) {
		Long id = Long.parseLong(resID);
		FlightReservation fr = flightReservationRepository.findById(id).orElse(null);
		if (fr == null || fr.getHotelReservation() == null) {
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Reservation does not exist.", ToasterType.ERROR.toString()), HttpStatus.OK);
		}
		if (fr.getHotelReservation().getDone()) {
			return new ResponseEntity<MessageDTO>(new MessageDTO("You can not cancel hotel reservation which is done.",
					ToasterType.ERROR.toString()), HttpStatus.OK);
		}
		Hotel hotel = fr.getHotelReservation().getHotelRoom().getHotel();
		HotelReservation hr = fr.getHotelReservation();
		hotel.getReservations().removeIf(h -> {
			if (h.getId().longValue() == hr.getId().longValue()) {
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
		fr.setHotelReservation(null);
		flightReservationRepository.save(fr);
		hotelReservationRepository.delete(hr);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Successfully canceled hotel reservation.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
	public ResponseEntity<MessageDTO> cancelCarReservation(String resID) {
		Long id = Long.parseLong(resID);
		FlightReservation fr = flightReservationRepository.findById(id).orElse(null);
		if (fr == null || fr.getVehicleReservation() == null) {
			return new ResponseEntity<MessageDTO>(
					new MessageDTO("Reservation does not exist.", ToasterType.ERROR.toString()), HttpStatus.OK);
		}
		if (fr.getVehicleReservation().getDone()) {
			return new ResponseEntity<MessageDTO>(new MessageDTO("You can not cancel car reservation which is done.",
					ToasterType.ERROR.toString()), HttpStatus.OK);
		}
		VehicleReservation vr = fr.getVehicleReservation();
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
		fr.setVehicleReservation(null);
		flightReservationRepository.save(fr);
		vehicleReservationRepository.delete(vr);
		return new ResponseEntity<MessageDTO>(
				new MessageDTO("Successfully canceled car reservation.", ToasterType.SUCCESS.toString()), HttpStatus.OK);
	}
}
