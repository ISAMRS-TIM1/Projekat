package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.QuickVehicleReservation;

public interface QuickVehicleReservationRepository extends JpaRepository<QuickVehicleReservation, Long> {
	QuickVehicleReservation findOneById(Long id);
}
