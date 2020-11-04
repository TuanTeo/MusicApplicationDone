package com.bkav.musicapplication.adapter;

import android.content.res.Configuration;
import android.os.Build;
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

/**
 * Tuantqd
 * Create adapter for FavoriteSongFragment
 */
public class FavoriteSongAdapter extends RecyclerView.Adapter<FavoriteSongAdapter.FavoriteSongViewHolder> {
    //Tuantqd Create List of Song
    private ArrayList<Song> mListFavoriteSongAdapter; //Create List of Song
    private LayoutInflater mInflater;
    private MainActivity mMainActivity;

    private int mLastItemPositionInt = Constant.MEDIA_DEFAULT_POSITION;     //Vi tri cua phan tu khi clicked
    private String mCurretSongPath = Constant.MEDIA_DEFAULT_TITLE;           //Path cua phan tu khi clicked

    public FavoriteSongAdapter(ArrayList<Song> mListSongAdapter, MainActivity context) {
        this.mMainActivity = context;
        this.mListFavoriteSongAdapter = mListSongAdapter;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FavoriteSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.song_list_item, parent, false);
        return new FavoriteSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteSongViewHolder holder, int position) {
        if(mMainActivity.getMediaService() != null){
            mCurretSongPath = mMainActivity.getMediaService().getmCurrentMediaTitle();
            //Set Name song
            holder.mSongNameItemTextView.setText(mListFavoriteSongAdapter.get(position).getmTitle());
            holder.mTotalTimeSongItemTextView.setText(mListFavoriteSongAdapter.get(position).getmDurationString());

            //Set font
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mListFavoriteSongAdapter.get(position).getmTitle().equals(mCurretSongPath)) {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongClickOverLay);
                } else {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongOverLay);
                }
            }

            //Set Serial
            if (mListFavoriteSongAdapter.get(position).getmTitle().equals(mCurretSongPath)) {
                holder.mSerialSongNumberTextView.setVisibility(View.INVISIBLE);
                holder.mPlayingSongImageLinearLayout.setVisibility(View.VISIBLE);
            } else {
                holder.mSerialSongNumberTextView.setText((position + 1) + "");
                holder.mSerialSongNumberTextView.setVisibility(View.VISIBLE);
                holder.mPlayingSongImageLinearLayout.setVisibility(View.GONE);
            }
        } else {
            //Set Name song
            holder.mSongNameItemTextView.setText(mListFavoriteSongAdapter.get(position).getmTitle());
            holder.mTotalTimeSongItemTextView.setText(mListFavoriteSongAdapter.get(position).getmDurationString());

            //Set font
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mListFavoriteSongAdapter.get(position).getmTitle().equals(mCurretSongPath)) {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongClickOverLay);
                } else {
                    holder.mSongNameItemTextView.setTextAppearance(R.style.SongTheme_NameSongOverLay);
                }
            }

            //Set Serial
            if (mListFavoriteSongAdapter.get(position).getmTitle().equals(mCurretSongPath)) {
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
        return mListFavoriteSongAdapter.size();
    }

    public class FavoriteSongViewHolder extends RecyclerView.ViewHolder
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
        public FavoriteSongViewHolder(@NonNull View itemView) {
            super(itemView);
            mSerialSongNumberTextView = itemView.findViewById(R.id.serial_item_textview);
            mSongNameItemTextView = itemView.findViewById(R.id.song_name_item_textview);
            mTotalTimeSongItemTextView = itemView.findViewById(R.id.total_time_song_item_textview);
            mSongDetailItemImageButton = itemView.findViewById(R.id.song_detail_item);
            mSongDetailItemImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    PopupMenu popupMenu = new PopupMenu(mMainActivity.getApplicationContext(), v);
                    popupMenu.inflate(R.menu.menu_favorite_song_item);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId() == R.id.remove_favorite_song_item){
                                deleteSongFromDataBase(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
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
            //Get position of item
            mLastItemPositionInt = getAdapterPosition();

            //Theo chieu doc => Show small playing area
            if (v.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//                //Get position of item
//                mLastItemPositionInt = getAdapterPosition();

                mMainActivity.getMediaService().setListSongService(mListFavoriteSongAdapter);

                //play Media
                mMainActivity.getMediaService().playMedia(mListFavoriteSongAdapter.get(mLastItemPositionInt));

                //UpDate data on View
                notifyDataSetChanged();
                //Show small playing area
                mMainActivity.getmFavoriteSongFragment().showSmallPlayingArea();
                //Update UI in AllSongFragment
                mMainActivity.getmFavoriteSongFragment().upDateSmallPlayingRelativeLayout();
            } else {    //Theo chieu ngang => khong hien thi small playing area
                mLastItemPositionInt = getAdapterPosition();
                mMainActivity.getMediaService().playMedia(mListFavoriteSongAdapter.get(mLastItemPositionInt));
                notifyDataSetChanged();
            }
        }

        /**
         * Tuantqd
         * Delete Song from database
         * @param position
         */
        private void deleteSongFromDataBase(int position) {
            int result = mMainActivity.getContentResolver()
                    .delete(FavoriteSongProvider.CONTENT_URI
                            , FavoriteSongDataBase.COLUMN_PATH + "=?", new String[]{mListFavoriteSongAdapter.get(position).getmPath()});
            if (result > 0) {
                {
                    mListFavoriteSongAdapter.remove(position);
                    notifyDataSetChanged();
                }
            }
           
        
            Toast.makeText(mMainActivity, result > 0 ? R.string.deleted_message : R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
}
