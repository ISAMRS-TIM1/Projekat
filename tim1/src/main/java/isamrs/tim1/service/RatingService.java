package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.dto.ReservationGradeDTO;
import isamrs.tim1.dto.ServiceGradeDTO;
import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.ServiceGrade;
import isamrs.tim1.model.VehicleReservation;
import isamrs.tim1.repository.FlightReservationRepository;
import isamrs.tim1.repository.HotelReservationRepository;
import isamrs.tim1.repository.ServiceRepository;
import isamrs.tim1.repository.VehicleReservationRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class RatingService {

	@Autowired
	private ServiceRepository serviceRepository;

	@Autowired
	private VehicleReservationRepository vehicleReservationRepository;

	@Autowired
	private FlightReservationRepository flightReservationRepository;

	@Autowired
	private HotelReservationRepository hotelReservationRepository;

	public void rateService(ServiceGradeDTO serviceGrade) {
		isamrs.tim1.model.Service s = serviceRepository.findOneByName(serviceGrade.getServiceName());
		RegisteredUser user = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		ServiceGrade g = new ServiceGrade(user, s, serviceGrade.getGrade());

		if (s.getServiceGrades().contains(g)) {
			s.getServiceGrades().remove(g);
			s.getServiceGrades().add(g);
		} else {
			s.getServiceGrades().add(g);
		}

		serviceRepository.save(s);
	}

	public void rateReservation(ReservationGradeDTO reservationGrade) {
		switch (reservationGrade.getType()) {
		case FLIGHT:
			FlightReservation fr = flightReservationRepository.findById(reservationGrade.getId()).get();
			fr.setGrade(reservationGrade.getGrade());
			flightReservationRepository.save(fr);
			break;
		case ROOM:
			HotelReservation hr = hotelReservationRepository.findById(reservationGrade.getId()).get();
			hr.setGrade(reservationGrade.getGrade());
			hotelReservationRepository.save(hr);
			break;
		case VEHICLE:
			VehicleReservation vr = vehicleReservationRepository.findById(reservationGrade.getId()).get();
			vr.setGrade(reservationGrade.getGrade());
			vehicleReservationRepository.save(vr);
			break;
		default:
			break;
		}
	}
}
