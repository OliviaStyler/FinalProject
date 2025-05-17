package assign11;

/**
 * Assignment 7: Music; CS1420 Represents an event that changes a property at a
 * specific time. Extends the AudioEvent class.
 * 
 * @author Olivia Styler
 * @version November 22, 2024
 */
public class ChangeEvent extends AudioEvent {
	private int value;
	private SimpleSynthesizer synthesizer;

	/**
	 * Constructs a ChangeEvent with the specified time, type, channel, and new
	 * value.
	 * 
	 * @param time    the time at which the change event occurs
	 * @param type    the type of change event
	 * @param channel the channel number for the change
	 * @param value   the new value for the property being changed
	 */
	public ChangeEvent(int time, String type, int channel, int value, SimpleSynthesizer synthesizer) {
		super(time, type, channel);
		this.value = value;
		this.synthesizer = synthesizer;
	}

	/**
	 * Returns the new value for the property being changed.
	 * 
	 * @return the new value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Provides a string representation of the change event in the format:
	 * "type[channel, time, value]".
	 * 
	 * @return a string describing the change event
	 */
	@Override
	public String toString() {
		return getName() + "[" + getChannel() + ", " + getTime() + ", " + value + "]";
	}

	/**
	 * Compares this ChangeEvent with another AudioEvent for order, primarily by
	 * time. If the events have the same time, ChangeEvents come before both
	 * NoteEvents and TrackEvents.
	 * 
	 * @param other the AudioEvent to be compared
	 * @return a negative value if this event comes before the other, a positive
	 *         value if it comes after, and 0 if they are considered equal.
	 */
	@Override
	public int compareTo(AudioEvent other) {

		if (this.getTime() != other.getTime()) {
			return Integer.compare(this.getTime(), other.getTime());
		}

		if (other instanceof NoteEvent || other instanceof TrackEvent) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * Executes the change event by printing a description of the event.
	 */
	@Override
	public void execute() {

	}

	/**
	 * Completes the change event. Currently, this method does not perform any
	 * actions.
	 */
	@Override
	public void complete() {
		// Do nothing for now
	}

	/**
	 * Cancels the change event. Currently, this method does not perform any
	 * actions.
	 */
	@Override
	public void cancel() {
		// Do nothing for now
	}
}
