package nguyen.zylin.hippoplayer.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nguyen.zylin.hippoplayer.models.Song;

public class SongHelper {
    private static final String TAG = "SongHelper";

    public SongHelper() {
    }

    public static ArrayList<Song> getSongList(Context context) {
        ArrayList<Song> songList = new ArrayList<>();
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Uri albumUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
//        Cursor albumCursor = musicResolver.query(albumUri, null, null, null, null);
        //Create new cursor for album

        if ((musicCursor != null && musicCursor.moveToFirst())) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM);
            int durationColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
            int urlColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA);
            int albumIDColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM_ID);
            //add songs to list 1106
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String thisDuration = convertDuration(musicCursor.getLong(durationColumn));
                String thisUrl = musicCursor.getString(urlColumn);
                String albumID = musicCursor.getString(albumIDColumn);
                Uri thisCoverUri = null;

                //Get album art:
                String selection = MediaStore.Audio.Media._ID + " = "+thisId+"";

                Cursor cursor2 = context.getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] {
                                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID},
                        selection, null, null);

                if (cursor2.moveToFirst()) {
                    long albumId = cursor2.getLong(cursor2.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    Log.d("Album ID : ", ""+albumId);

                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    thisCoverUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                }
                cursor2.close();
                //___________________________________
//                if (albumCursor != null && albumCursor.moveToFirst()) {
//                    int albumColumn2 = albumCursor.getColumnIndex(android.provider.MediaStore.Audio.Albums._ID);
//                    int albumArtColumn = albumCursor.getColumnIndex(android.provider.MediaStore.Audio.Albums.ALBUM_ART);
//                    Log.i(TAG, "getSongList: "+albumColumn2 + " / " + albumArtColumn );
//                    do {
//                        String albumID2 = albumCursor.getString(albumColumn2);
//                        if (albumID2.equals(albumID)) {
//                            thisCoverPath = albumCursor.getString(albumArtColumn);
//                            break;
//                        }
//                    } while (albumCursor.moveToNext());
//                }


                Log.i(TAG, "getSongList: " + thisTitle + "\t" + thisArtist + "\t" + thisAlbum
                        + "\t" + thisDuration + "\t" + thisUrl + "\t" + thisCoverUri);

                songList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, thisDuration, thisUrl, thisCoverUri));
            }
            while (musicCursor.moveToNext());
        }
        sortByTitle(songList);
//        if (albumCursor != null) {
//            albumCursor.close();
//        }

        if (musicCursor != null) {
            musicCursor.close();
        }

        return songList;
    }

    private static void sortByTitle(ArrayList<Song> songList) {
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    private static String convertDuration(long duration) {
        String out = null;
        long hours = 0;
        try {
            hours = (duration / 3600000);
        } catch (Exception e) {
            e.printStackTrace();
            return out;
        }
        long remaining_minutes = (duration - (hours * 3600000)) / 60000;
        String minutes = String.valueOf(remaining_minutes);
        if (minutes.equals(0)) {
            minutes = "00";
        }
        long remaining_seconds = (duration - (hours * 3600000) - (remaining_minutes * 60000));
        String seconds = String.valueOf(remaining_seconds);
        if (seconds.length() < 2) {
            seconds = "00";
        } else {
            seconds = seconds.substring(0, 2);
        }

        if (hours > 0) {
            out = hours + ":" + minutes + ":" + seconds;
        } else {
            out = minutes + ":" + seconds;
        }

        return out;

    }

}
