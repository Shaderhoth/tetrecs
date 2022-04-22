package uk.ac.soton.comp1206.utilities;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Objects;

/**
 * The multimedia class is used to play audio
 */
public class Multimedia {
    /**
     * The song track currently being played
     */
    private static String currentSong;
    /**
     * The media player to play music
     */
    private static MediaPlayer mediaPlayer;
    /**
     * the audio player to play short audio files
     */
    private static MediaPlayer audioPlayer;
    /**
     * The video player to watch videos
     */
    private static WebView mediaView;
    /**
     * The volume of the audio player
     */
    private static SimpleDoubleProperty audioVolume = new SimpleDoubleProperty(1.0);
    /**
     * The volume of the media player
     */
    private static SimpleDoubleProperty mediaVolume = new SimpleDoubleProperty(1.0);

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
        if (audioEnabled.get()) {

            String toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
            logger.info("Playing audio: " + toPlay);
            try {
                Media play = new Media(toPlay);
                audioPlayer = new MediaPlayer(play);
                audioPlayer.volumeProperty().bind(audioVolume);
                audioPlayer.play();
            } catch (Exception e) {
                audioEnabled.set(false);
                e.printStackTrace();
                logger.error("Unable to play audio file, disabling audio");
            }
        }
    }

    /**
     * Initialise a media player with a background song on repeat
     * @param file the file containing the song
     */
    public static void playMedia(String file) {
        if (audioEnabled.get()) {
            if (!file.equals(currentSong)) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                currentSong = file;
                logger.info(file);
                String toPlay = Multimedia.class.getResource("/music/" + file).toExternalForm();
                logger.info("Playing music: " + toPlay);
                try {
                    Media play = new Media(toPlay);
                    mediaPlayer = new MediaPlayer(play);
                    mediaPlayer.volumeProperty().bind(mediaVolume);
                    mediaPlayer.setAutoPlay(true);
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    mediaPlayer.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void setVideo(){
        mediaView = null;
        System.gc();
        audioEnabled.setValue(true);

    }
    public static void setVideo(String link){
        link = link.replace("watch?v=","embed/") + "?autoplay=1";
        logger.info("Watching " + link);
        mediaView = new WebView();
        mediaView.setMaxHeight(GameWindow.height);
        mediaView.setMaxWidth(GameWindow.width);
        mediaView.getEngine().load(link);
        audioEnabled.setValue(false);
        if (mediaPlayer != null){
            mediaPlayer.stop();
        }
    }
    public static WebView getVideo(){
        return mediaView;

    }



    /**
     * get the audio enabled property
     * @return the audio enabled property
     */
    public static SimpleBooleanProperty getAudioEnabled(){
        return audioEnabled;
    }


    /**
     * gets the audio volume
     * @return the volume
     */
    public static  SimpleDoubleProperty getAudioVolume() {
        return audioVolume;
    }

    /**
     * gets the media volume
     * @return the volume
     */
    public static SimpleDoubleProperty getMediaVolume() {
        return mediaVolume;
    }
}
