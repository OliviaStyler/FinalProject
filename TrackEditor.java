package assign11;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.Timer;

/**
 * A specialized editor for managing and sequencing a single track in a musical
 * composition. The class provides a grid-based interface where each row
 * represents a musical pitch and columns represent time intervals. Users can
 * interact with the grid to create, modify, and remove musical notes for
 * playback.
 *
 * @author Olivia Styler
 * @version November 22, 2024
 */

public class TrackEditor extends GridCanvas {
	private static final long serialVersionUID = 1L;

	private SimpleSynthesizer synthesizer;
	private SimpleSequencer sequencer;
	private int trackNumber;
	private int width, height;
	private int currentPitch;

	/**
	 * Constructor initializes the TrackEditor with specified parameters.
	 *
	 * @param width       the width of the grid
	 * @param height      the height of the grid
	 * @param trackNumber the track number
	 * @param synthesizer the SimpleSynthesizer instance
	 */
	public TrackEditor(int width, int height, int trackNumber, SimpleSynthesizer synthesizer) {
		super(width, height, 120, 16, 12, 5);
		this.height = height;
		this.trackNumber = trackNumber;
		this.synthesizer = synthesizer;

		this.sequencer = new SimpleSequencer(16);

		setRestrictions(1, -1);

		addMouseListener(this);
		addMouseMotionListener(this);

	}

	/**
	 * Sets the track length and updates the number of grid columns.
	 *
	 * @param length the new length of the track
	 */
	public void setLength(int length) {
		sequencer.setLength(length);
		setColumns(length);
	}

	/**
	 * Gets the current length of the track.
	 *
	 * @return the length of the track
	 */
	public int getLength() {
		return sequencer.getLength();
	}

	/**
	 * Sets the volume for the synthesizer.
	 *
	 * @param volume the new volume
	 */
	public void setVolume(int volume) {
		synthesizer.setVolume(this.trackNumber, volume);
	}

	/**
	 * Gets the current volume for the synthesizer.
	 *
	 * @return the current volume
	 */
	public int getVolume() {
		return synthesizer.getVolume(this.trackNumber);
	}

	/**
	 * Mutes or unmutes the synthesizer.
	 *
	 * @param mute true to mute, false to unmute
	 */
	public void setMute(boolean mute) {
		synthesizer.setMute(this.trackNumber, mute);
	}

	/**
	 * Sets the instrument for the synthesizer.
	 *
	 * @param instrument the instrument index
	 */
	public void setInstrument(int instrument) {
		synthesizer.setInstrument(this.trackNumber, instrument);
	}

	/**
	 * Gets the list of instrument names as a Vector.
	 *
	 * @return a Vector of instrument names
	 */
	public Vector<String> getInstrumentNames() {
		return new Vector<>(synthesizer.getInstrumentNames());
	}

	/**
	 * Gets the sequencer for this track.
	 *
	 * @return the SimpleSequencer instance
	 */
	public SimpleSequencer getSequencer() {
		return sequencer;
	}

	/**
	 * Clears the grid and sequencer, stopping playback.
	 */
	@Override
	public void clear() {
		super.clear();
		sequencer.stop();
		sequencer.clear();
	}

	/**
	 * Sets the events for the track, replacing existing ones.
	 *
	 * @param newEvents the new events to set
	 */
	public void setEvents(BetterDynamicArray<AudioEvent> newEvents) {
		clear();
		for (int i = 0; i < newEvents.size(); i++) {
			AudioEvent event = newEvents.get(i);
			if (event instanceof NoteEvent) {
				NoteEvent note = (NoteEvent) event;
				addCell(note.getPitch(), note.getTime(), 1, note.getDuration());
			}
		}
		sequencer.updateSequence(newEvents);
	}

	/**
	 * Paints the component, including a time indicator.
	 *
	 * @param g the Graphics object
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {

				g.setColor(Color.BLUE);
				g.fillRect(col * (getWidth() / sequencer.getLength()), row * (getHeight() / height),
						getWidth() / sequencer.getLength(), getHeight() / height);

			}
		}

		// Draw the playback bar
		int x = 0;
		if (sequencer.getLength() > 0) {
			double totalDuration = sequencer.getLength() * (60.0 / sequencer.getLength()); // Total duration
			double progress = sequencer.getElapsedTime() / totalDuration; // Progress as a fraction

			x = (int) (sequencer.getElapsedTime() * getWidth() / sequencer.getLength()); // Snap to column
		}

		Color customPurple = new Color(230, 179, 255);
		g.setColor(customPurple);
		g.fillRect(x, 0, 5, getHeight());
		repaint();
	}

	/**
	 * Handles the event when a cell in the grid is pressed.
	 * 
	 * @param row     the row index of the pressed cell, representing the pitch
	 * @param col     the column index of the pressed cell, representing the time
	 * @param rowSpan the vertical span of the pressed cell (unused)
	 * @param colSpan the horizontal span of the pressed cell (unused)
	 */
	@Override
	public void onCellPressed(int row, int col, int rowSpan, int colSpan) {
		currentPitch = row;
		synthesizer.noteOn(trackNumber, currentPitch);
		repaint();

	}

	/**
	 * Handles the event when a cell in the grid is dragged.
	 * 
	 * @param row     the row index of the dragged cell, representing the pitch
	 * @param col     the column index of the dragged cell, representing the time
	 * @param rowSpan the vertical span of the dragged cell (unused)
	 * @param colSpan the horizontal span of the dragged cell (unused)
	 */
	@Override
	public void onCellDragged(int row, int col, int rowSpan, int colSpan) {
		if (row != currentPitch) {
			synthesizer.noteOff(trackNumber, currentPitch);
			currentPitch = row;
			synthesizer.noteOn(trackNumber, currentPitch);
		}
		repaint();
	}

	/**
	 * Handles the event when a cell in the grid is released.
	 *
	 * @param row     the row index of the released cell, representing the pitch
	 * @param col     the column index of the released cell, representing the start
	 *                time
	 * @param rowSpan the vertical span of the released cell (unused)
	 * @param colSpan the horizontal span of the released cell, representing the
	 *                duration
	 */
	@Override
	public void onCellReleased(int row, int col, int rowSpan, int colSpan) {
		if (colSpan > 0) {
			NoteEvent note = new NoteEvent(col, "Note", trackNumber, colSpan, row, synthesizer);
			sequencer.add(note);
			synthesizer.noteOff(trackNumber, currentPitch);
		}
	}

	/**
	 * Handles the event when a cell in the grid is removed.
	 * 
	 * @param row the row index of the removed cell, representing the pitch
	 * @param col the column index of the removed cell, representing the time
	 */
	@Override
	public void onCellRemoved(int row, int col) {
		for (AudioEvent event : sequencer) {
			if (event instanceof NoteEvent) {
				NoteEvent note = (NoteEvent) event;
				if (note.getPitch() == row && note.getTime() == col) {
					sequencer.remove(note);
				}
			}
		}
	}

}
