/**
 * 
 */
package com.mks.helpdesk.api.security.model;

import com.mks.helpdesk.api.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Markus Souza on 20/03/2018
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUser {
	
	private String token;
	private User user;

}
