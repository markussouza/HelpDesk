/**
 * 
 */
package com.mks.helpdesk.api.service;

import org.springframework.data.domain.Page;

import com.mks.helpdesk.api.entity.ChangeStatus;
import com.mks.helpdesk.api.entity.Ticket;

/**
 * @author Markus Souza on 21/03/2018
 *
 */
public interface TicketService {
	
	Ticket createOrUpdate(Ticket ticket);
	
	void delete(String id);
	
	Ticket findById(String id);
	
	Page<Ticket> listTicket(int page, int count);
	
	ChangeStatus createChangeStatus(ChangeStatus changeStatus);
	
	Iterable<ChangeStatus> listChangeStatus(String ticketId);
	
	Page<Ticket> findByCurrentUser(int page, int count, String userId);
	
	Page<Ticket> findByParameters(int page, int count, String title, String status, String priority);
	
	Page<Ticket> findByParametersAndCurrentUser(int page, int count, String title, String status, String priority, String userId);
	
	Page<Ticket> findByNumber(int page, int count, Integer number);
	
	Iterable<Ticket> findAll();
	
	Page<Ticket> findByParametersAndAssignedUser(int page, int count, String title, String status, String priority, String assignedUserId);
	
	
 }
