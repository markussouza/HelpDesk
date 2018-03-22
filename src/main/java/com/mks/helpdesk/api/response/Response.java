/**
 * 
 */
package com.mks.helpdesk.api.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Markus Souza on 20/03/2018
 *
 */
@Data
public class Response<T> {
	
	private T data;
	private List<String> errors;
	
	public List<String> getErrors() {
		if (this.errors == null) {
			this.errors = new ArrayList<>();
		}
		return this.errors;
	}

}
