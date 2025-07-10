package pt.isec.pa.chess.ui.res;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.media.MediaPlayer.Status;



public class SoundManager {
    private static MediaPlayer mp;
    private static List<String> soundQueue = new ArrayList<>();
    private static boolean isPlaying = false;

    private SoundManager(){}

    public static boolean isPlaying() {
        return mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public static void playSequentially(List<String> filenames, int language) {
        if (language < 0) return; // Sound disabled
        soundQueue.addAll(filenames);
        if (!isPlaying) {
            playNext(language);
        }
    }

    private static void playNext(int language) {
        if (soundQueue.isEmpty()) {
            isPlaying = false;
            return;
        }

        String filename = soundQueue.remove(0);
        isPlaying = true;

        try {
            URL url = SoundManager.class.getResource(
                    language == 0 ? "sounds/en/" + filename + ".mp3"
                            : "sounds/br/br_" + filename + ".mp3");
            if (url == null) {
                System.out.println("Sound file not found: " + filename);
                playNext(language); // Skip to next sound if current one isn't found
                return;
            }

            String path = url.toExternalForm();
            Media sound = new Media(path);
            mp = new MediaPlayer(sound);

            mp.setOnEndOfMedia(() -> {
                playNext(language);
            });

            mp.setAutoPlay(true);
        } catch (Exception e) {
            System.out.println("Error playing sound: " + filename);
            playNext(language); // Continue with next sound if error occurs
        }
    }
}

