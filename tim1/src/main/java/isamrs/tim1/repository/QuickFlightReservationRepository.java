package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.QuickFlightReservation;

public interface QuickFlightReservationRepository extends JpaRepository<QuickFlightReservation, Long> {

}
