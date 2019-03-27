package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.model.User;
import isamrs.tim1.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public boolean editProfile(User user) throws Exception {
		User userToEdit = userRepository.findOneByEmail(user.getEmail());
		if (userToEdit == null) {
			return false;
		}
		userToEdit.setFirstName(user.getFirstName());
		userToEdit.setLastName(user.getLastName());
		userToEdit.setAddress(user.getAddress());
		userToEdit.setPhoneNumber(user.getPhoneNumber());
		try {
			userRepository.save(userToEdit);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

}
