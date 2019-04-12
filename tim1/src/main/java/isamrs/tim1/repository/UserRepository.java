package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findOneByEmail(String email);

	@Query(value = "select * from users u where u.user_id =(select t.user from verification_tokens t where t.token = ?1)", nativeQuery = true)
	User findByToken(String token);
}
