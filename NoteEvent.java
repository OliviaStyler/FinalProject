package assign11;

/**
 * Assignment 7: Music; CS1420 Represents a musical note event with a specific
 * duration and pitch. Extends the AudioEvent class.
 * 
 * @author Olivia Styler
 * @version November 22, 2024
 */
public class NoteEvent extends AudioEvent {
	private int duration;
	private int pitch;
	private SimpleSynthesizer synthesizer;

	/**
	 * Constructs a NoteEvent with the specified time, instrument name, channel,
	 * duration, and pitch.
	 * 
	 * @param time       the time at which the note event occurs
	 * @param instrument the instrument name
	 * @param channel    the channel number for the note
	 * @param duration   the duration of the note
	 * @param pitch      the pitch of the note
	 */
	public NoteEvent(int time, String instrument, int channel, int duration, int pitch, SimpleSynthesizer synthesizer) {
		super(time, instrument, channel);
		this.duration = duration;
		this.pitch = pitch;
		this.synthesizer = synthesizer;
	}

	/**
	 * Returns the duration of the note event.
	 * 
	 * @return the note duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Returns the pitch of the note event.
	 * 
	 * @return the note pitch
	 */
	public int getPitch() {
		return pitch;
	}

	/**
	 * Provides a string representation of the note event in the format:
	 * "instrument[channel, time, duration, pitch]".
	 * 
	 * @return a string describing the note event
	 */
	@Override
	public String toString() {
		return getName() + "[" + getChannel() + ", " + getTime() + ", " + duration + ", " + pitch + "]";
	}

	/**
	 * Compares this NoteEvent with another AudioEvent for order, primarily by time.
	 * If the events have the same time, the events are ordered as follows:
	 * ChangeEvents are first, followed by NoteEvents, and TrackEvents are last.
	 * 
	 * @param other the AudioEvent to be compared
	 * @return a negative value if this event comes before the other, a positive
	 *         value if it comes after, and 0 if they are considered equal.
	 */
	@Override
	public int compareTo(AudioEvent other) {
		// First compare by time
		if (this.getTime() != other.getTime()) {
			return Integer.compare(this.getTime(), other.getTime());
		}
		// If times are equal, apply class ordering
		if (other instanceof ChangeEvent) {
			return 1; // ChangeEvents should come before NoteEvents
		} else if (other instanceof TrackEvent) {
			return -1; // NoteEvents should come before TrackEvents
		} else {
			return 0; // Same class, so equal ordering
		}
	}

	/**
	 * Executes the note event by printing a description of the event.
	 */
	@Override
	public void execute() {
		synthesizer.noteOn(getChannel(), pitch);
	}

	/**
	 * Completes the note event. Currently, this method does not perform any
	 * actions.
	 */
	@Override
	public void complete() {
		synthesizer.noteOff(getChannel(), pitch);
	}

	/**
	 * Cancels the note event. Currently, this method does not perform any actions.
	 */
	@Override
	public void cancel() {
		synthesizer.noteOff(getChannel(), pitch);
	}

}
