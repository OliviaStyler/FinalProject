package assign11;

import java.awt.Color;
import java.awt.Graphics;

/**
 * A visual editor for managing and sequencing tracks in a song. The
 * {@code SongEditor} class provides an interactive grid-based interface where
 * each row represents a track and the columns represent time intervals. Users
 * can add, remove, and edit musical events across multiple tracks.
 * 
 * @author Olivia Styler
 * @version November 22, 2024
 */

public class SongEditor extends GridCanvas {

	private SimpleSequencer sequencer;
	private BetterDynamicArray<TrackPanel> trackPanels;
	private int width, height, currentTrack;

	/**
	 * Constructor initializes the SongEditor with specified width and height.
	 *
	 * @param width  the width of the grid
	 * @param height the height of the grid
	 */
	public SongEditor(int width, int height) {
		super(width, height, 1, 16, 5, 5);
		this.width = width;
		this.height = height; // Start with 1 row, adjusted later based on track list
		this.sequencer = new SimpleSequencer(128); // Initial length chosen for simplicity
		this.trackPanels = new BetterDynamicArray<>();

		// Set initial length
		sequencer.setLength(128);

		// Add mouse listeners for interaction
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
	 * Gets the SimpleSequencer associated with this SongEditor.
	 *
	 * @return the SimpleSequencer
	 */
	public SimpleSequencer getSequencer() {
		return sequencer;
	}

	/**
	 * Clears the sequencer and updates the visual state.
	 */
	@Override
	public void clear() {
		super.clear();
		sequencer.stop();
		sequencer.clear();
	}

	/**
	 * Updates the sequence of events within the sequencer.
	 *
	 * @param newEvents the new events to be set
	 */
	public void setEvents(BetterDynamicArray<AudioEvent> newEvents) {
		clear();
		for (int i = 0; i < newEvents.size(); i++) {
			AudioEvent event = newEvents.get(i);
			if (event instanceof TrackEvent) {
				TrackEvent note = (TrackEvent) event;
				addCell(note.getChannel(), note.getTime(), 1, note.getDuration());
			}
		}
		sequencer.updateSequence(newEvents);
	}

	/**
	 * Sets the track list for the SongEditor, updating its height and triggering a
	 * repaint.
	 *
	 * @param trackList a list of TrackPanels
	 */
	public void setTrackList(BetterDynamicArray<TrackPanel> trackList) {
		this.trackPanels = trackList;
		this.height = trackList.size(); // Update height based on the number of tracks
		repaint(); // Trigger a repaint to reflect the changes
	}

	/**
	 * Paints the SongEditor component, including time indicators and visual
	 * elements.
	 *
	 * @param g the Graphics object
	 */
	@Override
	public void paintComponent(Graphics g) {
		setRows(trackPanels.size());
		super.paintComponent(g);

		// Additional visual elements can be added here (e.g., time indicator)
		// Draw the playback bar
		// Draw the playback bar
		int x = 0;
		if (sequencer.getLength() > 0) {

//            System.out.println("Elapsed Time: " + sequencer.getElapsedTime());
//            System.out.println("Total Duration: " + totalDuration);
//            System.out.println("Progress: " + progress);
			x = (int) (sequencer.getElapsedTime() * getWidth() / sequencer.getLength()); // Snap to column
		}

		// Draw the playback bar
		Color customPurple = new Color(230, 179, 255);
		g.setColor(customPurple);
		g.fillRect(x, 0, 5, getHeight()); // Playback bar spans the height of the component
		repaint();
	}

	/**
	 * Handles mouse press events, setting the current track and updating
	 * restrictions.
	 *
	 * @param row     the row index
	 * @param col     the column index
	 * @param rowSpan the number of rows spanned
	 * @param colSpan the number of columns spanned
	 */
	@Override
	public void onCellPressed(int row, int col, int rowSpan, int colSpan) {
		currentTrack = row;
		setRestrictions(1, trackPanels.get(currentTrack).getSequencer().getLength());
		repaint(); // Redraw to reflect selection
	}

	/**
	 * Handles mouse drag events, adjusting the current track if necessary and
	 * updating restrictions.
	 *
	 * @param row     the row index
	 * @param col     the column index
	 * @param rowSpan the number of rows spanned
	 * @param colSpan the number of columns spanned
	 */
	@Override
	public void onCellDragged(int row, int col, int rowSpan, int colSpan) {
		if (row != currentTrack) {
			currentTrack = row;
			setRestrictions(1, trackPanels.get(currentTrack).getSequencer().getLength());
		}
		repaint(); // Redraw to reflect selection
	}

	/**
	 * Handles mouse release events, constructing a new TrackEvent and adding it to
	 * the sequencer.
	 *
	 * @param row     the row index
	 * @param col     the column index
	 * @param rowSpan the number of rows spanned
	 * @param colSpan the number of columns spanned
	 */
	@Override
	public void onCellReleased(int row, int col, int rowSpan, int colSpan) {
		if (colSpan > 0) {
			TrackEvent event = new TrackEvent(col, "Track" + row, row, colSpan,
					trackPanels.get(currentTrack).getSequencer());
			sequencer.add(event);
		}

		setRestrictions(1, 1);
	}

	/**
	 * Handles the removal of cells, iterating through the sequencer to find and
	 * remove matching TrackEvents.
	 *
	 * @param row the row index
	 * @param col the column index
	 */
	@Override
	public void onCellRemoved(int row, int col) {

		// Loop over the AudioEvents in the SimpleSequencer
		for (AudioEvent event : sequencer) {
			// If the TrackEvent's channel and time match the row and col, remove it
			if (event.getChannel() == trackPanels.size() - 1 - row && event.getTime() == col) {
				sequencer.remove(event); // Remove the matching TrackEvent
			}

		}
	}

	private static final long serialVersionUID = 1L;
}
