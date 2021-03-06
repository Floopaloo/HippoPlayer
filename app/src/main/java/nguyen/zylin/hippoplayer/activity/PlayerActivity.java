package nguyen.zylin.hippoplayer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import nguyen.zylin.hippoplayer.R;
import nguyen.zylin.hippoplayer.models.Song;
import nguyen.zylin.hippoplayer.service.MusicPlayerService;

public class PlayerActivity extends AppCompatActivity implements MusicPlayerService.PlayingStateListener,
        View.OnClickListener {

    private static final String TAG = "PlayerActivity";

    private static PlayerActivity instance;
    public static PlayerActivity getInstance(){
        if (instance == null) {
            instance=new PlayerActivity();
        }
        return instance;
    }

    public interface OnControlListener {

        void onNext();

        void onPlayPause();

        void onPrevious();
    }

    OnControlListener onControlListener;

    public void setOnControlListener(OnControlListener onControlListener) {
        this.onControlListener = onControlListener;
    }

    ImageView mAlbumCover;

    TextView mSongTitle, mSongArtist, mSongAlbum;
    AppCompatSeekBar mSeekBar;
    ImageButton mBtnPre, mBtnPlay, mBtnNext;
    MusicPlayerService mMusicPlayerService;
    private boolean mMusicBound = false;
    Intent mPlayIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mAlbumCover = findViewById(R.id.song_album_cover);
        mSongTitle = findViewById(R.id.tv_song_title);
        mSongArtist = findViewById(R.id.tv_song_artist);
        mSongAlbum = findViewById(R.id.tv_song_album);
        mSeekBar = findViewById(R.id.seek_bar);
        mBtnPre = findViewById(R.id.btn_previous);
        mBtnPre.setOnClickListener(this);
        mBtnPlay = findViewById(R.id.btn_play_pause);
        mBtnPlay.setOnClickListener(this);
        mBtnNext = findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(this);

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MusicPlayerService.class);
            bindService(mPlayIntent, mMusicPlayerServiceConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);
        }
    }

    /*Connect to the MusicPlayerService*/
    private ServiceConnection mMusicPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicBinder musicBinder = ((MusicPlayerService.MusicBinder) service);
            mMusicPlayerService = musicBinder.getService();
//            mMusicPlayerService.setPlayList(mSongList);
            mMusicBound = true;
            mMusicPlayerService.setPlayingStateListener(PlayerActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
        }
    };

    private static boolean isPlayingFlag = false;
    @Override
    public void isPlaying(boolean isPlaying) {
        isPlayingFlag = isPlaying;
        if (isPlaying) {
            mBtnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            mBtnPlay.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    @Override
    public void atCurrentSong(Song currentSong) {
        mSongTitle.setText(currentSong.getTitle());
        mSongArtist.setText(currentSong.getArtist());
        mSongAlbum.setText(currentSong.getAlbum());
        if (currentSong.getCoverUri() != null) {
            Log.i(TAG, "atCurrentSong: coverURI: " + currentSong.getCoverUri());
            mAlbumCover.setImageURI(currentSong.getCoverUri());
        } else {
            mAlbumCover.setImageResource(R.drawable.hippo);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
//                onControlListener.onPrevious();
                mMusicPlayerService.prevSong();
                break;

            case R.id.btn_play_pause:
//                onControlListener.onPlayPause();
                if (isPlayingFlag) {
                    mMusicPlayerService.pauseSong();
                } else {
                    mMusicPlayerService.resumeSong();
                }
                break;

            case R.id.btn_next:
//                onControlListener.onNext();
                mMusicPlayerService.nextSong();
                break;
        }
    }

}
