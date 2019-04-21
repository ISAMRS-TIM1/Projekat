package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.Destination;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
	Destination findOneByName(String name);
}
