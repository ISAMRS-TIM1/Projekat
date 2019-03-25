package isamrs.tim1.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import isamrs.tim1.model.User;

@Service
public class UserService {
	
	@PersistenceContext
	protected EntityManager manager;
	
	@SuppressWarnings("unchecked")
	@Transactional
	public boolean editProfile(User user) throws Exception {
		Query query = manager.createQuery("SELECT u FROM User u WHERE u.email=?1");
		query.setParameter(1, user.getEmail());
		List<User> usersFound = query.getResultList();
        if (usersFound.isEmpty()) {
            return false;
        }
        User userToEdit = usersFound.get(0);
        userToEdit.setFirstName(user.getFirstName());
        userToEdit.setLastName(user.getLastName());
        userToEdit.setAddress(user.getAddress());
        userToEdit.setPhoneNumber(user.getPhoneNumber());
        manager.persist(userToEdit);
        return true;
	}

}
