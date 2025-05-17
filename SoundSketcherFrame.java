package assign11;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * The SoundSketcherFrame class represents the main GUI frame for the
 * SoundSketcher application. It allows users to create and manage multiple
 * audio tracks, adjust tempo, and toggle playback and looping options. The
 * frame contains a tabbed pane for navigating between the main song panel and
 * individual track panels.
 * 
 * @author Olivia Styler
 * @version November 15, 2024
 */
public class SoundSketcherFrame extends JFrame implements ActionListener, ChangeListener {

	private final int maxTracks = 16;
	private boolean addingTrack;

	private SongPanel songPanel;
	private BetterDynamicArray<TrackPanel> trackPanels;
	private JTabbedPane tracksPane;
	private JToggleButton playButton, loopButton;
	private JSlider tempoSlider;
	private JLabel tempoLabel;
	
	private SongFiles songFiles = new SongFiles();


	private SimpleSynthesizer synthesizer;

	/**
	 * Constructs a new SoundSketcherFrame, initializing all GUI components,
	 * including the song panel, track panels, and control panel.
	 */
	public SoundSketcherFrame() {
		synthesizer = new SimpleSynthesizer();

		// Initialize components
		songPanel = new SongPanel(900, 900);
		trackPanels = new BetterDynamicArray<>();
		tracksPane = new JTabbedPane();

		// Add SongPanel and initial TrackPanel
		tracksPane.addTab("Song", songPanel);
		TrackPanel firstTrack = new TrackPanel(800, 800, 0, synthesizer);
		trackPanels.add(firstTrack);
		songPanel.setTrackList(trackPanels);
		tracksPane.addTab("Track 0", firstTrack);

		// Add "Add Track" tab
		tracksPane.addTab("Add Track", new JPanel());
		tracksPane.setSelectedIndex(1);
		firstTrack.setTempo(120);
		songPanel.setTempo(120);
		addingTrack = false;
		tracksPane.addChangeListener(this);

		// Initialize control panel
		JPanel controlPanel = createControlPanel();

		// Layout setup
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(controlPanel, BorderLayout.SOUTH);
		mainPanel.add(tracksPane, BorderLayout.CENTER);

		// Add the menu bar
        setJMenuBar(createMenuBar());
        
		setContentPane(mainPanel);
		setTitle("SoundSketcher");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);

	}
	/**
     * Creates the menu bar with save and load options.
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(this);
        saveItem.setActionCommand("save");

        JMenuItem loadItem = new JMenuItem("Upload");
        loadItem.addActionListener(this);
        loadItem.setActionCommand("load");

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);

        return menuBar;
    }

	/**
	 * Creates and configures the control panel containing playback and loop buttons
	 * as well as a tempo slider.
	 * 
	 * @return A JPanel containing the control panel elements.
	 */
	private JPanel createControlPanel() {
		JPanel controlPanel = new JPanel();

		playButton = new JToggleButton("Play");
		playButton.addActionListener(this);

		loopButton = new JToggleButton("Loop");
		loopButton.addActionListener(this);

		tempoLabel = new JLabel("Tempo: 120 BPM");
		tempoSlider = new JSlider(60, 180, 120);
		tempoSlider.setMajorTickSpacing(20);
		tempoSlider.setPaintTicks(true);
		tempoSlider.setPaintLabels(true);
		tempoSlider.addChangeListener(this);

		controlPanel.add(playButton);
		controlPanel.add(loopButton);
		controlPanel.add(tempoLabel);
		controlPanel.add(tempoSlider);

		return controlPanel;
	}

	/**
	 * Adds a new track to the application. This method is called when the "Add
	 * Track" tab is selected and there is room for additional tracks.
	 */
	public void addTrack() {
		addingTrack = true;
		if (trackPanels.size() < maxTracks) {
			TrackPanel newTrack = new TrackPanel(800, 600, trackPanels.size(), synthesizer);
			newTrack.setTempo(tempoSlider.getValue());
			newTrack.setLoop(loopButton.isSelected());
			trackPanels.add(newTrack);

			tracksPane.insertTab("Track " + (trackPanels.size() - 1), null, newTrack, null,
					tracksPane.getTabCount() - 1);
		}
		tracksPane.setSelectedIndex(tracksPane.getTabCount() - 2);
		addingTrack = false;
	}

	/**
	 * Responds to changes in the tab selection and tempo slider. Adds a new track
	 * when the "Add Track" tab is selected. Updates the tempo for all tracks and
	 * the song panel when the tempo slider is adjusted.
	 * 
	 * @param event The ChangeEvent triggered by user interaction.
	 */
	@Override
	public void stateChanged(ChangeEvent event) {
		if ((event.getSource() == tracksPane) && (tracksPane.getSelectedIndex() == tracksPane.getTabCount() - 1)
				&& !addingTrack) {
			addTrack();
		}

		if (event.getSource() == tempoSlider) {
	        int tempo = tempoSlider.getValue();
	        tempoLabel.setText("Tempo: " + tempo + " BPM");
	        
	        // Use the setTempoSlider method to set the tempo
	        setTempoSlider(tempo);

	        // Update the tempo for all tracks and the song panel
	        songPanel.setTempo(tempo);
	        for (int i = 0; i < trackPanels.size(); i++) {
	            trackPanels.get(i).setTempo(tempo);
	        }
	    }
	}

	/**
	 * Handles actions for the play and loop buttons. Toggles playback on all tracks
	 * based on the play button's state, and toggles looping for all tracks based on
	 * the loop button's state.
	 * 
	 * @param event The ActionEvent triggered by user interaction.
	 */
	@SuppressWarnings("static-access")
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		// Check if the Play button was clicked
		if (source == playButton) {
			boolean isPlaying = playButton.isSelected(); // true for play, false for pause

			// Iterate over each track and set the play state
			for (int i = 0; i < trackPanels.size(); i++) {
				if (isPlaying) {
					trackPanels.get(i).play(); // Start playback for each track
				} else {
					trackPanels.get(i).stop(); // Stop playback for each track
				}
			}

			// Update button text to reflect the new state
			if (isPlaying) {
				playButton.setText("Pause");
			} else {
				playButton.setText("Play");
			}
		}
		// Loop button logic
		else if (source == loopButton) {
			boolean isLooping = loopButton.isSelected();
			for (int i = 0; i < trackPanels.size(); i++) {
				trackPanels.get(i).setLoop(isLooping); // Set loop for each track
			}
		}
		String command = event.getActionCommand();

        if ("save".equals(command)) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Song files", "song"));

            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                
                int tempo = tempoSlider.getValue();
    	        
                songFiles.writeFile(selectedFile, tempo, trackPanels, songPanel);
            }
        } else if ("load".equals(command)) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Song files", "song"));

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                SimpleSynthesizer synthesizer = new SimpleSynthesizer(); // Your synthesizer object
             //   BetterDynamicArray<TrackPanel> tracks = new BetterDynamicArray<>(); // Array for tracks
                //SongPanel song = new SongPanel(900,900); // Your song panel object
                int width = 900;  // Example width
                int height = 900; // Example height

                // Call the readFile method to load the song
                int tempo = songFiles.readFile(selectedFile, synthesizer, trackPanels, songPanel, width, height);

                // After reading the tempo, update the tempo slider and any other necessary UI components
                tempoSlider.setValue(tempo);

                // Update the tabs with the loaded tracks
                updateTabs();
               songPanel.setTrackList(trackPanels);
            }
        }
	}
	/**
     * Updates the tabbed pane after loading a file.
     */
    private void updateTabs() {
        addingTrack = true;
        // Remove all old tracks
        while (tracksPane.getTabCount() > 2)
            tracksPane.remove(1);
        // Add all new tracks
        int trackNumber = 0;
        while (trackNumber < trackPanels.size()) {
            tracksPane.insertTab("Track " + trackNumber, null,
                    trackPanels.get(trackNumber), null,
                    trackNumber + 1);
            trackNumber++;
        }
        tracksPane.setSelectedIndex(1);
        addingTrack = false;
    }

    
    private void setTempoSlider(int newTempo) {
        if(newTempo < tempoSlider.getMinimum())
            tempoSlider.setMinimum(newTempo);
        if(newTempo > tempoSlider.getMaximum())
            tempoSlider.setMaximum(newTempo);
        tempoSlider.setValue(newTempo);
    }
	/**
	 * Required by a serializable class (ignore for now).
	 */
	private static final long serialVersionUID = 1L;
}
