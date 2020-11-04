package com.bkav.musicapplication.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bkav.musicapplication.R;
import com.bkav.musicapplication.activity.MainActivity;
import com.bkav.musicapplication.adapter.FavoriteSongAdapter;
import com.bkav.musicapplication.constant.Constant;
import com.bkav.musicapplication.contentprovider.FavoriteSongProvider;
import com.bkav.musicapplication.database.FavoriteSongDataBase;
import com.bkav.musicapplication.object.Song;
import java.util.ArrayList;

/**
 * Tuantqd
 * Fragment to show List Favorite Song
 */
public class FavoriteSongFragment extends BaseFragment {
    private static final int MEDIA_DEFAULT_POSITION = Constant.MEDIA_DEFAULT_POSITION;
    private ArrayList<Song> mListSongAdapter;  //song List object
    private FavoriteSongAdapter mFavoriteSongAdapter;   //song Adapter object
    private RecyclerView mRecyclerView; //Recycleview object

    //Variables to check when have to update UI
    private int mSongPosition = MEDIA_DEFAULT_POSITION;
    boolean mIsPlay = false;

    private Handler mHandler = new Handler() {   //Handle object as a Thread

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mSongPosition != mMainActivity.getMediaService().getmMediaPosition()
                    || mIsPlay != mMainActivity.getMediaService().getmMediaPlayer().isPlaying()) {
                upDateSmallPlayingRelativeLayout();
                mFavoriteSongAdapter.notifyDataSetChanged();
            }
            mSongPosition = mMainActivity.getMediaService().getmMediaPosition();
            mIsPlay = mMainActivity.getMediaService().getmMediaPlayer().isPlaying();

            Message message = new Message();
            sendMessageDelayed(message, 500);
        }
    };

    /*String[] to query from database*/
    private static final String[] BASE_PROJECTION = new String[]{
            FavoriteSongDataBase.COLUMN_PATH,
            FavoriteSongDataBase.COLUMN_TRACK,
            FavoriteSongDataBase.COLUMN_YEAR,
            FavoriteSongDataBase.COLUMN_DURATION,
            FavoriteSongDataBase.COLUMN_TITLE,
            FavoriteSongDataBase.COLUMN_ALBUM,
            FavoriteSongDataBase.COLUMN_ARTIST_ID,
            FavoriteSongDataBase.COLUMN_ARTIST,
            FavoriteSongDataBase.COLUMN_ALBUM_ID,
            FavoriteSongDataBase.COLUMN_ID,
    };

    /**
     * Tuantqd
     * Create all Items RecycleView
     *
     * @param view
     */
    public void createRecycleView(View view) {
        //Get Favorite Song with FavoriteProvider
        mListSongAdapter = getFavoriteSongs();

        mFavoriteSongAdapter = new FavoriteSongAdapter(mListSongAdapter, mMainActivity);
        mRecyclerView = view.findViewById(R.id.list_song_recycleview);
        mRecyclerView.setAdapter(mFavoriteSongAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivity.getApplicationContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_list_song_fragment, container, false);
        mMainActivity = (MainActivity) getActivity();
        createRecycleView(view);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMainActivity.getMediaService() != null) {
            showSmallPlayingArea();
            upDateSmallPlayingRelativeLayout();
        }
    }

    /**
     * Tuantqd
     * Update UI for SmallPlayingRelativeLayout on FavoriteSongFragment
     */
    public void upDateSmallPlayingRelativeLayout() {
        if (mMainActivity.getMediaService().isPlaying()) {
            Message message = new Message();
            mHandler.sendMessage(message);

            super.upDateSmallPlayingRelativeLayout(mListSongAdapter, mFavoriteSongAdapter, mRecyclerView);
        }
    }

    /**
     * Tuantqd
     * Function to get List Favorite Song
     * @return
     */
    private ArrayList<Song> getFavoriteSongs() {
        ArrayList<Song> songArrayList = new ArrayList<>();
        Cursor cursor = getContext().getContentResolver()
                .query(FavoriteSongProvider.CONTENT_URI, BASE_PROJECTION,
                        null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                songArrayList.add(Song.getSong(cursor));
            }
        }
        return songArrayList;
    }
}
