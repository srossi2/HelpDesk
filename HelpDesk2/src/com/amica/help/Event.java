package com.amica.help;

import static com.amica.help.Ticket.Status;

import lombok.Getter;

/**
 * Represents an event in a ticket's history.
 *
 * @author Will Provost
 */
@Getter
public class Event {

	private long timestamp;
	private Status newStatus;
	private String note;
  
	public Event(String note) {
		this(null, note);
	}
  
	public Event(Status newStatus, String note) {
		this.timestamp= Clock.getTime();
		this.newStatus = newStatus;
		this.note = note;
	}

	@Override
	public String toString() {
		String result = "Event: " + note;
		if (newStatus != null) {
			result += " [" + newStatus + "]";
		}
		result += " (" + Clock.format(timestamp) + ")";
		return result;
	}
}
