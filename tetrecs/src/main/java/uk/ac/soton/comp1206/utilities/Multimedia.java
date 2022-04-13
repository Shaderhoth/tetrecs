package uk.ac.soton.comp1206.utilities;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;

/**
 * The multimedia class is used to play audio
 */
public class Multimedia {
    /**
     * The mesia player to play music
     */
    private static MediaPlayer mediaPlayer;
    /**
     * the audio player to play short audio files
     */
    private static MediaPlayer audioPlayer;
    /**
     * Is the audio enabled?
     */
    private static SimpleBooleanProperty audioEnabled = new SimpleBooleanProperty(true);
    /**
     * logs are everywhere
     */
    private static final Logger logger = LogManager.getLogger(Multimedia.class);

    /**
     * play an audio file
     * @param file
     */
    public static void playAudio(String file) {
        if (!audioEnabled.get()) return;

        String toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
        logger.info("Playing audio: " + toPlay);

        try {
            Media play = new Media(toPlay);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            audioEnabled.set(false);
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }

    /**
     * Initialise a media player with a background song on repeat
     * @param file the file containing the song
     */
    public Multimedia(String file) {
        if (!audioEnabled.get()) return;

        String toPlay = Multimedia.class.getResource("/music/" + file).toExternalForm();
        logger.info("Playing music: " + toPlay);
        try {
            Media play = new Media(toPlay);
            mediaPlayer = new MediaPlayer(play);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            audioEnabled.set(false);
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }

    /**
     * stop playing media
     */
    public void stop(){
        mediaPlayer.stop();
    }

    /**
     * get the audio enabled property
     * @return the audio enabled property
     */
    public static SimpleBooleanProperty getAudioEnabled(){
        return audioEnabled;
    }
}
