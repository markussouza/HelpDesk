/**
 * 
 */
package com.mks.helpdesk.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mks.helpdesk.api.entity.Ticket;

/**
 * @author Markus Souza on 19/03/2018
 *
 */
public interface TicketRepository extends MongoRepository<Ticket, String> {
	
	Ticket findById(String id);
	
	Page<Ticket> findByUserIdOrderByDateDesc(Pageable pages, String userId);
	
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDesc(String title, String status, String priority, Pageable pages);
	
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(String title, String status, String priority, String userId, Pageable pages);
	
	Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDateDesc(String title, String status, String priority, String assignedUserId, Pageable pages);
	
	Page<Ticket> findByNumber(Integer number, Pageable pages);
	

}
