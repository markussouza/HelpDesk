/**
 * 
 */
package com.mks.helpdesk.api.enums;

/**
 * @author Markus Souza on 19/03/2018
 *
 */
public enum StatusEnum {
	
	New,
	Assigned,
	Resolved,
	Approved,
	Disapproved,
	Closed;
	
	public static StatusEnum getStatus(String status) {
		switch (status) {
		case "New": 
			return New;
		case "Assigned": 
			return Assigned;
		case "Resolved": 
			return Resolved;
		case "Disapproved": 
			return Disapproved;
		case "Closed": 
			return Closed;

		default:
			return New;
		}
	}

}
