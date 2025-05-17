package assign11;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A Sequencer maintains a sequence of AudioEvents and schedules their
 * execution. Playback can be started, stopped, and set to loop. Playback speed
 * can be adjusted by setting the ratio of AudioEvent tics to milliseconds.
 * 
 * @author Eric Heisler and Olivia Styler
 * @version November 22, 2024
 */
public class SimpleSequencer implements Iterable<AudioEvent> {

	private BetterDynamicArray<AudioEvent> sequence;

	private long startTime;
	private int length;
	private double speedFactor;
	private boolean running;
	private boolean loopSequence;
	private Timer timer;

	/**
	 * Creates an empty sequence of a given length in tics.
	 * 
	 * @param sequenceLength - number of timing tics in the sequence
	 */
	public SimpleSequencer(int sequenceLength) {

		sequence = new BetterDynamicArray<AudioEvent>();

		startTime = 0;
		running = false;
		length = sequenceLength;
		speedFactor = 1;
		loopSequence = false;
		timer = new Timer();
	}
	
	/**
	 * Gets the number of events in the sequence.
	 * 
	 * @return number of events
	 */
	public int getEventCount() {

		return sequence.size();
	}

	/**
	 * Adds an event to the sequence. Re-sorts the sequence after adding.
	 * 
	 * @param event - to add
	 */
	public void add(AudioEvent event) {
		sequence.add(event);
		sequence.sort();
	}

	/**
	 * Adds all events from a collection to the sequence. Re-sorts the sequence.
	 * 
	 * @param events - to add
	 */
	public void add(BetterDynamicArray<AudioEvent> events) {
		for (int i = 0; i < events.size(); i++) {
			sequence.add(events.get(i));
		}
		sequence.sort();
	}

	/**
	 * Replaces the sequence with a new collection of events. Re-sorts the sequence.
	 * 
	 * @param newSequence to replace the current one
	 */
	public void updateSequence(BetterDynamicArray<AudioEvent> newSequence) {
		sequence = newSequence;
		sequence.sort();
	}

	/**
	 * Removes the first event from the sequence that is equal to the given event.
	 * Equality is determined by the equals method. If the event is not in the
	 * sequence, nothing is changed.
	 * 
	 * @param event - to remove
	 */
	public void remove(AudioEvent event) {
		sequence.remove(event);

	}

	/**
	 * Removes all events from the sequence. Note that this does not cancel
	 * currently scheduled events.
	 */
	public void clear() {
		// Step 3: Make one method call in BetterDynamicArray<AudioEvent> to remove all
		// events.
		sequence.clear(); // Assuming you have a clear method in BetterDynamicArray<AudioEvent>
	}

	///////////////////////////////////////////////////////////////////////
	// Do not modify any code below this point.
	// These are methods and classes for scheduling execution of events
	// and iterating over events.
	///////////////////////////////////////////////////////////////////////

	/**
	 * Sets the number of tics per millisecond that allows speed control of the
	 * sequence. The default is one tic per millisecond.
	 * 
	 * @param ticsPerMillisecond - number of tics in one millisecond
	 */
	public void setSpeedFactor(double ticsPerMillisecond) {
		speedFactor = ticsPerMillisecond;
	}

	/**
	 * Sets a new length for the sequence in tics. Stops the sequence if executing.
	 * 
	 * @param newLength - length in tics of the sequence
	 */
	public void setLength(int newLength) {
		if (running)
			stop();
		length = newLength;
	}

	/**
	 * Gets the length for the sequence in tics.
	 * 
	 * @return length in tics
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Sets whether the sequence loops.
	 * 
	 * @param doLoop - true to loop the sequence, false to only play once
	 */
	public void setLoop(boolean doLoop) {
		loopSequence = doLoop;
	}

	/**
	 * The elapsed time is the time since the sequence last started. If the sequence
	 * has not started or has ended, this returns zero. The time is in tics, which
	 * differs from milliseconds if the speed factor is not 1.0.
	 * 
	 * @return elapsed time in tics
	 */
	public double getElapsedTime() {
		if (running) {
			double elapsed = millisToTics(System.currentTimeMillis() - startTime);
			return elapsed;
		}
		return 0.0;
	}

	/**
	 * Begins executing the sequence from the beginning.
	 */
	public void start() {
		System.out.println("starting");
		startTime = System.currentTimeMillis();
		running = true;
		for (int i = 0; i < sequence.size(); i++) {
			AudioEvent event = sequence.get(i);
			timer.schedule(new EventExecutionTask(event, true), ticsToMillis(event.getTime()));
			if (event instanceof NoteEvent)
				timer.schedule(new EventExecutionTask(event, false),
						ticsToMillis(event.getTime() + ((NoteEvent) event).getDuration()));
			if (event instanceof TrackEvent)
				timer.schedule(new EventExecutionTask(event, false),
						ticsToMillis(event.getTime() + ((TrackEvent) event).getDuration()));
		}
		timer.schedule(new EndSignal(), ticsToMillis(length));
	}

	/**
	 * Stops executing the sequence. This calls cancel on every event in the
	 * sequence.
	 */
	private boolean isStopping = false;

	public void stop() {
		startTime = -1;
		running = false;
		timer.cancel();
		timer = new Timer();
		for(int i = 0; i < sequence.size(); i++) {
			sequence.get(i).cancel();
		}
	}

	/**
	 * Provide an Iterator for events in the sequence. This allows using a for-each
	 * loop over the sequance.
	 * 
	 * @return an iterator for events in the sequence.
	 */
	public Iterator<AudioEvent> iterator() {
		return new SequenceIterator();
	}

	/**
	 * Converts a number of tics into milliseconds depending on the current tempo.
	 * 
	 * @param tics - amount to convert
	 * @return milliseconds amount
	 */
	public int ticsToMillis(int tics) {
		return (int) (tics / speedFactor);
	}

	/**
	 * Converts a number of milliseconds into tics depending on the current tempo.
	 * 
	 * @param milliseconds - amount to convert
	 * @return tics amount
	 */
	public double millisToTics(long milliseconds) {
		return milliseconds * speedFactor;
	}

	/**
	 * A TimerTask that executes or completes an event.
	 */
	private class EventExecutionTask extends TimerTask {
		private AudioEvent event;
		private boolean isStarting;

		/**
		 * Creates a new task with the given state
		 * 
		 * @param event      - to execute
		 * @param isStarting - true to call execute, false to call complete
		 */
		public EventExecutionTask(AudioEvent event, boolean isStarting) {
			this.event = event;
			this.isStarting = isStarting;
		}

		/**
		 * Calls the event's execute or complete method.
		 */
		@Override
		public void run() {
			if (isStarting)
				event.execute();
			else
				event.complete();
		}
	}

	/**
	 * A TimerTask that runs at the end of the sequence.
	 */
	private class EndSignal extends TimerTask {
		/**
		 * Restarts the sequence if looping.
		 */
		@Override
		public void run() {
			if (loopSequence)
				start();
			else
				running = false;
		}
	}

	/**
	 * An Iterator for the events in the sequence
	 */
	public class SequenceIterator implements Iterator<AudioEvent> {
		private int nextIndex;

		/**
		 * Constructs an iterator providing events in this sequence.
		 */
		public SequenceIterator() {
			nextIndex = 0;
		}

		/**
		 * Returns true if there is a next available event.
		 * 
		 * @return true if there is a next available event
		 */
		public boolean hasNext() {
			return nextIndex < sequence.size();
		}

		/**
		 * Gets the next available event in the sequence
		 * 
		 * @return the next available event
		 * @throws NoSuchElementException if there is no available event
		 */
		public AudioEvent next() {
			if (!hasNext())
				throw new NoSuchElementException();
			nextIndex++;
			return sequence.get(nextIndex - 1);
		}

	}
}
