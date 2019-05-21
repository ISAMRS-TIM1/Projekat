package isamrs.tim1.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.QuickVehicleReservation;

public interface QuickVehicleReservationRepository extends JpaRepository<QuickVehicleReservation, Long> {
	QuickVehicleReservation findOneById(Long id);
	
	@Query(value = "select * from vehicle_reservations vr where vr.vehicle = ?1", nativeQuery = true)
	ArrayList<QuickVehicleReservation> findAllByVehicle(int vehicle);
}
