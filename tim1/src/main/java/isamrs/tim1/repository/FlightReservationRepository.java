package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.FlightReservation;

public interface FlightReservationRepository extends JpaRepository<FlightReservation, Long>{

}
