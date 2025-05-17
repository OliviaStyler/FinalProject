package assign11;

/**
 * Assignment 7: Music; CS1420 Abstract class representing an audio event with a
 * specific time, name, and channel. Each subclass defines its own
 * implementation for executing, completing, and canceling the event. Implements
 * the Comparable interface to allow sorting by time and type.
 * 
 * @author Olivia Styler
 * @version November 22, 2024
 */

public abstract class AudioEvent implements Comparable<AudioEvent> {

	private int time;
	private String name;
	private int channel;

	/**
	 * Constructs an AudioEvent with the specified time, name, and channel.
	 * 
	 * @param time    the time at which the event occurs
	 * @param name    a descriptive name for the event
	 * @param channel the channel number for the event
	 */
	public AudioEvent(int time, String name, int channel) {
		this.time = time;
		this.name = name;
		this.channel = channel;
	}

	/**
	 * Returns the time of the event.
	 * 
	 * @return the event time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Returns the name of the event.
	 * 
	 * @return the event name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the channel of the event.
	 * 
	 * @return the event channel
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * Executes the event at its scheduled time. Each subclass must define its own
	 * implementation.
	 */
	public abstract void execute();

	/**
	 * Completes the event. Each subclass must define its own implementation.
	 */
	public abstract void complete();

	/**
	 * Cancels the event. Each subclass must define its own implementation.
	 */
	public abstract void cancel();
}
