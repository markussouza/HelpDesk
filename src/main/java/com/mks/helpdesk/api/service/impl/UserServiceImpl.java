/**
 * 
 */
package com.mks.helpdesk.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mks.helpdesk.api.entity.User;
import com.mks.helpdesk.api.repository.UserRepository;
import com.mks.helpdesk.api.service.UserService;

/**
 * @author Markus Souza on 19/03/2018
 *
 */
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User createOrUpdate(User user) {
		return userRepository.save(user);
	}

	@Override
	public User findById(String id) {
		return userRepository.findOne(id);
	}

	@Override
	public void delete(String id) {
		userRepository.delete(id);
	}

	@Override
	public Page<User> findAll(int page, int count) {
		return userRepository.findAll(new PageRequest(page, count));
	}

}
