package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.PassengerSeat;


public interface PassengerSeatRepository extends JpaRepository<PassengerSeat, Long> {

}
