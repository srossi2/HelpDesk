package com.amica.help;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Class representing a problem ticket for a help desk.
 *
 * @author Will Provost
 */
@Getter
@EqualsAndHashCode(of="ID")
public class Ticket implements Comparable<Ticket> {

	public enum Status { CREATED, ASSIGNED, RESOLVED }
	public enum Priority { LOW, MEDIUM, HIGH, URGENT }

	private int ID;
	private Priority priority;
	private String originator;
	private String description;
	private Technician technician;
	private List<Event> history = new ArrayList<>();
	private SortedSet<Tag> tags = new TreeSet<>();

	public Ticket(int ID, String originator, String description, Priority priority) {
		this.ID = ID;
		this.priority = priority;
		this.originator = originator;
		this.description = description;
		this.history.add(new Event(Status.CREATED, "Created ticket."));
	}

	public Status getStatus() {
		return history.stream()
				.map(Event::getNewStatus)
				.filter(Objects::nonNull)
				.reduce((a,b) -> b)
				.get();
	}

	public Stream<Event> getHistory() {
		return history.stream();
	}

	public Stream<Tag> getTags() {
		return tags.stream();
	}
    
	public void assign(Technician technician) {
		if (getStatus() != Status.RESOLVED) {
			this.technician = technician;
			Status newStatus = Status.ASSIGNED;
			history.add(new Event(newStatus, "Assigned to " + technician + "."));
			technician.assignTicket(this);
		} else {
			throw new IllegalStateException("Can't re-assign a resolved new ticket.");
		}
	}

	public void addNote(String note) {
		history.add(new Event(note));
	}

	public void resolve(String reason) {
		if (getStatus() != Status.RESOLVED) {
			history.add(new Event(Status.RESOLVED, reason));
			technician.resolveTicket(this);
		} else {
			throw new IllegalStateException("Can't resolve a resolved ticket.");
		}
	}

	public boolean addTag(Tag tag) {
		return tags.add(tag);
	}

	public int getMinutesToResolve() {
		final int MILLISECONDS_PER_MINUTE = 60000;
		if (getStatus() == Status.RESOLVED) {
			long time = history.get(history.size() - 1).getTimestamp() - history.get(0).getTimestamp();
			return (int) time / MILLISECONDS_PER_MINUTE;
		} else {
			throw new IllegalStateException("The ticket is not yet resolved.");
		}
	}
    
	public boolean includesText(String text) {
		return description.contains(text) || getHistory()
				.anyMatch(e -> e.getNote().contains(text));
    }
    
	@Override
	public String toString() {
		return String.format("Ticket %d: %s priority, %s", 
				ID, priority.toString(), getStatus().toString());
	}
    
	public int compareTo(Ticket other) {
		if (this.equals(other)) {
			return 0;
		}

		int result = -priority.compareTo(other.getPriority());
		if (result == 0) {
			result = Integer.compare(ID, other.getID());
		}
		return result;
	}
}
