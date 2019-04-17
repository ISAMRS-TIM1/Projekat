package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.Service;

public interface ServiceRepository extends JpaRepository<Service, Integer> {

	Service findOneByName(String name);

}