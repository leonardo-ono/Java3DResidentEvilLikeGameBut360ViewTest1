package audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Leo
 */
public class Audio {

    private static Synthesizer synthesizer;
    private static Sequence sequence;
    private static Sequencer sequencer;
    private static Map<String, Sequence> musics = new HashMap<>();
    
    private static Map<String, Clip> clips = new HashMap<>();
    
    public static void start() {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Audio.class.getResourceAsStream("/res/zombie_groan.wav")));
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.stop();
            clips.put("zombie_groan", clip);

            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            sequencer = MidiSystem.getSequencer(false);
            sequencer.open();
            sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
            
            InputStream is = Audio.class.getResourceAsStream("/res/psycho.mid");
            sequence = MidiSystem.getSequence(is);            
            musics.put("psycho", sequence);
            
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MidiUnavailableException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidMidiDataException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void playSound(String soundId) {
        Clip clip = clips.get(soundId);
        clip.stop();
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }
    
    public static void playMusic(String musicId) {
        try {
            Sequence sequence = musics.get(musicId);
            if (sequence == null) {
                return;
            }
            sequencer.stop();
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
        } 
        catch (InvalidMidiDataException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void setMusicVolume(int volume) {
        MidiChannel[] channels = synthesizer.getChannels();
        for (int i = 0; i < channels.length; i++) {
            channels[i].controlChange(7, (int) (127 * (volume / 9.0)));
            channels[i].controlChange(39, (int) (127 * (volume / 9.0)));  
        }
    }
    
    public static void main(String[] args) {
        Audio.start();
        Audio.playMusic("psycho");
        Audio.setMusicVolume(3);
        Audio.playSound("zombie_groan");        
    }
}
