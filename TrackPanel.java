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
 * A panel that represents a track in a music editor with controls for muting,
 * adjusting track length, volume, and selecting the instrument. It integrates
 * with a TrackEditor to handle the visualization and playback of the track's
 * audio.
 * 
 * @author Olivia Styler
 * @version November 22, 2024
 */
public class TrackPanel extends SketchingPanel implements ActionListener, ChangeListener {

	// GUI components and instance variables
	private TrackEditor trackEditor;
	private JPanel controlPanel;
	private JToggleButton muteButton;
	private JSpinner lengthSpinner;
	private JSlider volumeSlider;
	private JComboBox<String> instrumentComboBox;
	private JButton clearButton;

	private int trackNumber;
	private int instrumentNumber;

	/**
	 * Constructs a TrackPanel with the specified width, height, and track number.
	 * Initializes the TrackEditor and control panel with mute, length, volume, and
	 * instrument selection components.
	 *
	 * @param width       the width of the panel
	 * @param height      the height of the panel
	 * @param trackNumber the number of the track
	 */
	public TrackPanel(int width, int height, int trackNumber, SimpleSynthesizer synthesizer) {
		setPreferredSize(new Dimension(width, height));
		setLayout(new BorderLayout());

		this.trackNumber = trackNumber;
		this.trackEditor = new TrackEditor(width, height, trackNumber, synthesizer);

		// TrackEditor Panel (Main area)
		add(trackEditor, BorderLayout.CENTER);

		// Control Panel (For mute button, spinner, slider, and instrument selection)
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout()); // Adjust as needed
		setupControlPanel();

		add(controlPanel, BorderLayout.SOUTH); // Add control panel at the bottom
	}

	/**
	 * Sets up the control panel with components for controlling track settings such
	 * as mute button, track length, volume slider, and instrument selection.
	 */
	private void setupControlPanel() {
		// Mute Button
		muteButton = new JToggleButton("Mute");
		muteButton.addActionListener(this);
		controlPanel.add(muteButton);

		// Length Spinner (copied from SongPanel)
		lengthSpinner = new JSpinner(new SpinnerListModel(new Integer[] { 16, 32, 64, 128, 256, 512 }));
		lengthSpinner.addChangeListener(this);
		controlPanel.add(new JLabel("Track Length:"));
		controlPanel.add(lengthSpinner);

		// Volume Slider
		volumeSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 127, trackEditor.getVolume());
		volumeSlider.setMajorTickSpacing(32);
		volumeSlider.setMinorTickSpacing(8);
		volumeSlider.setPaintTicks(true);
		volumeSlider.setPaintLabels(true);
		volumeSlider.addChangeListener(this);
		controlPanel.add(new JLabel("Volume:"));
		controlPanel.add(volumeSlider);

		// Instrument ComboBox
		instrumentComboBox = new JComboBox<>(trackEditor.getInstrumentNames());
		instrumentComboBox.addActionListener(this);
		controlPanel.add(new JLabel("Instrument:"));
		controlPanel.add(instrumentComboBox);

		// Clear Button
		clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		controlPanel.add(clearButton); // Add to the control panel
	}

	/**
	 * Returns the sequencer associated with the track, used for playback and
	 * editing.
	 *
	 * @return the {@link SimpleSequencer} object
	 */
	@Override
	public SimpleSequencer getSequencer() {
		return trackEditor.getSequencer();
	}

	/**
	 * Sets the length of the track and updates the corresponding length spinner.
	 *
	 * @param length the length of the track
	 */
	@Override
	public void setLength(int length) {
		trackEditor.setLength(length);
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
	 * Sets the events associated with the track.
	 *
	 * @param events the audio events to set for the track
	 */
	@Override
	public void setEvents(BetterDynamicArray<AudioEvent> events) {
		trackEditor.setEvents(events);
	}

	/**
	 * Clears the track, resetting its state.
	 */
	@Override
	public void clear() {
		trackEditor.clear();
	}

	/**
	 * Gets the current volume setting for the track.
	 *
	 * @return the current volume of the track
	 */
	public int getVolume() {
		return trackEditor.getVolume();
	}

	/**
	 * Sets the volume for the track and updates the volume slider.
	 *
	 * @param volume the volume level to set
	 */
	public void setVolume(int volume) {
		trackEditor.setVolume(volume);
		volumeSlider.setValue(volume);
	}

	/**
	 * Gets the current instrument number for the track.
	 *
	 * @return the instrument number
	 */
	public int getInstrument() {
		return instrumentNumber;
	}

	/**
	 * Sets the instrument for the track and updates the instrument combo box.
	 *
	 * @param instrument the instrument number to set
	 */
	public void setInstrument(int instrument) {
		this.instrumentNumber = instrument;
		instrumentComboBox.setSelectedIndex(instrument);
		trackEditor.setInstrument(instrument);
	}

	/**
	 * Handles action events for mute button toggling and instrument selection.
	 * 
	 * @param event the action event
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		// Handle Mute Toggle
		if (source == muteButton) {
			boolean muteState = muteButton.isSelected();
			System.out.println("Mute Button Toggled: " + muteState);
			trackEditor.setMute(muteState);
			muteButton.setText(muteState ? "Unmute" : "Mute");
		}

		// Handle Instrument Selection
		else if (source == instrumentComboBox) {
			int selectedInstrument = instrumentComboBox.getSelectedIndex();
			System.out.println("Instrument Selected: " + selectedInstrument);
			trackEditor.setInstrument(selectedInstrument);
			requestFocus();
		}

		// Handle Clear Button
		else if (source == clearButton) {
			System.out.println("Clear Button Pressed: Clearing track...");
			trackEditor.clear(); // Reset the track
		}
	}

	/**
	 * Handles changes in the length spinner and volume slider.
	 * 
	 * @param event the change event
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		Object source = event.getSource();

		// Handle Length Spinner
		if (source == lengthSpinner) {
			int length = (int) lengthSpinner.getValue();
			setLength(length);
		}

		// Handle Volume Slider
		else if (source == volumeSlider) {
			int volume = volumeSlider.getValue();
			setVolume(volume);
		}
	}

	// Required by a serializable class (ignore for now)
	private static final long serialVersionUID = 1L;
}