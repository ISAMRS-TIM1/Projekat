package isamrs.tim1.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.model.Airline;
import isamrs.tim1.model.Flight;
import isamrs.tim1.model.DiscountInfo;
import isamrs.tim1.model.FlightInvitation;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.HotelRoom;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.ServiceGrade;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.repository.AirlineRepository;
import isamrs.tim1.repository.DiscountInfoRepository;
import isamrs.tim1.repository.FlightInvitationRepository;
import isamrs.tim1.repository.HotelRepository;
import isamrs.tim1.repository.RentACarRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class PeriodicCheckService {

	@Autowired
	private AirlineRepository airlineRepository;

	@Autowired
	private HotelRepository hotelRepository;

	@Autowired
	private RentACarRepository racRepository;

	@Autowired
	private DiscountInfoRepository discountInfoRepository;

	@Autowired
	private FlightInvitationRepository flightInvitationRepository;

	@Value("${daysBeforeHotelResIsDone}")
	private int daysBeforeHotelResIsDone;

	@Value("${daysBeforeVehicleResIsDone}")
	private int daysBeforeVehicleResIsDone;

	@Value("${hoursBeforeFlightResIsDone}")
	private int hoursBeforeFlightResIsDone;

	@Value("${hoursBeforeFlightInvExpires}")
	private int hoursBeforeFlightInvExpires;

	@Value("${daysAfterInvFlightInvExpires}")
	private int daysAfterInvFlightInvExpires;

	@Value("${discount.percentagePerPoint}")
	private double discountPercentagePerPoint;

	@Value("${discount.kmsNeededForPoint}")
	private double kmsNeededForPoint;

	@Value("${discount.maxDiscountPoints}")
	private double maxDiscountPoints;

	@Value("${discount.discountPerExtraReservation}")
	private double discountPerExtraReservation;

	// in order to run @Scheduled methods on server startup
	@PostConstruct
	public void onStartup() {
		if (discountInfoRepository.findAll().isEmpty()) {
			DiscountInfo di = new DiscountInfo();
			di.setId(null);
			di.setDiscountPercentagePerPoint(discountPercentagePerPoint);
			di.setKmsNeededForPoint(kmsNeededForPoint);
			di.setMaxDiscountPoints(maxDiscountPoints);
			di.setDiscountPerExtraReservation(discountPerExtraReservation);
			discountInfoRepository.save(di);
		}
		checkReservations();
		deleteExpiredFlightInvitations();
		calculateAverageGrades();
	}

	@Scheduled(cron = "${checkReservations.cron}")
	public void checkReservations() {
		Date now = new Date();
		int numOfDays;
		int numOfHours;
		DiscountInfo di = discountInfoRepository.findAll().get(0);

		for (Airline airline : airlineRepository.findAll()) {
			for (FlightReservation fr : airline.getReservations()) {
				numOfHours = (int) ((now.getTime() - fr.getFlight().getDepartureTime().getTime()) / (1000 * 60 * 60))
						+ 1;
				if (numOfHours <= hoursBeforeFlightResIsDone) {
					fr.setDone(true);
					fr.getUser().setDiscountPoints((int) Math.floor((fr.getUser().getDiscountPoints()
							+ fr.getFlight().getFlightLength() / di.getKmsNeededForPoint())));
				}
			}
			airlineRepository.save(airline);
		}

		for (Hotel hotel : hotelRepository.findAll()) {
			for (HotelReservation hr : hotel.getReservations()) {
				numOfDays = (int) ((now.getTime() - hr.getFromDate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
				hr.setDone(numOfDays <= daysBeforeHotelResIsDone);
			}
			hotelRepository.save(hotel);
		}

		for (RentACar rac : racRepository.findAll()) {
			for (VehicleReservation vr : rac.getReservations()) {
				numOfDays = (int) ((now.getTime() - vr.getFromDate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
				vr.setDone(numOfDays <= daysBeforeVehicleResIsDone);
			}
			racRepository.save(rac);
		}
	}

	@Scheduled(cron = "${deleteExpiredFlightInvitations.cron}")
	public void deleteExpiredFlightInvitations() {
		Date now = new Date();
		int numOfDays;
		int numOfHours;
		for (FlightInvitation fi : flightInvitationRepository.findAll()) {
			numOfHours = (int) ((fi.getFlightReservation().getFlight().getDepartureTime().getTime() - now.getTime())
					/ (1000 * 60 * 60)) + 1;
			numOfDays = (int) ((now.getTime() - fi.getDateOfInviting().getTime()) / (1000 * 60 * 60 * 24)) + 1;
			if (numOfHours < hoursBeforeFlightInvExpires || numOfDays > daysAfterInvFlightInvExpires) {
				flightInvitationRepository.delete(fi);
			}
		}
	}

	@Scheduled(cron = "${calculateAverageGrades.cron}")
	public void calculateAverageGrades() {
		List<RentACar> racs = racRepository.findAll();

		double serviceSum = 0;
		Set<ServiceGrade> serviceGrades;
		for (RentACar rac : racs) {
			serviceGrades = rac.getServiceGrades();

			for (ServiceGrade g : serviceGrades) {
				serviceSum += g.getGrade();
			}

			if (serviceGrades.isEmpty()) {
				rac.setAverageGrade(0.0);
			} else {
				rac.setAverageGrade(serviceSum / serviceGrades.size());
			}
			serviceSum = 0;

			double entitySum = 0;
			Set<VehicleReservation> vehicleReservations;
			for (Vehicle v : rac.getVehicles()) {
				vehicleReservations = v.getReservations();
				for (VehicleReservation vr : vehicleReservations) {
					if (vr.getDone() == true)
						entitySum += vr.getGrade();
				}

				if (vehicleReservations.isEmpty()) {
					v.setAverageGrade(0.0);
				} else {
					v.setAverageGrade(entitySum / vehicleReservations.size());
				}
				entitySum = 0;
			}
			racRepository.save(rac);
		}

		List<Airline> airs = airlineRepository.findAll();

		for (Airline air : airs) {
			serviceGrades = air.getServiceGrades();

			for (ServiceGrade g : serviceGrades) {
				serviceSum += g.getGrade();
			}

			if (serviceGrades.isEmpty()) {
				air.setAverageGrade(0.0);
			} else {
				air.setAverageGrade(serviceSum / serviceGrades.size());
			}
			serviceSum = 0;

			double entitySum = 0;
			Set<FlightReservation> flightReservations;
			for (Flight f : air.getFlights()) {
				flightReservations = f.getReservations();
				for (FlightReservation fr : flightReservations) {
					if (fr.getDone() == true)
						entitySum += fr.getGrade();
				}

				if (flightReservations.isEmpty()) {
					f.setAverageGrade(0.0);
				} else {
					f.setAverageGrade(entitySum / flightReservations.size());
				}
				entitySum = 0;
			}
			airlineRepository.save(air);
		}

		List<Hotel> hotels = hotelRepository.findAll();

		for (Hotel h : hotels) {
			serviceGrades = h.getServiceGrades();

			for (ServiceGrade g : serviceGrades) {
				serviceSum += g.getGrade();
			}

			if (serviceGrades.isEmpty()) {
				h.setAverageGrade(0.0);
			} else {
				h.setAverageGrade(serviceSum / serviceGrades.size());
			}
			serviceSum = 0;

			double entitySum = 0;
			Set<HotelReservation> hotelReservations;
			for (HotelRoom hr : h.getRooms()) {
				hotelReservations = hr.getReservations();
				for (HotelReservation res : hotelReservations) {
					if (res.getDone() == true)
						entitySum += res.getGrade();
				}

				if (hotelReservations.isEmpty()) {
					h.setAverageGrade(0.0);
				} else {
					h.setAverageGrade(entitySum / hotelReservations.size());
				}
				entitySum = 0;
			}
			hotelRepository.save(h);
		}
	}
}
