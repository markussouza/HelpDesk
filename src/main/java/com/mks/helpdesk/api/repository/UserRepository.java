/**
 * 
 */
package com.mks.helpdesk.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mks.helpdesk.api.entity.User;

/**
 * @author Markus Souza on 19/03/2018
 *
 */
public interface UserRepository extends MongoRepository<User, String> {
	User findByEmail(String email);
}
