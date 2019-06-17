package isamrs.tim1.repository;

import java.util.Date;
import java.util.Set;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {

	Flight findOneByFlightCode(String flightCode);

	@Query(value = "select * from flights f where f.start_destination = ?1 "
			+ "and f.end_destination = ?2 and year(?3) = year(f.departure_time) and "
			+ "month(?3) = month(f.departure_time) and dayofmonth(?3) = dayofmonth(f.departure_time) "
			+ "and f.round_trip = ?4", nativeQuery = true)
	Set<Flight> searchFlights(Long startID, Long endID, Date departureTime, boolean roundTrip);
}