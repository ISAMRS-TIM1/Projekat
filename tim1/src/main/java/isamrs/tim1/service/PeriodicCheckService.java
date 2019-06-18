package isamrs.tim1.service;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.model.Airline;
import isamrs.tim1.model.DiscountInfo;
import isamrs.tim1.model.FlightInvitation;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.repository.AirlineRepository;
import isamrs.tim1.repository.DiscountInfoRepository;
import isamrs.tim1.repository.FlightInvitationRepository;
import isamrs.tim1.repository.FlightReservationRepository;
import isamrs.tim1.repository.HotelRepository;
import isamrs.tim1.repository.HotelReservationRepository;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.repository.VehicleReservationRepository;

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
	
	@Autowired
	private FlightReservationRepository flightReservationRepository;
	
	@Autowired
	private HotelReservationRepository hotelReservationRepository;
	
	@Autowired
	private VehicleReservationRepository vehicleReservationRepository;

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
				if (!fr.getDone()) {
					numOfHours = Math.abs((int) ((fr.getFlight().getDepartureTime().getTime() - now.getTime()) / (1000 * 60 * 60)))
							+ 1;
					if (numOfHours <= hoursBeforeFlightResIsDone) {
						if (fr.getUser() != null) {
							fr.setDone(true);
							fr.getUser().setDiscountPoints((int) Math.floor((fr.getUser().getDiscountPoints()
									+ fr.getFlight().getFlightLength() / di.getKmsNeededForPoint())));
						}
						else {
							flightReservationRepository.delete(fr);
						}
					}
				}
			}
			airlineRepository.save(airline);
		}

		for (Hotel hotel : hotelRepository.findAll()) {
			for (HotelReservation hr : hotel.getReservations()) {
				if (!hr.getDone()) {
					numOfDays = (int) ((hr.getFromDate().getTime() - now.getTime()) / (1000 * 60 * 60 * 24)) + 1;
					if (numOfDays <= daysBeforeHotelResIsDone) {
						if (hr.getFlightReservation() != null) {
							hr.setDone(true);
						}
						else {
							hotelReservationRepository.delete(hr);
						}
					}
				}
			}
			hotelRepository.save(hotel);
		}

		for (RentACar rac : racRepository.findAll()) {
			for (VehicleReservation vr : rac.getReservations()) {
				if (!vr.getDone()) {
					numOfDays = (int) ((vr.getFromDate().getTime() - now.getTime()) / (1000 * 60 * 60 * 24)) + 1;
					if (numOfDays <= daysBeforeVehicleResIsDone) {
						if (vr.getFlightReservation() != null) {
							vr.setDone(true);
						}
						else {
							vehicleReservationRepository.delete(vr);
						}
					}
				}
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
		// TODO
	}

}
