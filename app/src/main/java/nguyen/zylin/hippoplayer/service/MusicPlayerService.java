package nguyen.zylin.hippoplayer.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import nguyen.zylin.hippoplayer.models.Song;

public class MusicPlayerService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    private static final String TAG = "MusicPlayerService";
    
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mSongList;
    private int mSongPosition;

    private final IBinder mMusicServiceBind = new MusicBinder();


    @Override
    public void onCreate() {
        super.onCreate();
        mSongPosition = 0;
        mMediaPlayer = new MediaPlayer();

        initMusicPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMusicServiceBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }


    private void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    public void setPlayList(ArrayList<Song> list) {
        mSongList = list;
    }

    public void setSongToPlay(int position) {
        this.mSongPosition = position;
    }

    public void playSong() {
        mMediaPlayer.reset();
        Song playSong = mSongList.get(mSongPosition);
        long currentSong = playSong.getId();
        Uri songUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currentSong);

        try {
            mMediaPlayer.setDataSource(getApplicationContext(), songUri);
        } catch (IOException e) {
            Log.e(TAG, "playSong: error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
    }



    public class MusicBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }
}
