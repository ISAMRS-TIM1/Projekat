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
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.HotelRoom;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.ServiceGrade;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.repository.AirlineRepository;
import isamrs.tim1.repository.HotelRepository;
import isamrs.tim1.repository.RentACarRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class PeriodicCheckService {

	@Autowired
	AirlineRepository airlineRepository;

	@Autowired
	HotelRepository hotelRepository;

	@Autowired
	RentACarRepository racRepository;

	@Value("${daysBeforeHotelResIsDone}")
	private int daysBeforeHotelResIsDone;

	@Value("${daysBeforeVehicleResIsDone}")
	private int daysBeforeVehicleResIsDone;

	@Value("${hoursBeforeFlightResIsDone}")
	private int hoursBeforeFlightResIsDone;

	// in order to run @Scheduled methods on server startup
	@PostConstruct
	public void onStartup() {
		checkReservations();
		deleteExpiredFlightInvitations();
		calculateAverageGrades();
	}

	@Scheduled(cron = "${checkReservations.cron}")
	public void checkReservations() {
		Date now = new Date();
		int numOfDays;
		int numOfHours;

		for (Airline airline : airlineRepository.findAll()) {
			for (FlightReservation fr : airline.getReservations()) {
				numOfHours = (int) ((now.getTime() - fr.getFlight().getDepartureTime().getTime()) / (1000 * 60 * 60))
						+ 1;
				if (numOfHours <= hoursBeforeFlightResIsDone) {
					fr.setDone(true);
				}
			}
		}

		for (Hotel hotel : hotelRepository.findAll()) {
			for (HotelReservation hr : hotel.getReservations()) {
				numOfDays = (int) ((now.getTime() - hr.getFromDate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
				hr.setDone(numOfDays <= daysBeforeHotelResIsDone);
			}
		}

		for (RentACar rac : racRepository.findAll()) {
			for (VehicleReservation vr : rac.getReservations()) {
				numOfDays = (int) ((now.getTime() - vr.getFromDate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
				vr.setDone(numOfDays <= daysBeforeVehicleResIsDone);
			}
		}
	}

	@Scheduled(cron = "${deleteExpiredFlightInvitations.cron}")
	public void deleteExpiredFlightInvitations() {
		// TODO
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
