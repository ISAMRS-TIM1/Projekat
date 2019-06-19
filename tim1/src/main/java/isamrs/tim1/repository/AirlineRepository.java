package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import isamrs.tim1.model.Airline;

public interface AirlineRepository extends JpaRepository<Airline, Integer> {

	Airline findOneByName(String name);
}
