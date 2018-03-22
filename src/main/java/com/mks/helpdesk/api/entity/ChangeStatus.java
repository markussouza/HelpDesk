/**
 * 
 */
package com.mks.helpdesk.api.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mks.helpdesk.api.enums.StatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Markus Souza on 19/03/2018
 *
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatus {
	
	@Id
	private String id;
	
	@DBRef
	private Ticket ticket;
	
	@DBRef
	private User userChange;
	
	private Date dateChangeStatus;
	
	private StatusEnum status;

}
