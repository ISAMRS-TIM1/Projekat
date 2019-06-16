package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import isamrs.tim1.model.RentACar;

public interface RentACarRepository extends JpaRepository<RentACar, Integer> {
	RentACar findOneByName(String name);

	RentACar findOneById(Integer id);
}
