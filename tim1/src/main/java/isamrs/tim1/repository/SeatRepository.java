package isamrs.tim1.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import isamrs.tim1.model.PlaneSegment;
import isamrs.tim1.model.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	
	@Lock(LockModeType.OPTIMISTIC)
	Seat findOneByRowAndColumnAndPlaneSegment(int row, int column, PlaneSegment planeSegId);

}
