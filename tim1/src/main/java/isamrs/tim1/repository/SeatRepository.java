package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {

}