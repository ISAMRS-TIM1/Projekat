package isamrs.tim1.repository;

import java.util.ArrayList;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.QuickVehicleReservation;

public interface QuickVehicleReservationRepository extends JpaRepository<QuickVehicleReservation, Long> {

	@Lock(LockModeType.OPTIMISTIC)
	QuickVehicleReservation findOneById(Long id);

	@Query(value = "select * from vehicle_reservations vr where vr.vehicle = ?1", nativeQuery = true)
	ArrayList<QuickVehicleReservation> findAllByVehicle(int vehicle);
}
