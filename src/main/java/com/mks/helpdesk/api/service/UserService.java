/**
 * 
 */
package com.mks.helpdesk.api.service;

import org.springframework.data.domain.Page;

import com.mks.helpdesk.api.entity.User;

/**
 * @author Markus Souza on 19/03/2018
 *
 */
public interface UserService {
	
	User findByEmail(String email);
	
	User createOrUpdate(User user);
	
	User findById(String id);
	
	void delete(String id);
	
	Page<User> findAll(int page, int count);

}
