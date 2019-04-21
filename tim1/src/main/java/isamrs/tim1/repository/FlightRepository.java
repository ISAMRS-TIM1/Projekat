package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long>{

}
