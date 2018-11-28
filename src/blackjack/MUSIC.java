package blackjack;

import java.io.File;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

class Music{
    
    private static Sequencer sequencer;

    public Music(String filename){
        
        try{
            File midiFile = new File(filename);
            Sequence song = MidiSystem.getSequence(midiFile);
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(song);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        }
        catch (MidiUnavailableException e){}
        catch(InvalidMidiDataException e){}
        catch(IOException e){}
        
    }
    
    public void start(){sequencer.start();}
    
    public void stop(){sequencer.stop();}
    
}

