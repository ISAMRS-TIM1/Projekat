package isamrs.tim1.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.UserReservation;

public interface UserReservationRepository extends JpaRepository<UserReservation, Long> {
	
	@Query(value = "select * from userreservations u where u.user = ?1", nativeQuery = true)
	Set<UserReservation> getByUser(Long id);
}
