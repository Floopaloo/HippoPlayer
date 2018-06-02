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

import nguyen.zylin.hippoplayer.activity.PlayerActivity;
import nguyen.zylin.hippoplayer.models.Song;

public class MusicPlayerService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private static MusicPlayerService musicPlayerService;
    public static MusicPlayerService getInstance(){
        if (musicPlayerService == null) {
            musicPlayerService = new MusicPlayerService();
        }
        return musicPlayerService;
    }

    public interface PlayingStateListener {

        void isPlaying(boolean isPlaying);

        void atCurrentSong(Song currentSong);
    }

    private PlayingStateListener mPlayingStateListener;

    public void setPlayingStateListener(PlayingStateListener mPlayingStateListener) {
        this.mPlayingStateListener = mPlayingStateListener;
    }

    public void notifyActivity(){
        mPlayingStateListener.atCurrentSong(playSong);
        mPlayingStateListener.isPlaying(isPlaying);
    }

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

    private Song playSong;

    public Song getCurrentSong() {
        return playSong;
    }
    private boolean isPlaying;
    public void playSong() {
        mMediaPlayer.reset();
        playSong = mSongList.get(mSongPosition);
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

        isPlaying = true;
        mPlayingStateListener.isPlaying(isPlaying);
        mPlayingStateListener.atCurrentSong(playSong);
    }

    private int length = 0;

    public void pauseSong() {
        if ((mMediaPlayer != null) && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            length = mMediaPlayer.getCurrentPosition();
            isPlaying = false;
            mPlayingStateListener.isPlaying(isPlaying);
        }
    }

    public void resumeSong() {
        if ((mMediaPlayer != null) && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(length);
            mMediaPlayer.start();
            isPlaying = true;
            mPlayingStateListener.isPlaying(isPlaying);
        }
    }

    public void nextSong() {
        if (mSongPosition < mSongList.size() - 1) {
            mSongPosition++;
        } else {
            mSongPosition = 0;
        }
        setSongToPlay(mSongPosition);
        playSong();
    }

    public void prevSong() {
        if (mSongPosition > 0) {
            mSongPosition--;
        } else {
            mSongPosition = mSongList.size() - 1;
        }
        setSongToPlay(mSongPosition);
        playSong();
    }

//    public void moveToNowPlaying(){
////        PlayerActivity.
//    }
//
//    @Override
//    public void onNext() {
//
//    }
//
//    @Override
//    public void onPlayPause() {
//
//    }
//
//    @Override
//    public void onPrevious() {
//
//    }



    public class MusicBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }
}
