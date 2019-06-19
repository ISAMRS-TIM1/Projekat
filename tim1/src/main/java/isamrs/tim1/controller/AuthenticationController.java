package isamrs.tim1.controller;

import java.io.IOException;
import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.User;
import isamrs.tim1.model.UserTokenState;
import isamrs.tim1.security.auth.JwtAuthenticationRequest;
import isamrs.tim1.service.AuthenticationService;

@RestController
public class AuthenticationController {
	@Autowired
	private AuthenticationService authenticationService;

	@RequestMapping(value = "auth/confirm", method = RequestMethod.GET)
	public RedirectView confirmRegistration(@RequestParam String token) {
		return authenticationService.confirmRegistration(token);
	}

	@RequestMapping(value = "auth/register", method = RequestMethod.POST)
	public ResponseEntity<Object> register(@Valid @RequestBody User user) {
		return new ResponseEntity<Object>(authenticationService.register(user), HttpStatus.OK);
	}

	@RequestMapping(value = "auth/login", method = RequestMethod.POST)
	public ResponseEntity<Object> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
			HttpServletResponse response) throws AuthenticationException, IOException {
		return new ResponseEntity<Object>(
				authenticationService.createAuthenticationToken(authenticationRequest, response), HttpStatus.OK);
	}

	@RequestMapping(value = "auth/registerAdmin/{serviceName}", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> registerAdmin(@Valid @RequestBody User user,
			@PathVariable("serviceName") String serviceName) {
		return new ResponseEntity<MessageDTO>(authenticationService.registerServiceAdmin(user, serviceName),
				HttpStatus.OK);
	}

	@RequestMapping(value = "auth/changePassword", method = RequestMethod.PUT)
	public ResponseEntity<UserTokenState> changePassword(@RequestBody String password) {
		return new ResponseEntity<UserTokenState>(authenticationService.changePassword(password), HttpStatus.OK);
	}

	@RequestMapping(value = "auth/checkIfRegisteredUser", method = RequestMethod.GET)
	public ResponseEntity<Boolean> checkIfRegisteredUser() {
		boolean isRegisteredUser = true;
		try {
			@SuppressWarnings("unused")
			RegisteredUser regUser = (RegisteredUser) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
		} catch (Exception e) {
			isRegisteredUser = false;
		}
		return new ResponseEntity<Boolean>(isRegisteredUser, HttpStatus.OK);
	}
}
