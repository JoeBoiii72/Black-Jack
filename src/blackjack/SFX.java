
package blackjack;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

class SFX{
    
    private Clip sound;
    public static final SFX busted = new SFX("sounds/bust.wav");

    private SFX(String f)
    {
        File audioFile = new File(f);
        
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile);
            sound = AudioSystem.getClip();
            sound.open(audioIn);
            audioIn.close();
        } 
        catch(UnsupportedAudioFileException e){}
        catch(LineUnavailableException e){}
        catch(IOException e){}
    }

    public void play(){
        sound.setFramePosition(0);
        sound.start();
    }
       
}


