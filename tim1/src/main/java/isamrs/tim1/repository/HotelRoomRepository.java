package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.HotelRoom;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Integer>{
	@Query(value = "select * from hotel_rooms r where r.room_number = ?1 and r.hotel = ?2", nativeQuery = true)
	HotelRoom findOneByNumberAndHotel(String roomNumber, Integer hotel);
}
