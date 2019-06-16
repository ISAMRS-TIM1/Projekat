package isamrs.tim1.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {

	Hotel findOneByName(String name);
	
	@Query(value = "select * from hotels h inner join locations l on h.location = l.location_id where h.name like %?1% and h.average_grade between ?2 and ?3 and l.country like ?4", nativeQuery = true)
	ArrayList<Hotel> findByNameGradeCountry(String name, double fromGrade, double toGrade, String country);
}
