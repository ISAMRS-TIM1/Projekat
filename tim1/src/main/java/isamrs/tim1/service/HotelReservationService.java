package isamrs.tim1.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import isamrs.tim1.model.HotelAdditionalService;
import isamrs.tim1.model.HotelReservation;
import isamrs.tim1.model.SeasonalHotelRoomPrice;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class HotelReservationService {

	public double calculateReservationPrice(HotelReservation res) {
		double price = 0;
		for(HotelAdditionalService has : res.getAdditionalServices()) {
			price+= has.getPrice();
		}
		double roomPricePerDay = res.getHotelRoom().getDefaultPriceOneNight();
		Date firstDay = res.getFromDate();
		for(SeasonalHotelRoomPrice sp : res.getHotelRoom().getSeasonalPrices()) {
			if( firstDay.after(sp.getFromDate()) && firstDay.before(sp.getToDate())) {
				roomPricePerDay= sp.getPrice();
				break;
			}
		}
		int numOfDays = (int)( (res.getToDate().getTime() - res.getFromDate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
		price += roomPricePerDay * numOfDays;
		return price;
	}
}
