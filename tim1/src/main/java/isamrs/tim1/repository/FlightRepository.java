package isamrs.tim1.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long>{
	
	@Query(value = "select * from flights f where f.start_destination = ?1 and f.end_destination = ?2", nativeQuery = true)
	Set<Flight> searchFlightsByDestinations(Long startID, Long endID);
	
	Flight findOneByFlightCode(String flightCode);
}
