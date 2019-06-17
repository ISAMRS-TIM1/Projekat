package isamrs.tim1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.model.VehicleReservation;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class VehicleReservationService {

	public double calculateReservationPrice(VehicleReservation vr) {
		double price = 0;
		int numOfDays = (int) ((vr.getToDate().getTime() - vr.getFromDate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
		price += vr.getVehicle().getPricePerDay() * numOfDays;

		return price;
	}
}
