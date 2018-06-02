package nguyen.zylin.hippoplayer.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import nguyen.zylin.hippoplayer.R;
import nguyen.zylin.hippoplayer.models.Song;
import nguyen.zylin.hippoplayer.service.MusicPlayerService;


public class FragmentNowPlaying extends Fragment
        implements View.OnClickListener, MusicPlayerService.PlayingStateListener{

    private static final String TAG = "FragmentNowPlaying";
    ImageView mAlbumCover;

    TextView mSongTitle, mSongArtist, mSongAlbum;
    AppCompatSeekBar mSeekBar;
    ImageButton mBtnPre, mBtnPlay, mBtnNext;
    MusicPlayerService mMusicPlayerService;
    private boolean mMusicBound = false;
    Intent mPlayIntent;
    Context context;

    public FragmentNowPlaying() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        context = view.getContext();
        mAlbumCover = view.findViewById(R.id.song_album_cover);
        mSongTitle = view.findViewById(R.id.tv_song_title);
        mSongArtist = view.findViewById(R.id.tv_song_artist);
        mSongAlbum = view.findViewById(R.id.tv_song_album);

        mSeekBar = view.findViewById(R.id.seek_bar);
        mSeekBar.setVisibility(View.GONE);

        mBtnPre = view.findViewById(R.id.btn_previous);
        mBtnPre.setOnClickListener(this);
        mBtnPlay = view.findViewById(R.id.btn_play_pause);
        mBtnPlay.setOnClickListener(this);
        mBtnNext = view.findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(context, MusicPlayerService.class);
            context.bindService(mPlayIntent, mMusicPlayerServiceConnection, Context.BIND_AUTO_CREATE);
            context.startService(mPlayIntent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMusicBound) {
            context.unbindService(mMusicPlayerServiceConnection);
            mMusicBound = false;
        }
    }

    /*Connect to the MusicPlayerService*/
    private ServiceConnection mMusicPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicBinder musicBinder = ((MusicPlayerService.MusicBinder) service);
            mMusicPlayerService = musicBinder.getService();
            mMusicBound = true;
            mMusicPlayerService.setPlayingStateListener(FragmentNowPlaying.this);
            mMusicPlayerService.notifyActivity();
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

        currentSongListener.sendSong(currentSong);

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
                mMusicPlayerService.prevSong();
                break;

            case R.id.btn_play_pause:
                if (isPlayingFlag) {
                    mMusicPlayerService.pauseSong();
                } else {
                    mMusicPlayerService.resumeSong();
                }
                break;

            case R.id.btn_next:
                mMusicPlayerService.nextSong();
                break;
        }
    }

    public interface CurrentSongListener{
        void sendSong(Song song);
    }
    CurrentSongListener currentSongListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        currentSongListener = ((CurrentSongListener) context);
    }
}
