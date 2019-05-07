package isamrs.tim1.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import isamrs.tim1.model.SeasonalHotelRoomPrice;

public interface SeasonalHotelRoomPriceRepository extends JpaRepository<SeasonalHotelRoomPrice, Integer> {
	@Query(value = "select * from SEASONAL_HOTEL_ROOM_PRICES sp where sp.from_date = ?1 and sp.to_date = ?2 and sp.hotel_room = ?3", nativeQuery = true)
	SeasonalHotelRoomPrice findOneByDatesAndRoom(Date from, Date to, Integer hotelRoom);
}
