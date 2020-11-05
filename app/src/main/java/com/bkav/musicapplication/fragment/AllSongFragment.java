package com.bkav.musicapplication.fragment;

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
import com.bkav.musicapplication.adapter.SongAdapter;
import com.bkav.musicapplication.contentprovider.SongProvider;
import com.bkav.musicapplication.object.Song;
import java.util.ArrayList;


/**
 * Create by: Tuantqd
 * Display Songs List
 */
public class AllSongFragment extends BaseFragment {

    private ArrayList<Song> mListSongAdapter;  //song List object
    private SongAdapter mSongAdapter;   //song Adapter object
    private RecyclerView mRecyclerView; //Recycleview object

    //Variables to check when have to update UI
    private int mSongPosition = -1;
    boolean mIsPlay = false;

    /*Handler is a Thread to Update UI current time*/
    private Handler mHandler = new Handler() {   //Handle object as a Thread

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(mMainActivity.ismIsBindService()) {
                if (mMainActivity.getMediaService() != null
                        && mSongPosition != mMainActivity.getMediaService().getmMediaPosition()
                        || mIsPlay != mMainActivity.getMediaService().getmMediaPlayer().isPlaying()) {
                    upDateSmallPlayingRelativeLayout(mListSongAdapter, mSongAdapter, mRecyclerView);
                    mSongAdapter.notifyDataSetChanged();
                }

                mSongPosition = mMainActivity.getMediaService().getmMediaPosition();
                mIsPlay = mMainActivity.getMediaService().getmMediaPlayer().isPlaying();
                Message message = new Message();
                sendMessageDelayed(message, 500);
            } else {

            }
        }
    };



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
        /*Update UI can lay bai hat hien tai dang choi trong Service
        * nen Service phai check khac null
        * */
        if (mMainActivity.getMediaService() != null) {
            showSmallPlayingArea();
            upDateSmallPlayingRelativeLayout(mListSongAdapter, mSongAdapter, mRecyclerView);
        }
    }

    /**
     * Tuantqd
     * Create all Items RecycleView
     *
     * @param view
     */
    public void createRecycleView(View view) {
        //Read all Song with SongProvider
        mListSongAdapter = SongProvider.getInstance(getActivity().getApplicationContext()).getmListSong();

        mSongAdapter = new SongAdapter(mListSongAdapter, mMainActivity);
        mRecyclerView = view.findViewById(R.id.list_song_recycleview);
        mRecyclerView.setAdapter(mSongAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivity.getApplicationContext()));
    }


    /**
     * Tuantqd
     * Update UI for SmallPlayingRelativeLayout on AllSongFragment
     */
    public void upDateSmallPlayingRelativeLayout() {
        if (mMainActivity.getMediaService().getmMediaPlayer() != null) {
            Message message = new Message();
            mHandler.sendMessage(message);

            super.upDateSmallPlayingRelativeLayout(mListSongAdapter, mSongAdapter, mRecyclerView);
        }
    }

    /**
     * Tuantqd
     * Function to get List all song
     * @return
     */
    public ArrayList<Song> getmListSongAdapter() {
        return mListSongAdapter;
    }

    /**
     * Tuantqd
     * Fuction to get SongAdapter object
     * @return
     */
    public SongAdapter getmSongAdapter(){
        return mSongAdapter;
    }
}
