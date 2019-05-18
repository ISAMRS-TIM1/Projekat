package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>{

}
