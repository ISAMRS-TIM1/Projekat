package isamrs.tim1.repository;

import java.util.ArrayList;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelRoom;

public interface HotelRoomRepository extends JpaRepository<HotelRoom, Integer> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select r from HotelRoom r where r.roomNumber = :roomNumber and r.hotel = :hotel and r.deleted = false")
	HotelRoom findOneByNumberAndHotel(@Param("roomNumber") String roomNumber, @Param("hotel") Hotel hotel);
	
	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query(value = "select r from HotelRoom r where r.roomNumber  = :roomNumber and r.hotel = :hotel and r.deleted = false")
	HotelRoom findOneByNumberAndHotelForRead(@Param("roomNumber") String roomNumber, @Param("hotel") Hotel hotel);

	@Query(value = "select * from hotel_rooms hr where hr.hotel = ?1 and hr.number_of_people = ?2 and hr.average_grade between ?3 and ?4 and hr.deleted = false", nativeQuery = true)
	ArrayList<HotelRoom> findByHotelPeopleGrade(Integer hotel, int forPeople, double fromGrade, double toGrade);

}
