package isamrs.tim1.repository;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.VehicleReservation;

public interface VehicleReservationRepository extends JpaRepository<VehicleReservation, Long> {

	@Query(value = "select * from vehicle_reservations vr where vr.from_date >= ?1 and vr.to_date <= ?2 and vr.vehicle = ?3", nativeQuery = true)
	ArrayList<VehicleReservation> findByDates(Date start, Date end, int vehicle);
}
