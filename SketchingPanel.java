package assign11;

import javax.swing.JPanel;

/**
 * An abstract class representing one of the main panels for the GUI. For
 * example, this could be for one track or for the song. It will have a
 * SimpleSequencer associated with it. It is a subclass of JPanel. DO NOT MODIFY
 * THIS CLASS.
 * 
 * @author Eric Heisler
 * @version 2024-10-25
 */
public abstract class SketchingPanel extends JPanel {

	// There are no instance variables and no constructor here.

	///////////////////////////////////////////////////////////////////
	// Abstract methods

	/**
	 * Gets the sequencer for this panel
	 * 
	 * @return sequencer for the panel
	 */
	public abstract SimpleSequencer getSequencer();

	/**
	 * Sets the number of beats for the panel.
	 * 
	 * @param length - number of beats for the panel
	 */
	public abstract void setLength(int length);

	/**
	 * Clears and sets the events asociated with this panel to the given list.
	 * 
	 * @param events - to be set in this panel
	 */
	public abstract void setEvents(BetterDynamicArray<AudioEvent> events);

	/**
	 * Clears all events.
	 */
	public abstract void clear();

	///////////////////////////////////////////////////////////////////
	// Implemented methods (You don't need to override these)

	/**
	 * Gets the number of beats for the panel.
	 * 
	 * @return the number of beats for the panel
	 */
	public int getLength() {
		return getSequencer().getLength();
	}

	/**
	 * Begins playing the sequence.
	 */
	public void play() {
		getSequencer().start();
	}

	/**
	 * Stops playing the sequence.
	 */
	public void stop() {
		getSequencer().stop();
	}

	/**
	 * Sets whether to loop the sequence.
	 * 
	 * @param doLoop - true if looping is desired
	 */
	public void setLoop(boolean doLoop) {
		getSequencer().setLoop(doLoop);
	}

	/**
	 * Sets the playback speed for the track.
	 * 
	 * @param tempo - in beats per minute
	 */
	public void setTempo(int tempo) {
		getSequencer().setSpeedFactor(tempo / 60000.0);
	}

	// Required by a serializable class (ignore for now)
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
}
