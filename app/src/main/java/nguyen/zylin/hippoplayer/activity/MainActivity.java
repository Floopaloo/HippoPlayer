package nguyen.zylin.hippoplayer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nguyen.zylin.hippoplayer.R;
import nguyen.zylin.hippoplayer.models.Song;
import nguyen.zylin.hippoplayer.service.MusicPlayerService;
import nguyen.zylin.hippoplayer.utils.SongHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ArrayList<Song> mSongList = new ArrayList<>();
    private RecyclerView mRVSongList;

    private MusicPlayerService mMusicPlayerService;
    private Intent mPlayIntent;
    private boolean mMusicBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSongList = SongHelper.getSongList(getApplicationContext());

        mRVSongList = findViewById(R.id.rv_song_list);
        SongListAdapter songListAdapter = new SongListAdapter(mSongList);
        mRVSongList.setAdapter(songListAdapter);
        mRVSongList.setLayoutManager(new LinearLayoutManager(this));
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
            mMusicPlayerService.setPlayList(mSongList);
            mMusicBound = true;
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

