package isamrs.tim1.repository;

import java.util.ArrayList;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.HotelRoom;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Integer> {
	@Query(value = "select * from hotel_rooms r where r.room_number = ?1 and r.hotel = ?2 and r.deleted = false", nativeQuery = true)
	HotelRoom findOneByNumberAndHotel(String roomNumber, Integer hotel);

	@Query(value = "select * from hotel_rooms hr where hr.hotel = ?1 and hr.number_of_people = ?2 and hr.average_grade between ?3 and ?4 and hr.deleted = false", nativeQuery = true)
	ArrayList<HotelRoom> findByHotelPeopleGrade(Integer hotel, int forPeople, double fromGrade, double toGrade);

	//@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select hr from HotelRoom hr join hr.hotel h where hr.roomNumber = ?1 and h.name = ?2 and hr.deleted = false")
	HotelRoom findOneByNumberAndHotelName(String roomNumber, String hotelName);

}
