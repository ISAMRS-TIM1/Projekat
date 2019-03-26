package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findOneByEmail(String email);
}
