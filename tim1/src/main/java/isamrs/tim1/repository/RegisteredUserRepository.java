package isamrs.tim1.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.RegisteredUser;

public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {

	RegisteredUser findOneByEmail(String email);

	@Query(value = "select * from users u where u.first_name like ?1 and u.last_name like ?2 and u.email not like ?3 and dtype like 'RegisteredUser'", nativeQuery = true)
	Set<RegisteredUser> findByFirstAndLastName(String firstName, String lastName, String email);
	
}
