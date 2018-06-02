package nguyen.zylin.hippoplayer.utils;

import android.util.Log;

import nguyen.zylin.hippoplayer.models.Song;

/**
 * Created by Android Studio
 * Author: Duy-Linh Nguyen
 * Date: 02-Jun-18
 * Time: 20:00
 */
public class LyricFinder {

    private static final String TAG = "LyricFinder";

    public interface LyricFinderListener {
        void onResult(String result);
    }

    private RequestLyric requestLyricTask;
    private LyricFinderListener lyricFinderListener;

    public LyricFinder(LyricFinderListener lyricFinderListener) {
        this.lyricFinderListener = lyricFinderListener;
    }

    public void parseLyric(Song song) {
        if (requestLyricTask != null) {
            requestLyricTask.stop();
            requestLyricTask = null;
        }

        RequestLyric requestLyric = new RequestLyric(new RequestLyric.RequestListener() {
            @Override
            public String doInBackgroundThread(String result) {
                return extractLyric(result);
            }

            @Override
            public void doOnUIThread(String result) {
                requestLyricTask = null;
                if (lyricFinderListener != null) {
                    lyricFinderListener.onResult(result);
                }
            }

            @Override
            public void onError(int errorCode, Exception e) {
                Log.w(TAG, "onError: " + "[" + errorCode + "]: " + e.getMessage());
            }
        });
        requestLyricTask = requestLyric;

        String artist = processArtistName(song.getArtist())
                .replaceAll("[^A-Za-z0-9]", "")
                .replaceAll("\\s", "").toLowerCase();
        String title = song.getTitle()
                .replaceAll("[^A-Za-z0-9]", "")
                .replaceAll("\\s", "").toLowerCase();
        String lyricURL = "https://www.azlyrics.com/lyrics/" + artist + "/" + title + ".html";
        Log.d(TAG, "parseLyric: lyricURL: " + lyricURL);
        requestLyric.execute(lyricURL);
    }

    private String processArtistName(String string) {
        String name = string;
        if (string.toLowerCase().contains("the ")) {
            name = string.toLowerCase().replace("the", "").trim();
        }
        return name;
    }

    private String extractLyric(String string) {
        String s = ". -->";

        int index = string.indexOf(s);
        if (index != -1) {
            int startPoint = index + s.length();
            String rawText = string.substring(startPoint, string.indexOf("</div>", startPoint));

            return translateSpecialHTMLCharacters(rawText)
                    .replaceAll("<br>", "\r\n")
                    .replaceAll("<i>", "")
                    .replaceAll("</i>", "");
        }
        return null;
    }

    private String translateSpecialHTMLCharacters(String input) {
        return input
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'");
    }
}
