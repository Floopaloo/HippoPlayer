package nguyen.zylin.hippoplayer.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import nguyen.zylin.hippoplayer.R;
import nguyen.zylin.hippoplayer.activity.NowPlayingActivity;
import nguyen.zylin.hippoplayer.models.Song;
import nguyen.zylin.hippoplayer.service.MusicPlayerService;
import nguyen.zylin.hippoplayer.utils.LyricFinder;

/**
 * Created by Android Studio
 * Author: Duy-Linh Nguyen
 * Date: 02-Jun-18
 * Time: 12:45
 */
public class FragmentLyric extends android.support.v4.app.Fragment implements NowPlayingActivity.PassCurrentSong{

    private static final String TAG = "FragmentLyric";

    Context context;
    TextView tvLyric;
    LyricFinder lyricFinder;
    LinearLayout notificationContainer;
    Song song;
    public FragmentLyric() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_lyric, container, false);
        context = view.getContext();
        tvLyric = view.findViewById(R.id.tv_lyric);
        notificationContainer = view.findViewById(R.id.cant_find_lyric);
        notificationContainer.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);

        ((NowPlayingActivity) context).setPassCurrentSongListener(this);

        lyricFinder = new LyricFinder(new LyricFinder.LyricFinderListener() {
            @Override
            public void onResult(String result) {
                Log.d(TAG, "onViewCreated: onResult: ");
                if (result == null) {
                    tvLyric.setText("");
                    notificationContainer.setVisibility(View.VISIBLE);
                } else {
                    notificationContainer.setVisibility(View.INVISIBLE);
                    tvLyric.setText(result);
                }
            }
        });
    }

    @Override
    public void passSong(Song song) {
        Log.d(TAG, "passSong: get a song");
        this.song = song;
        lyricFinder.parseLyric(song);
    }
}
