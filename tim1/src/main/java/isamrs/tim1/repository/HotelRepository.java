package isamrs.tim1.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {

	Hotel findOneByName(String name);
	
	@Query(value = "select * from hotels h where h.name like %?1% and h.average_grade between ?2 and ?3", nativeQuery = true)
	ArrayList<Hotel> findByNameGrade(String name, double fromGrade, double toGrade);
}
