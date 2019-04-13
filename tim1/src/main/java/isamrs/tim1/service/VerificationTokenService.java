package isamrs.tim1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.model.VerificationToken;
import isamrs.tim1.repository.VerificationTokenRepository;

@Service
public class VerificationTokenService {
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	public void saveToken(VerificationToken token) {
		verificationTokenRepository.save(token);
	}

}
