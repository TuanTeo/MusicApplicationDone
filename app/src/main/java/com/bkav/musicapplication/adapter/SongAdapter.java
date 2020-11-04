package com.bkav.musicapplication.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.activity.MainActivity;
import com.bkav.musicapplication.constant.Constant;
import com.bkav.musicapplication.contentprovider.FavoriteSongProvider;
import com.bkav.musicapplication.database.FavoriteSongDataBase;
import com.bkav.musicapplication.object.Song;

import java.util.ArrayList;

/**Bkav Thanhnch: Hay nhu deu giong FavoriteSongAdapter?
 * Tuantqd
 * Create Adapter for AllSongFragment view
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private ArrayList<Song> mListSongAdapter; //Create List of Song
    private LayoutInflater mInflater;
    private MainActivity mMainActivity;

    private String mCurrentSongTitle = Constant.MEDIA_DEFAULT_TITLE;
    private int mLastItemPositionInt = Constant.MEDIA_DEFAULT_POSITION;  //Vi tri cua phan tu khi clicked

    public SongAdapter(ArrayList<Song> mListSongAdapter, MainActivity context) {
        this.mMainActivity = context;
        this.mListSongAdapter = mListSongAdapter;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.song_list_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        //Bind view when back from defferent activity
        if (mMainActivity.getMediaService() != null) {
            mCurrentSongTitle = mMainActivity.getMediaService().getmCurrentMediaTitle();
            //Set Name song
            holder.mSongNameItemTextView.setText(mListSongAdapter.get(position).getmTitle());
            holder.mTotalTimeSongItemTextView.setText(mListSongAdapter.get(position).getmDurationString());

            //Set font
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mListSongAdapter.get(position).getmTitle().equals(mCurrentSongTitle)) {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongClickOverLay);
                } else {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongOverLay);
                }
            }

            //Set Serial
            if (mListSongAdapter.get(position).getmTitle().equals(mCurrentSongTitle)) {
                holder.mSerialSongNumberTextView.setVisibility(View.INVISIBLE);
                holder.mPlayingSongImageLinearLayout.setVisibility(View.VISIBLE);
            } else {
                holder.mSerialSongNumberTextView.setText((position + 1) + "");
                holder.mSerialSongNumberTextView.setVisibility(View.VISIBLE);
                holder.mPlayingSongImageLinearLayout.setVisibility(View.GONE);
            }
        }
        //Bind view when the first time (mLastItemPosition = -1)
        else {
            //Set Name song
            holder.mSongNameItemTextView.setText(mListSongAdapter.get(position).getmTitle());
            holder.mTotalTimeSongItemTextView.setText(mListSongAdapter.get(position).getmDurationString());

            //Set font
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mListSongAdapter.get(position).getmTitle().equals(mCurrentSongTitle)) {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongClickOverLay);
                } else {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongOverLay);
                }
            }

            //Set Serial
            if (mListSongAdapter.get(position).getmTitle().equals(mCurrentSongTitle)) {
                holder.mSerialSongNumberTextView.setVisibility(View.INVISIBLE);
                holder.mPlayingSongImageLinearLayout.setVisibility(View.VISIBLE);
            } else {
                holder.mSerialSongNumberTextView.setText((position + 1) + "");
                holder.mSerialSongNumberTextView.setVisibility(View.VISIBLE);
                holder.mPlayingSongImageLinearLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListSongAdapter.size();
    }

    /**
     * Tuantqd
     * Filter to search song with newList
     * newList get from onQueryTextChange(String newText)
     * @param newList
     */
    public void setFilter(ArrayList<Song> newList) {
        mListSongAdapter = new ArrayList<>();
        mListSongAdapter.addAll(newList);
        notifyDataSetChanged();
    }

    public Song getCurrentSongPlay() {
        return mListSongAdapter.get(mLastItemPositionInt);
    }

    /**
     * Tuantqd
     * Holder item list view and setOnclick, update UI for this view
     */
    public class SongViewHolder extends RecyclerView.ViewHolder
            implements RecyclerView.OnClickListener {

        private TextView mSerialSongNumberTextView;
        private TextView mSongNameItemTextView;
        private TextView mTotalTimeSongItemTextView;
        private ImageButton mSongDetailItemImageButton;
        private LinearLayout mPlayingSongImageLinearLayout;

        /**
         * Tuantqd
         * Constructor of Song View Holder
         *
         * @param itemView
         */
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mSerialSongNumberTextView = itemView.findViewById(R.id.serial_item_textview);
            mSongNameItemTextView = itemView.findViewById(R.id.song_name_item_textview);
            mTotalTimeSongItemTextView = itemView.findViewById(R.id.total_time_song_item_textview);
            mSongDetailItemImageButton = itemView.findViewById(R.id.song_detail_item);

            //Set onClick for Detail image button
            mSongDetailItemImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    PopupMenu popupMenu = new PopupMenu(mMainActivity.getApplicationContext(), v);
                    popupMenu.inflate(R.menu.menu_song_item);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(mListSongAdapter.get(position).isFavoriteSong()){
                                if(item.getItemId() == R.id.add_to_favorite_song_item){
                                    item.setEnabled(false);
                                }
                            }
                            switch (item.getItemId()) {
                                case R.id.add_to_favorite_song_item:
                                    addSongToDataBase(position);
                                    notifyDataSetChanged();
                                    return true;
                                case R.id.delete_song_item:
                                    deleteSongFromDataBase(position);
                                    return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            mPlayingSongImageLinearLayout = itemView.findViewById(R.id.playing_icon_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                doOnClickSong(mMainActivity);
        }

        /**
         * Tuantqd + Thanhnch
         * Function to set what work will do when click in holder
         * @param activity
         */
        public void doOnClickSong(Activity activity){
            mLastItemPositionInt = getAdapterPosition();
            mCurrentSongTitle = mListSongAdapter.get(mLastItemPositionInt).getmTitle();

            if (activity.getResources().getConfiguration().orientation
                    != Configuration.ORIENTATION_LANDSCAPE) {

                if(mMainActivity.ismIsBindService()){
                    mMainActivity.getMediaService().setListSongService(mListSongAdapter);
                    mMainActivity.getMediaService().playMedia(mListSongAdapter.get(mLastItemPositionInt));
                } else {
                    mMainActivity.bindMediaService();
                }

                //UpDate data on View
                notifyDataSetChanged();
                //Show small playing area
                mMainActivity.getmAllSongFragment().showSmallPlayingArea();
                //Update UI in AllSongFragment
                if(mMainActivity.ismIsBindService()){
                    mMainActivity.getmAllSongFragment()
                            .upDateSmallPlayingRelativeLayout();
                } else {
                    mMainActivity.bindMediaService();
                }

                if (mListSongAdapter.get(mLastItemPositionInt).isFavoriteSong()) {
                    addSongToDataBase(mLastItemPositionInt);
                } else {
                    mListSongAdapter.get(mLastItemPositionInt).countIncrease();
                }
            } else {    //Theo chieu ngang => khong hien thi small playing area
                mLastItemPositionInt = getAdapterPosition();
                if(mMainActivity.ismIsBindService()){
                    mMainActivity.getMediaService().playMedia(mListSongAdapter.get(mLastItemPositionInt));
                } else {
                    mMainActivity.bindMediaService();
                }

                notifyDataSetChanged();
            }
        }

        /**
         * Tuantqd
         * Get Song data put to ContentValues
         *
         * @param position
         * @return
         */
        private ContentValues getSongData(int position) {
            ContentValues contentValues = new ContentValues();

            contentValues.put(FavoriteSongDataBase.COLUMN_PATH, mListSongAdapter.get(position).getmPath());
            contentValues.put(FavoriteSongDataBase.COLUMN_TITLE, mListSongAdapter.get(position).getmTitle());
            contentValues.put(FavoriteSongDataBase.COLUMN_TRACK, mListSongAdapter.get(position).getmTrackNumber());
            contentValues.put(FavoriteSongDataBase.COLUMN_YEAR, mListSongAdapter.get(position).getmYear());
            contentValues.put(FavoriteSongDataBase.COLUMN_ALBUM, mListSongAdapter.get(position).getmAlbumName());
            contentValues.put(FavoriteSongDataBase.COLUMN_ALBUM_ID, mListSongAdapter.get(position).getmAlbumID());
            contentValues.put(FavoriteSongDataBase.COLUMN_ARTIST, mListSongAdapter.get(position).getmArtistName());
            contentValues.put(FavoriteSongDataBase.COLUMN_ARTIST_ID, mListSongAdapter.get(position).getmArtistId());
            contentValues.put(FavoriteSongDataBase.COLUMN_DURATION, mListSongAdapter.get(position).getmDuration());

            return contentValues;
        }

        /**
         * Tuantqd
         * Add Song To FavoriteSongDataBase
         *
         * @param position
         */
        private void addSongToDataBase(int position) {
            try {
                ContentValues values = getSongData(position);
                Uri uri = mMainActivity.getContentResolver().insert(
                        FavoriteSongProvider.CONTENT_URI, values);
            } catch (SQLException ex) {
                Log.d("SongAdapter", "addSongToDataBase: exception");
            }
        }

        /**
         * Tuantqd
         * Delete this Song from Database
         *
         * @param position
         */
        private void deleteSongFromDataBase(int position) {
            mMainActivity.getContentResolver()
                    .delete(FavoriteSongProvider.CONTENT_URI
                            , "Path=?", new String[]{mListSongAdapter.get(position).getmPath()});
            Toast.makeText(mMainActivity, "Deleted!", Toast.LENGTH_SHORT).show();
        }
    }
}
