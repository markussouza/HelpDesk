/**
 * 
 */
package com.mks.helpdesk.api.security.jwt;

import java.io.Serializable;

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
public class JwtAuthenticationRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String email;
	private String password;

}
