package assign11;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel that represents a song in a music editor, with controls for playing,
 * stopping, looping, and adjusting the song's length. It integrates with a
 * SongEditor to handle the visualization and playback of the song's audio.
 * 
 * @author Olivia Styler
 * @version November 22, 2024
 */
public class SongPanel extends SketchingPanel implements ActionListener, ChangeListener {
	private SongEditor songEditor;
	private JPanel controlPanel;
	private JToggleButton playStopButton;
	private JToggleButton loopButton;
	private JSpinner lengthSpinner;
	private JButton clearButton;

	/**
	 * Constructs a new SongPanel with the specified dimensions. This initializes
	 * the song editor and control panel, setting up the layout and interactive
	 * controls.
	 * 
	 * @param width  the width of the panel in pixels
	 * @param height the height of the panel in pixels
	 */
	public SongPanel(int width, int height) {
		setLayout(new BorderLayout());

		// SongEditor: Dynamically sized based on container
		songEditor = new SongEditor(width, height);
		songEditor.setLayout(new GridLayout(0, 1)); // Dynamically manage rows (one per track)
		add(songEditor, BorderLayout.CENTER);

		// Control Panel (Bottom)
		controlPanel = new JPanel(new FlowLayout());
		setupControlPanel();
		add(controlPanel, BorderLayout.SOUTH);
	}

	/**
	 * Sets up the control panel with buttons for play/stop and loop, as well as a
	 * spinner to adjust the song length. The buttons and spinner are linked to
	 * their respective listeners for user interaction.
	 */
	private void setupControlPanel() {
		playStopButton = new JToggleButton("Play");
		playStopButton.addActionListener(this);
		controlPanel.add(playStopButton);

		loopButton = new JToggleButton("Loop");
		loopButton.addActionListener(this);
		controlPanel.add(loopButton);

		lengthSpinner = new JSpinner(new SpinnerListModel(new Integer[] { 16, 32, 64, 128, 256, 512 }));
		lengthSpinner.addChangeListener(this);
		controlPanel.add(new JLabel("Song Length:"));
		controlPanel.add(lengthSpinner);

		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		controlPanel.add(clearButton); // Add to the control panel

	}

	/**
	 * Retrieves the {@link SimpleSequencer} associated with the song editor.
	 * 
	 * @return the sequencer used for audio playback
	 */
	@Override
	public SimpleSequencer getSequencer() {
		return songEditor.getSequencer();
	}
	// Add a getter method for accessing songEditor
    public SongEditor getSongEditor() {
        return songEditor;
    }

	/**
	 * Updates the length of the song in both the song editor and the length
	 * spinner.
	 * 
	 * @param length the new length of the song
	 */
	@Override
	public void setLength(int length) {
		songEditor.setLength(length);
		try {
		    lengthSpinner.setValue(length);
		} catch (IllegalArgumentException e) {
		    SpinnerListModel model = (SpinnerListModel) lengthSpinner.getModel();
		    
		    // Get the list of values from the model
		    @SuppressWarnings("unchecked")
		    List<Integer> modelList = (List<Integer>) model.getList();

		    // Use BetterDynamicArray without changing it (just to work with the data)
		    BetterDynamicArray<Integer> values = new BetterDynamicArray<>();
		    
		    // Add all items from modelList to BetterDynamicArray
		    for (Integer value : modelList) {
		        values.add(value);
		    }

		    // Add the new length value and sort the array (assuming BetterDynamicArray has add() and sort())
		    values.add(length);
		    values.sort(); // Sort the items in BetterDynamicArray

		    // Convert BetterDynamicArray back to List<Integer> for the model
		    List<Integer> sortedList = new ArrayList<>();
		    for (int i = 0; i < values.size(); i++) {
		        sortedList.add(values.get(i)); // Assuming BetterDynamicArray has a get() method
		    }

		    // Update the model with the sorted list
		    model.setList(sortedList);
		    lengthSpinner.setValue(length); // Set the value after the list is updated
		}
	}

	/**
	 * Sets the audio events for the song editor to display and manage.
	 * 
	 * @param events a dynamic array of audio events to set
	 */
	@Override
	public void setEvents(BetterDynamicArray<AudioEvent> events) {
		songEditor.setEvents(events);
	}

	/**
	 * Clears all tracks and events in the song editor.
	 */
	@Override
	public void clear() {
		songEditor.clear();
	}

	/**
	 * Updates the list of track panels displayed in the song editor.
	 * 
	 * @param tracks a dynamic array of track panels to set
	 */
	public void setTrackList(BetterDynamicArray<TrackPanel> tracks) {
		this.songEditor.setTrackList(tracks);
	}

	/**
	 * Handles action events triggered by the play/stop and loop buttons. Updates
	 * the state of the buttons and the playback or looping behavior.
	 * 
	 * @param event the action event generated by a button press
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		// Handling Play/Stop Toggle
		if (source == playStopButton) {
			if (playStopButton.isSelected()) {
				System.out.println("playing");
				play();
				playStopButton.setText("Stop");
			} else {
				System.out.println("stopping");
				stop();
				playStopButton.setText("Play");
			}
		}

		// Handling Loop Toggle
		else if (source == loopButton) {
			setLoop(loopButton.isSelected());
		}

		else if (source == clearButton) {
			System.out.println("Clear Button Pressed: Clearing all tracks and events...");
			clear(); // Calls the `clear` method to reset the SongEditor
		}
	}

	/**
	 * Responds to changes in the song length spinner's value by updating the song
	 * length in both the SongPanel and the SongEditor.
	 * 
	 * @param event the change event generated by the spinner
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == lengthSpinner) {
			int length = (int) lengthSpinner.getValue();
			setLength(length); // Update length in SongPanel and SongEditor
		}

	}

	// Required by a serializable class (ignore for now)
	private static final long serialVersionUID = 1L;
}
