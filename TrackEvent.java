package assign11;

/**
 * Assignment 7: Music; CS1420 Represents a track event containing a sequence of
 * audio events. Extends the AudioEvent class.
 * 
 * @author Olivia Styler
 * @version November 22, 2024
 */

public class TrackEvent extends AudioEvent {
	private int duration;
	private SimpleSequencer sequencer;

	/**
	 * Constructs a TrackEvent with the specified time, track name, channel,
	 * duration, and sequencer.
	 * 
	 * @param time      the time at which the track event occurs
	 * @param trackName the name of the track
	 * @param channel   the channel number for the track
	 * @param duration  the duration of the track
	 * @param sequencer the SimpleSequencer managing the track
	 */
	public TrackEvent(int time, String trackName, int channel, int duration, SimpleSequencer sequencer) {
		super(time, trackName, channel);
		this.duration = duration;
		this.sequencer = sequencer;
	}

	/**
	 * Returns the duration of the track event.
	 * 
	 * @return the track duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Returns the sequence managed by the SimpleSequencer.
	 * 
	 * @return the SimpleSequencer managing this track
	 */
	public SimpleSequencer getSequence() {
		return sequencer;
	}

	/**
	 * Provides a string representation of the track event in the format:
	 * "trackName[channel, time, duration, sequenceLength]". Also includes a
	 * formatted list of each event in the sequence.
	 * 
	 * @return a string describing the track event and its sequence
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append("[").append(getChannel()).append(", ").append(getTime()).append(", ")
				.append(duration).append(", ").append(sequencer.getEventCount()).append("]\n");

		// Iterate over the sequencer to list all events
		for (AudioEvent event : sequencer) {
			sb.append("- ").append(event.toString()).append("\n");
		}

		return sb.toString();
	}

	/**
	 * Compares this TrackEvent with another AudioEvent for order, primarily by
	 * time. If the events have the same time, TrackEvents are considered to come
	 * after both ChangeEvents and NoteEvents.
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
		if (other instanceof NoteEvent || other instanceof ChangeEvent) {
			return 1; // TrackEvents should come last
		} else {
			return 0; // Same class, so equal ordering
		}
	}

	/**
	 * Executes the track event by starting the sequencer.
	 */
	@Override
	public void execute() {
		sequencer.start();
	}

	/**
	 * Completes the track event. Currently, this method does not perform any
	 * actions.
	 */
	@Override
	public void complete() {
		// Do nothing for now
	}

	/**
	 * Cancels the track event by stopping the sequencer.
	 */
	@Override
	public void cancel() {
        sequencer.stop();
	}
}
