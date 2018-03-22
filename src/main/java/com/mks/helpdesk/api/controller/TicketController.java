/**
 * 
 */
package com.mks.helpdesk.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mks.helpdesk.api.dto.Summary;
import com.mks.helpdesk.api.entity.ChangeStatus;
import com.mks.helpdesk.api.entity.Ticket;
import com.mks.helpdesk.api.entity.User;
import com.mks.helpdesk.api.enums.ProfileEnum;
import com.mks.helpdesk.api.enums.StatusEnum;
import com.mks.helpdesk.api.response.Response;
import com.mks.helpdesk.api.security.jwt.JwtTokenUtil;
import com.mks.helpdesk.api.service.TicketService;
import com.mks.helpdesk.api.service.UserService;

/**
 * @author Markus Souza on 21/03/2018
 *
 */
@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController {
	
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	protected JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/{page}/{count}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
	public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request, @PathVariable int page, @PathVariable int count) {
		Response<Page<Ticket>> response = new Response<>();
		Page<Ticket> tickets = null;
		User userRequest = userFromRequest(request);
		if (userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
			tickets = ticketService.listTicket(page, count);
		} else if (userRequest.getProfile().equals(ProfileEnum.ROLE_COSTUMER)) {
			tickets = ticketService.findByCurrentUser(page, count, userRequest.getId());
		}
		response.setData(tickets);
		return ResponseEntity.ok(response);
		
	}
	
	@GetMapping("/{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
	public ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request, 
													@PathVariable int page,
													@PathVariable int count,
													@PathVariable Integer number,
													@PathVariable String title,
													@PathVariable String status,
													@PathVariable String priority,
													@PathVariable boolean assigned) {
		
		title = title.equals("uninformed") ? "" : title;
		status = status.equals("uninformed") ? "" : status;
		priority = priority.equals("uninformed") ? "" : priority;
		
		Response<Page<Ticket>> response = new Response<>();
		Page<Ticket> tickets = null;
		if (number >  0) {
			tickets = ticketService.findByNumber(page, count, number);
		} else {
			User userRequest = userFromRequest(request);
			if (userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
				if (assigned) {
					tickets = ticketService.findByParametersAndAssignedUser(page, count, title, status, priority, userRequest.getId());
				} else {
					tickets = ticketService.findByParameters(page, count, title, status, priority);
				}
			} else if (userRequest.getProfile().equals(ProfileEnum.ROLE_COSTUMER)) {
				tickets = ticketService.findByParametersAndCurrentUser(page, count, title, status, priority, userRequest.getId());
			}
		}
		response.setData(tickets);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
	public ResponseEntity<Response<Ticket>> findById(@PathVariable String id, BindingResult result) {
		Response<Ticket> response = new Response<>();
		Ticket ticket = ticketService.findById(id);
		if (ticket == null) {
			response.getErrors().add("Register not found id: " + id);
			return ResponseEntity.badRequest().body(response);
		}
		List<ChangeStatus> changes = new ArrayList<>();
		Iterable<ChangeStatus> changesCurrent = ticketService.listChangeStatus(ticket.getId());
		for (Iterator<ChangeStatus> iterator = changesCurrent.iterator(); iterator.hasNext();) {
			ChangeStatus changeStatus = (ChangeStatus) iterator.next();
			changeStatus.setTicket(null);
			changes.add(changeStatus);
		}
		ticket.setChange(changes);
		response.setData(ticket);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{summary}")
	public ResponseEntity<Response<Summary>> findSummary() {
		Response<Summary> response = new Response<>();
		Summary summary = new Summary();
		int amountNew = 0;
		int amountResolved = 0;
		int amountApproved = 0;
		int amountDisapproved = 0;
		int amountAssigned = 0;
		int amountClosed = 0;
		
		Iterable<Ticket> tickets = ticketService.findAll();
		if (tickets != null) {
			for (Iterator<Ticket> iterator = tickets.iterator(); iterator.hasNext();) {
				Ticket ticket = (Ticket) iterator.next();
				if (ticket.getStatus().equals(StatusEnum.New)) {
					amountNew++;
				} else if (ticket.getStatus().equals(StatusEnum.Resolved)) {
					amountResolved++;
				} else if (ticket.getStatus().equals(StatusEnum.Approved)) {
					amountApproved++;
				} else if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
					amountDisapproved++;
				} else if (ticket.getStatus().equals(StatusEnum.Assigned)) {
					amountAssigned++;
				} else if (ticket.getStatus().equals(StatusEnum.Closed)){
					amountClosed++;
				}
			}
		}
		summary.setAmountNew(amountNew);
		summary.setAmountResolved(amountResolved);
		summary.setAmountApproved(amountApproved);
		summary.setAmountDisapproved(amountDisapproved);
		summary.setAmountAssigned(amountAssigned);
		summary.setAmountClosed(amountClosed);
		
		response.setData(summary);
		return ResponseEntity.ok(response);
		
	}
	
	@PostMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<Ticket>> create(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result) {
		Response<Ticket> response = new Response<>();
		try {
			validateCreateTicket(ticket, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			ticket.setStatus(StatusEnum.getStatus("New"));
			ticket.setUser(userFromRequest(request));
			ticket.setDate(new Date());
			ticket.setNumber(generateNumber());
			response.setData(ticketService.createOrUpdate(ticket));
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result) {
		Response<Ticket> response = new Response<>();
		try {
			validateUpdateTicket(ticket, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			Ticket ticketCurrent = ticketService.findById(ticket.getId());
			ticket.setStatus(ticketCurrent.getStatus());
			ticket.setUser(ticketCurrent.getUser());
			ticket.setDate(ticketCurrent.getDate());
			ticket.setNumber(ticketCurrent.getNumber());
			if (ticketCurrent.getAssignedUser() != null) {
				ticket.setAssignedUser(ticketCurrent.getAssignedUser());
			}
			response.setData(ticketService.createOrUpdate(ticket));
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}/{status}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
	public ResponseEntity<Response<Ticket>> changStatus(HttpServletRequest request,
														@PathVariable String id,
														@PathVariable String status,
														@RequestBody Ticket ticket, 
														BindingResult result) {
		Response<Ticket> response = new Response<>();
		try {
			validateChangStatus(id, status, result);
			if (result.hasErrors()) {
				result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(response);
			}
			Ticket ticketCurrent = ticketService.findById(ticket.getId());
			ticketCurrent.setStatus(StatusEnum.getStatus(status));
			if (status.equals("Assigned")) {
				ticketCurrent.setAssignedUser(userFromRequest(request));
			}
			Ticket ticketPresited = ticketService.createOrUpdate(ticketCurrent);
			ChangeStatus changeStatus = new ChangeStatus();
			changeStatus.setUserChange(userFromRequest(request));
			changeStatus.setDateChangeStatus(new Date());
			changeStatus.setStatus(StatusEnum.getStatus(status));
			changeStatus.setTicket(ticketPresited);
			ticketService.createChangeStatus(changeStatus);
			response.setData(ticketPresited);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<Response<String>> delete(@PathVariable String id, BindingResult result) {
		Response<String> response = new Response<>();
		Ticket ticket = ticketService.findById(id);
		if (ticket == null) {
			response.getErrors().add("Register not found id: " + id);
			return ResponseEntity.badRequest().body(response);
		}
		ticketService.delete(id);
		return ResponseEntity.ok(new Response<String>());
	}
	
	private Integer generateNumber() {
		Random random = new Random();
		return random.nextInt(9999);
	}

	private User userFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		String email = jwtTokenUtil.getUsernameFromToken(token);
		return userService.findByEmail(email);
	}
	
	private void validateUpdateTicket(Ticket ticket, BindingResult result) {
		if (ticket.getId() == null) {
			result.addError(new ObjectError("Ticket", "Id no information."));
		}
		if (ticket.getTitle() == null) {
			result.addError(new ObjectError("Ticket", "Title no information."));
		}
	}

	private void validateCreateTicket(Ticket ticket, BindingResult result) {
		if (ticket.getTitle() == null) {
			result.addError(new ObjectError("Ticket", "Ticket no information."));
		}
	}
	
	private void validateChangStatus(String id, String status, BindingResult result) {
		if (id == null || id.equals("")) {
			result.addError(new ObjectError("Ticket", "Id no information."));
			return;
		}
		if (status == null || status.equals("")) {
			result.addError(new ObjectError("Ticket", "Status no information."));
			return;
		}
	}

}
