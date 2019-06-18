package isamrs.tim1.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import isamrs.tim1.model.QuickFlightReservation;

public interface QuickFlightReservationRepository extends JpaRepository<QuickFlightReservation, Long> {
	
	@Lock(LockModeType.OPTIMISTIC)
	QuickFlightReservation findOneById(Long id);
}
