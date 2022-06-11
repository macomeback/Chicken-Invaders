package main.game.MVC;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

class SoundPlayer {
    private SoundPlayer(){

    }
    static Clip playSound(String soundFilePath,int loop){
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFilePath).getAbsoluteFile());
            Clip clip;
            clip=AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clip.loop(loop);
            return clip;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    static Clip playSound(String soundFilePath){
        return playSound(soundFilePath,Clip.LOOP_CONTINUOUSLY);
    }
}
