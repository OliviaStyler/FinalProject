package assign11;

import java.util.ArrayList;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/**
 * A simplified midi synthesizer. This contains an instance of Java's midi
 * Synthesizer, but provides a much simpler interface for it.
 * 
 * The available channels and instruments depend on your machine. If your
 * machine does not provide midi support, this class can still be used but will
 * not produce any sound. In that case, It will appear to have 16 channels and
 * one DEFAULT instrument.
 * 
 * @author Eric Heisler
 * @version 2024-11-8
 */
public class SimpleSynthesizer {
	private Synthesizer synth;
	private MidiChannel[] channels;
	private Instrument[] instruments;

	/**
	 * Creates a new SimpleSynthesizer that uses the default soundbank. Every
	 * channel is initialized with the first available instrument. If there is an
	 * error setting up the midi system, this synthesizer will still be valid and
	 * can be used, but it won't produce any audio.
	 */
	public SimpleSynthesizer() {
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
			channels = synth.getChannels();
			instruments = new Instrument[channels.length];
			synth.loadAllInstruments(synth.getDefaultSoundbank());
			Instrument defaultInstrument = synth.getLoadedInstruments()[0];
			for (int i = 0; i < channels.length; i++) {
				instruments[i] = defaultInstrument;
				channels[i].programChange(defaultInstrument.getPatch().getProgram());
			}

		} catch (MidiUnavailableException e) {
			System.out.println("Couldn't open a midi synthesizer. You may not have support on this machine.");
			e.printStackTrace();
			synth = null;
			channels = null;
			instruments = null;

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(
					"There are no midi channels or instruments provided by the midi synthesizer. Can't make sound.");
			synth = null;
			channels = null;
			instruments = null;
		}
	}

	/**
	 * Gets a list of available instrument names from the midi system. If the midi
	 * system is not available, this returns a list with one element: "DEFAULT"
	 * 
	 * @return list of instrument names
	 */
	public ArrayList<String> getInstrumentNames() {
		ArrayList<String> names = new ArrayList<String>();
		if (synth != null) {
			for (Instrument instr : synth.getLoadedInstruments())
				names.add(instr.getName());
		} else {
			// provide one default instrument if the midi system is not available
			names.add("DEFAULT");
		}
		return names;
	}

	/**
	 * Gets a list of valid channel indices. This should be numbers 0 to 15, though
	 * it is not strictly enforced by the midi protocol. If the midi system is not
	 * available, this returns a list of numbers 0 to 15.
	 * 
	 * @return list of valid indices
	 */
	public ArrayList<Integer> getValidChannelIndices() {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		if (channels != null) {
			for (int i = 0; i < channels.length; i++)
				if (channels[i] != null)
					indices.add(i);
		} else {
			// Provide channels 0 to 15 if the midi system is not available
			for (int i = 0; i < 16; i++)
				indices.add(i);
		}
		return indices;
	}

	/**
	 * Sets the current instrument on a given channel. The index will match an index
	 * in the list of instrument names provided by getInstrumentNames.
	 * 
	 * @param channel         - index of channel
	 * @param instrumentIndex - index of instrument in the list
	 * @throws IllegalArgumentException if either index is invalid
	 */
	public void setInstrument(int channel, int instrumentIndex) {
		if (channels == null)
			return;
		if (!getValidChannelIndices().contains(channel))
			throw new IllegalArgumentException("Invalid channel");
		Instrument[] instr = synth.getLoadedInstruments();
		if (instrumentIndex > instr.length || instrumentIndex < 0)
			throw new IllegalArgumentException("Invalid instrument index. Maximum is " + instr.length);
		instruments[channel] = instr[instrumentIndex];
		channels[channel].programChange(instruments[channel].getPatch().getProgram());
	}

	/**
	 * Sets the volume of a given channel. The value is clamped between 0 and 127.
	 * This has no effect if the midi system is not available.
	 * 
	 * @param channel - to set the volume of
	 * @param volume  value that will be clamped between 0 and 127
	 * @throws IllegalArgumentException if channel index is invalid
	 */
	public void setVolume(int channel, int volume) {
		if (channels == null)
			return;
		if (!getValidChannelIndices().contains(channel))
			throw new IllegalArgumentException("Invalid channel");
		if (volume > 127)
			volume = 127;
		if (volume < 0)
			volume = 0;
		// Note: 7 is the control number for volume (midi 1.0 spec)
		channels[channel].controlChange(7, volume);
	}

	/**
	 * Returns the current volume value for a given channel. If the midi system is
	 * not available, this always returns 0.
	 * 
	 * @param channel - to get the volume of
	 * @return volume value on that channel
	 * @throws IllegalArgumentException if channel index is invalid
	 */
	public int getVolume(int channel) {
		if (channels == null)
			return 0;
		if (!getValidChannelIndices().contains(channel))
			throw new IllegalArgumentException("Invalid channel");
		// Note: 7 is the control number for volume (midi 1.0 spec)
		return channels[channel].getController(7);
	}

	/**
	 * Mutes or unmutes a given channel.
	 * 
	 * @param channel - to mute
	 * @param mute    - true to mute, false to unmute
	 * @throws IllegalArgumentException if channel index is invalid
	 */
	public void setMute(int channel, boolean mute) {
		if (channels == null)
			return;
		if (!getValidChannelIndices().contains(channel))
			throw new IllegalArgumentException("Invalid channel");
		channels[channel].setMute(mute);
	}

	/**
	 * Sets a pitch bend on a given channel. This may not have an effect on all
	 * instruments or hardware implementations, and the range of pitch changes can
	 * vary, though it is typically a maximum of two semitones up or down from
	 * center. (e.g. amount 8191 raises the pitch two semitones, amount -8192 lowers
	 * two semitones)
	 * 
	 * The amount ranges from -8192 to 8191, with 0 being the center. The value is
	 * clamped to this range.
	 * 
	 * @param channel - to bend the pitch of
	 * @param amount  - to bend the pitch between -8192 and 8191 (0 is center)
	 * @throws IllegalArgumentException if channel index is invalid
	 */
	public void setPitchBend(int channel, int amount) {
		if (channels == null)
			return;
		if (!getValidChannelIndices().contains(channel))
			throw new IllegalArgumentException("Invalid channel");
		if (amount > 8191)
			amount = 8191;
		if (amount < -8192)
			amount = -8192;
		channels[channel].setPitchBend(amount + 8192);
	}

	/**
	 * Begins playing a given pitch on the given channel. The note will not end
	 * until noteOff is called for the same pitch and channel.
	 * 
	 * This has no effect if the midi system is not available or if the channel
	 * index is not valid.
	 * 
	 * @param channel - to use
	 * @param pitch   - to turn on
	 */
	public void noteOn(int channel, int pitch) {
		if (channels != null && getValidChannelIndices().contains(channel)) {
			channels[channel].noteOn(pitch, 100); // velocity is always 100
			// Including a velocity parameter is possible, but it
			// is omitted for simplicity.
		}
	}

	/**
	 * Stops playing a given pitch on a given channel. If the pitch was not already
	 * playing, nothing happens.
	 * 
	 * This has no effect if the midi system is not available or if the channel
	 * index is not valid.
	 * 
	 * @param channel - to use
	 * @param pitch   - to turn off
	 */
	public void noteOff(int channel, int pitch) {
		if (channels != null && getValidChannelIndices().contains(channel)) {
			channels[channel].noteOff(pitch);
		}
	}

	/**
	 * Turns off all notes that are playing on all channels.
	 */
	public void allNotesOff() {
		if (channels == null)
			return;
		for (MidiChannel ch : channels)
			ch.allNotesOff();
	}
}
