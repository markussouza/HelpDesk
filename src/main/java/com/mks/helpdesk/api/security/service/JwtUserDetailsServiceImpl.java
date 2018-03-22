/**
 * 
 */
package com.mks.helpdesk.api.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mks.helpdesk.api.entity.User;
import com.mks.helpdesk.api.security.jwt.JwtUserFactory;
import com.mks.helpdesk.api.service.UserService;

/**
 * @author Markus Souza on 19/03/2018
 *
 */
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userService.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("No user found with username '%s'.", email));
		}
		return JwtUserFactory.create(user);
	}

}
