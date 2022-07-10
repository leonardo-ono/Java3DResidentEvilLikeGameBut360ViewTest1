package core;

import java.io.BufferedInputStream;
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
    
    private static Clip getClip(String resPath, String res, int loop) {
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(Audio.class.getResourceAsStream(resPath + res))));
            clip.loop(loop);
            clip.stop();
        } catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        return clip;
    }
    
    public static void start() {
        try {
            Clip clip = null;
            
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(Audio.class.getResourceAsStream("/res/sound/zombie_groan.wav"))));
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.stop();
            clips.put("zombie_groan", clip);

            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(Audio.class.getResourceAsStream("/res/sound/pistol_shot.wav"))));
            clip.loop(0);
            clip.stop();
            clips.put("pistol_shot", clip);

            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(Audio.class.getResourceAsStream("/res/sound/pistol_dry_fire.wav"))));
            clip.loop(0);
            clip.stop();
            clips.put("pistol_dry_fire", clip);
           
            String resPath = "/res/sound/";
            clips.put("pistol_reload", getClip(resPath, "pistol_reload.wav", 0));
            clips.put("walking", getClip(resPath, "walking.wav", Clip.LOOP_CONTINUOUSLY));
            clips.put("footstep0", getClip(resPath, "footstep0.wav", 0));
            clips.put("footstep1", getClip(resPath, "footstep1.wav", 0));
            clips.put("footstep2", getClip(resPath, "footstep2.wav", 0));
            clips.put("footstep3", getClip(resPath, "footstep3.wav", 0));
            clips.put("footstep4", getClip(resPath, "footstep4.wav", 0));
            clips.put("footstep5", getClip(resPath, "footstep5.wav", 0));
            
            clips.put("footstep_wood0", getClip(resPath, "footstep_wood0.wav", 0));
            clips.put("footstep_wood1", getClip(resPath, "footstep_wood1.wav", 0));
            
            clips.put("door_locked", getClip(resPath, "door_locked.wav", 0));
            
            clips.put("beep", getClip(resPath, "beep.wav", 0));
            
            clips.put("door_open", getClip(resPath, "door_open.wav", 0));
            clips.put("door_close", getClip(resPath, "door_close.wav", 0));

            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            sequencer = MidiSystem.getSequencer(false);
            sequencer.open();
            sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
            
            InputStream is = Audio.class.getResourceAsStream("/res/music/longstrings2high.mid");
            sequence = MidiSystem.getSequence(is);            
            musics.put("longstrings2high", sequence);
            
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
    
    public static void stopSound(String soundId) {
        Clip clip = clips.get(soundId);
        clip.stop();
    }
    
    public static void playSound(String soundId) {
        Clip clip = clips.get(soundId);
        clip.stop();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.setFramePosition(0);
        clip.start();
    }

    public static void playSoundOnce(String soundId) {
        Clip clip = clips.get(soundId);
        clip.setFramePosition(0);
        //clip.setMicrosecondPosition(0);
        //Thread.yield();
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
        }
//        while (clip.isActive() || clip.isRunning()) {
//            Thread.yield();
//        }
//        clip.setFramePosition(0);
        //if (!clip.isRunning()) {
            clip.start();
        //}
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
    
    public static void stopMusic() {
        sequencer.stop();
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
