package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.HotelReservation;


public interface HotelReservationRepository extends JpaRepository<HotelReservation, Long> {

}
