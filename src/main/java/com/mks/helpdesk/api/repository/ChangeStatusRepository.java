/**
 * 
 */
package com.mks.helpdesk.api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mks.helpdesk.api.entity.ChangeStatus;

/**
 * @author Markus Souza on 19/03/2018
 *
 */
public interface ChangeStatusRepository extends MongoRepository<ChangeStatus, String> {
	
	Iterable<ChangeStatus> findByTicketIdOrderByDateChangeStatusDesc(String changeId);

}
