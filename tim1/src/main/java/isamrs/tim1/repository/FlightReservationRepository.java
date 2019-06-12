package isamrs.tim1.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.FlightReservation;

public interface FlightReservationRepository extends JpaRepository<FlightReservation, Long>{
	
	@Query(value = "select * from flight_reservations fr where fr.user = ?1", nativeQuery = true)
	Set<FlightReservation> getByUser(Long id);
}
