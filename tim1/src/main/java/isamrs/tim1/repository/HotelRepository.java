package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.Airline;
import isamrs.tim1.model.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {

	Hotel findOneByName(String name);
}
