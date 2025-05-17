package assign11;

import java.io.*;
import java.util.Scanner;

/**
 * Utility class for reading and writing song data to and from files. Handles
 * file operations for songs, tracks, and audio events.
 * 
 * @author Olivia Styler
 * @version December 2, 2024
 */
public class SongFiles {

	/**
	 * Writes the song data to the specified file.
	 *
	 * @param file   The file to write to.
	 * @param tempo  The tempo of the song.
	 * @param tracks A dynamic array containing all the track panels.
	 * @param song   The song panel representing the entire song.
	 * @throws IOException If an I/O error occurs while writing to the file.
	 */
	public static void writeFile(File file, int tempo, BetterDynamicArray<TrackPanel> tracks, SongPanel song) {
		try (FileWriter writer = new FileWriter(file)) {
			StringBuilder sb = new StringBuilder();

			// Write tempo
			sb.append(tempo).append("\n");

			// Write number of tracks
			sb.append(tracks.size()).append("\n");

			// Write track blocks
			for (int i = 0; i < tracks.size(); i++) {
				TrackPanel track = tracks.get(i);
				writeTrackBlock(sb, track, i); // Assuming you have this method defined elsewhere
			}

			// Write song block
			writeSongBlock(sb, tracks, tracks.size()); // Assuming you have this method defined elsewhere

			// Write all accumulated data to the file
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception
		}
	}

	/**
	 * Reads the song data from the specified file.
	 *
	 * @param file        The file to read from.
	 * @param synthesizer The synthesizer used to manage audio.
	 * @param tracks      A dynamic array to store the track panels.
	 * @param song        The song panel to populate.
	 * @param width       The width of the track panel grid.
	 * @param height      The height of the track panel grid.
	 * @return The tempo of the song read from the file.
	 * @throws IOException           If an I/O error occurs while reading the file.
	 * @throws IllegalStateException If the file's format is invalid.
	 */
	public static int readFile(File file, SimpleSynthesizer synthesizer, BetterDynamicArray<TrackPanel> tracks,
			SongPanel song, int width, int height) {
		tracks.clear(); // Clear the BetterDynamicArray
		song.clear();
		int tempo = 0;
		try (Scanner scanner = new Scanner(file)) {
			// Read tempo
			tempo = scanner.nextInt();

			// Read number of tracks
			int numTracks = scanner.nextInt();

			// Read track blocks
			for (int i = 0; i < numTracks; i++) {
				readTrackBlock(scanner, tracks, synthesizer, width, height, tempo);
			}

			// Read song block
			readSongBlock(scanner, song, tracks, synthesizer, tempo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempo;
	}

	/**
	 * Writes a track block to the StringBuilder.
	 *
	 * @param sb          The StringBuilder to append to.
	 * @param track       The track panel to write.
	 * @param trackNumber The track's identifier number.
	 */
	private static void writeTrackBlock(StringBuilder sb, TrackPanel track, int trackNumber) {
		sb.append("track").append(trackNumber).append("\n");
		sb.append(trackNumber).append("\n");
		sb.append(track.getInstrument()).append("\n");
		sb.append(track.getVolume()).append("\n");
		sb.append(track.getLength()).append("\n");
		sb.append(track.getSequencer().getEventCount()).append("\n");

		// Write AudioEvent blocks
		for (AudioEvent event : track.getSequencer()) {
			writeAudioEventBlock(sb, event);
		}
	}

	/**
	 * Writes a song block to the StringBuilder.
	 *
	 * @param sb     The StringBuilder to append to.
	 * @param tracks A dynamic array of track panels representing the song.
	 * @param length The length of the song in terms of grid units.
	 */
	private static void writeSongBlock(StringBuilder sb, BetterDynamicArray<TrackPanel> tracks, int length) {
		sb.append("song\n");
		sb.append(length).append("\n");

		// Aggregate total event count from all tracks
		int totalEventCount = 0;
		for (TrackPanel track : tracks) {
			totalEventCount += track.getSequencer().getEventCount();
		}
		sb.append(totalEventCount).append("\n");

		// Write all AudioEvent blocks for the entire song (aggregated from tracks)
		for (TrackPanel track : tracks) {
			for (AudioEvent event : track.getSequencer()) {
				writeAudioEventBlock(sb, event);
			}
		}

	}

	/**
	 * Writes an audio event block to the StringBuilder.
	 *
	 * @param sb    The StringBuilder to append to.
	 * @param event The audio event to write.
	 */
	private static void writeAudioEventBlock(StringBuilder sb, AudioEvent event) {
		// Write event type
		if (event instanceof ChangeEvent) {
			sb.append("change").append("\n");
			ChangeEvent changeEvent = (ChangeEvent) event;
			sb.append(changeEvent.getName()).append("\n");
			sb.append(changeEvent.getTime()).append("\n");
			sb.append(changeEvent.getChannel()).append("\n");
			sb.append(changeEvent.getValue()).append("\n");
			sb.append(0).append("\n"); // Duration is 0 for ChangeEvent
		} else if (event instanceof NoteEvent) {
			sb.append("note").append("\n");
			NoteEvent noteEvent = (NoteEvent) event;
			sb.append(noteEvent.getName()).append("\n");
			sb.append(noteEvent.getTime()).append("\n");
			sb.append(noteEvent.getChannel()).append("\n");
			sb.append(noteEvent.getPitch()).append("\n"); // Value is pitch for NoteEvent
			sb.append(noteEvent.getDuration()).append("\n"); // Duration for NoteEvent
		} else if (event instanceof TrackEvent) {
			sb.append("track").append("\n");
			TrackEvent trackEvent = (TrackEvent) event;
			sb.append(trackEvent.getName()).append("\n");
			sb.append(trackEvent.getTime()).append("\n");
			sb.append(trackEvent.getChannel()).append("\n");
			sb.append(0).append("\n"); // Value is 0 for TrackEvent
			sb.append(trackEvent.getDuration()).append("\n"); // Duration for TrackEvent
		}
	}

	/**
	 * Reads a track block from the scanner and adds it to the tracks array.
	 *
	 * @param scanner     The scanner to read data from.
	 * @param tracks      The array to store the track panels.
	 * @param synthesizer The synthesizer used to manage audio.
	 * @param width       The width of the track panel grid.
	 * @param height      The height of the track panel grid.
	 * @param tempo       The tempo of the song.
	 * @throws IllegalArgumentException If the track block data is invalid or
	 *                                  malformed.
	 */
	private static void readTrackBlock(Scanner scanner, BetterDynamicArray<TrackPanel> tracks,
			SimpleSynthesizer synthesizer, int width, int height, int tempo) {
		String trackName = scanner.next(); // skip "trackX"
		int trackNumber = scanner.nextInt();
		int instrument = scanner.nextInt();
		int volume = scanner.nextInt();
		int length = scanner.nextInt();
		int numEvents = scanner.nextInt();

		// Create a new TrackPanel with the correct synthesizer and length
		TrackPanel track = new TrackPanel(width, height, trackNumber, synthesizer);
		track.setLength(length);
		track.setInstrument(instrument);
		track.setVolume(volume);
		track.setTempo(tempo);

		// Read AudioEvents for the track and add them to the sequencer
		BetterDynamicArray<AudioEvent> events = new BetterDynamicArray<>();

		for (int i = 0; i < numEvents; i++) {
			AudioEvent event = readAudioEventBlock(scanner, synthesizer, tracks); // Pass both synthesizer
																					// and sequencer
			events.add(event);
		}
		track.setEvents(events);
		tracks.add(track); // Add the track to the list
	}

	/**
	 * Reads a song block from the scanner and populates the song panel.
	 *
	 * @param scanner     The scanner to read data from.
	 * @param song        The song panel to populate.
	 * @param tracks      A dynamic array of track panels representing the song.
	 * @param synthesizer The synthesizer used to manage audio.
	 * @param tempo       The tempo of the song.
	 * @throws IllegalArgumentException If the song block data is invalid or
	 *                                  malformed.
	 */
	private static void readSongBlock(Scanner scanner, SongPanel song, BetterDynamicArray<TrackPanel> tracks,
			SimpleSynthesizer synthesizer, int tempo) {
		String songName = scanner.next(); // skip "song"
		int length = scanner.nextInt();
		int numEvents = scanner.nextInt();

		song.setLength(length);
		song.setTempo(tempo);

		BetterDynamicArray<AudioEvent> events = new BetterDynamicArray<>();

		for (int i = 0; i < numEvents; i++) {
			AudioEvent event = readAudioEventBlock(scanner, synthesizer, tracks);
			events.add(event);
		}
		song.setEvents(events);

	}

	/**
	 * Reads an audio event block from the scanner and creates an AudioEvent object.
	 *
	 * @param scanner     The scanner to read data from.
	 * @param synthesizer The synthesizer used to create certain events.
	 * @param tracks      A dynamic array of track panels for event references.
	 * @return The created AudioEvent.
	 * @throws IllegalArgumentException If the audio event block data is invalid or
	 *                                  malformed.
	 */
	private static AudioEvent readAudioEventBlock(Scanner scanner, SimpleSynthesizer synthesizer,
			BetterDynamicArray<TrackPanel> tracks) {
		String eventType = scanner.next();
		String name = scanner.next();
		int time = scanner.nextInt();
		int channel = scanner.nextInt();
		int value = scanner.nextInt();
		int duration = scanner.nextInt();

		AudioEvent event = null;
		switch (eventType) {
		case "change":
			event = new ChangeEvent(time, name, channel, value, synthesizer);
			break;
		case "note":
			event = new NoteEvent(time, name, channel, duration, value, synthesizer);
			break;
		case "track":
			event = new TrackEvent(time, name, channel, duration, tracks.get(channel).getSequencer());
			break;
		}
		return event;
	}

}
