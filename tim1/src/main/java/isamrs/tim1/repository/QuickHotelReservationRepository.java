package isamrs.tim1.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import isamrs.tim1.model.QuickHotelReservation;

public interface QuickHotelReservationRepository extends JpaRepository<QuickHotelReservation, Long> {
	
	@Lock(LockModeType.OPTIMISTIC)
	QuickHotelReservation findOneById(Long id);
}
