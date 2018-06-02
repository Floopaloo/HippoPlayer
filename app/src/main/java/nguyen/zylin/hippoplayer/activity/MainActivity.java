package nguyen.zylin.hippoplayer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nguyen.zylin.hippoplayer.R;
import nguyen.zylin.hippoplayer.models.Song;
import nguyen.zylin.hippoplayer.service.MusicPlayerService;
import nguyen.zylin.hippoplayer.utils.SongHelper;

public class MainActivity extends AppCompatActivity implements
        MusicPlayerService.PlayingStateListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView mNPSongTitle, mNPSongArtist;
    private ImageView mNPAlbumCover;
    private ImageButton mNPBtnPrevious, mNPBtnPlayPause, mNPBtnNext, mBtnInfo;
    private RelativeLayout mInfoContainer;

    private ArrayList<Song> mSongList = new ArrayList<>();
    private RecyclerView mRVSongList;

    private MusicPlayerService mMusicPlayerService;
    private Intent mPlayIntent;
    private boolean mMusicBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNPSongTitle = findViewById(R.id.song_title);
        mNPSongArtist = findViewById(R.id.song_artist);
        mNPAlbumCover = findViewById(R.id.song_album_cover);
        mNPBtnPrevious = findViewById(R.id.ibtn_previous);
        mNPBtnPrevious.setOnClickListener(this);
        mNPBtnPlayPause = findViewById(R.id.ibtn_play_pause);
        mNPBtnPlayPause.setOnClickListener(this);
        mNPBtnNext = findViewById(R.id.ibtn_next);
        mNPBtnNext.setOnClickListener(this);
        mInfoContainer = findViewById(R.id.info_container);
        mInfoContainer.setOnClickListener(this);
        mBtnInfo = findViewById(R.id.ibtn_info);
        mBtnInfo.setOnClickListener(this);

        mSongList = SongHelper.getSongList(getApplicationContext());

        mRVSongList = findViewById(R.id.rv_song_list);
        SongListAdapter songListAdapter = new SongListAdapter(mSongList);
        mRVSongList.setAdapter(songListAdapter);
        mRVSongList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        if (mPlayIntent == null) {
            Log.d(TAG, "onStart: bindService");
            mPlayIntent = new Intent(this, MusicPlayerService.class);
            bindService(mPlayIntent, mMusicPlayerServiceConnection, Context.BIND_AUTO_CREATE);
            startService(mPlayIntent);
        }
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        if (mPlayIntent == null) {
            Log.d(TAG, "onResume: bindService");
            mPlayIntent = new Intent(this, MusicPlayerService.class);
            bindService(mPlayIntent, mMusicPlayerServiceConnection, Context.BIND_AUTO_CREATE);

        }
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
        if (mMusicPlayerService != null) {
            Log.d(TAG, "onRestart: setListener");
            mMusicPlayerService.setPlayingStateListener(MainActivity.this);
            mMusicPlayerService.notifyActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMusicPlayerService.stopSelf();
        unbindService(mMusicPlayerServiceConnection);
    }

    /*Connect to the MusicPlayerService*/
    private ServiceConnection mMusicPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            MusicPlayerService.MusicBinder musicBinder = ((MusicPlayerService.MusicBinder) service);
            mMusicPlayerService = musicBinder.getService();
            mMusicPlayerService.setPlayList(mSongList);
            mMusicBound = true;
            mMusicPlayerService.setPlayingStateListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound = false;
        }
    };

    public void songPicked(View view) {
        Log.i(TAG, "songPicked: Running...");
        mMusicPlayerService.setSongToPlay(Integer.parseInt(view.getTag().toString()));
        mMusicPlayerService.playSong();
    }


    /*Listen playing status*/
    private static boolean isPlayingFlag = false;

    @Override
    public void isPlaying(boolean isPlaying) {
        isPlayingFlag = isPlaying;
        if (isPlaying) {
            mNPBtnPlayPause.setImageResource(R.drawable.ic_pause);
        } else {
            mNPBtnPlayPause.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    @Override
    public void atCurrentSong(Song currentSong) {
        mNPSongTitle.setText(currentSong.getTitle());
        mNPSongArtist.setText(currentSong.getArtist());
        if (currentSong.getCoverUri() != null) {
            Log.i(TAG, "atCurrentSong: coverURI: " + currentSong.getCoverUri());
            mNPAlbumCover.setImageURI(currentSong.getCoverUri());
        } else {
            mNPAlbumCover.setImageResource(R.drawable.hippo);
        }
    }

    /*Button Action*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_previous:
                mMusicPlayerService.prevSong();
                break;

            case R.id.ibtn_play_pause:
                if (isPlayingFlag) {
                    mMusicPlayerService.pauseSong();
                } else {
                    mMusicPlayerService.resumeSong();
                }
                break;

            case R.id.ibtn_next:
                mMusicPlayerService.nextSong();
                break;

            case R.id.info_container:
                Intent intent = new Intent(MainActivity.this, NowPlayingActivity.class);
                startActivity(intent);
//                mMusicPlayerService.moveToNowPlaying();
                break;

            case R.id.ibtn_info:
                Intent intent2 = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent2);
                break;
        }
    }


    /*Recycler View Utils*/
    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, artist, album, length;
        LinearLayout row;

        ViewHolder(View itemView) {
            super(itemView);

            row = itemView.findViewById(R.id.row_song_list);
            title = itemView.findViewById(R.id.song_title);
            artist = itemView.findViewById(R.id.song_artist);
            album = itemView.findViewById(R.id.song_album);
            length = itemView.findViewById(R.id.song_length);
        }
    }

    private class SongListAdapter extends RecyclerView.Adapter<MainActivity.ViewHolder> implements View.OnClickListener {

        List<Song> songList;

        SongListAdapter(List<Song> songList) {
            this.songList = songList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.row_song_list, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Song song = songList.get(position);

            holder.title.setText(song.getTitle());
            holder.artist.setText(song.getArtist());
            holder.album.setText(song.getAlbum());
            holder.length.setText(song.getLength());

            holder.row.setTag(position);
            holder.row.setOnClickListener(this);
        }

        @Override
        public int getItemCount() {
            return mSongList.size();
        }

        @Override
        public void onClick(View v) {
            songPicked(v);

        }
    }
}

