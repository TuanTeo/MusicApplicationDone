package com.bkav.musicapplication.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bkav.musicapplication.R;
import com.bkav.musicapplication.broadcast.NotificationActionReceiver;
import com.bkav.musicapplication.contentprovider.SongProvider;
import com.bkav.musicapplication.fragment.AllSongFragment;
import com.bkav.musicapplication.fragment.FavoriteSongFragment;
import com.bkav.musicapplication.service.MediaPlaybackService;
import com.bkav.musicapplication.object.Song;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private AllSongFragment mAllSongFragment;
    private FavoriteSongFragment mFavoriteSongFragment = new FavoriteSongFragment();
    private MediaPlaybackService mMediaService;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private boolean mIsBindService;
    private boolean mIsFirstCreateService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Tao doi tuong service
            MediaPlaybackService.BoundService bind = (MediaPlaybackService.BoundService) service;
            mMediaService = bind.getService(); //Get instance of service
            mIsBindService = true;
            if (mIsFirstCreateService) {
                mIsFirstCreateService = false;
                if (getResources().getConfiguration().orientation
                        != Configuration.ORIENTATION_LANDSCAPE) {

                    getMediaService().setListSongService(mAllSongFragment.getmListSongAdapter());
                    getMediaService().playMedia(mAllSongFragment.getmSongAdapter().getCurrentSongPlay());
                    getmAllSongFragment()
                            .upDateSmallPlayingRelativeLayout();
                } else {
                    getMediaService().playMedia(mAllSongFragment.getmSongAdapter().getCurrentSongPlay());
                }

            } else {
                mAllSongFragment.showSmallPlayingArea();
                getmAllSongFragment()
                        .upDateSmallPlayingRelativeLayout();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBindService = false;
            mMediaService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create Music app Toolbar
        setSupportActionBar(
                (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_main));

        //Set navigation button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);  //show button
        actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_button);    //set button icon

        //NavigationView
        mDrawerLayout = findViewById(R.id.all_screen_view);
        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_listen_now:
                        //Close navigation page
                        mDrawerLayout.close();
                        return true;
                    case R.id.nav_music_library:
                        if (getResources().getConfiguration().orientation
                                == Configuration.ORIENTATION_PORTRAIT) {
                            showAllSongFragment(R.id.container);
                        } else {
                            showAllSongFragment(R.id.container_left);
                        }
                        mDrawerLayout.close();
                        return true;

                    case R.id.nav_recents:
                        if (getResources().getConfiguration().orientation
                                == Configuration.ORIENTATION_PORTRAIT) {
                            showFavoriteSongFragment(R.id.container);
                        } else {
                            showFavoriteSongFragment(R.id.container_left);
                        }
                        mDrawerLayout.close();
                        return true;
                }
                return false;
            }
        });

        //Permission READ_EXTERNAL_STORAGE
        if (isReadStoragePermissionGranted()) {
            SongProvider.getInstance(this);
            createMainView();
        }

        //Bind to service if Service is Running
        if (isMyServiceRunning(MediaPlaybackService.class)) {
            bindMediaService();
        }

        //Create Fragment View


    }

    /**
     * Tuantqd
     * Function bind to service
     */
    public void bindMediaService() {
        Intent intent = new Intent(getApplicationContext(), MediaPlaybackService.class);
        if (!isMyServiceRunning(MediaPlaybackService.class)) {
            mIsFirstCreateService = true;
            startService(intent);
        }
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Check Service is running
     *
     * @param serviceClass
     * @return
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mIsBindService) {
            if (isMyServiceRunning(MediaPlaybackService.class)) {
                bindMediaService();
            }
        }
    }

    /**
     * Tuantqd
     * Push item cua menu len toolbar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.seach_action_imagebutton);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        return true;
    }


    /**
     * Tuantqd
     * Set event for item clicked
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.seach_action_imagebutton:
                /*Hanlder search action on onQueryTextChange*/
                break;
            case android.R.id.home:
                //When click to NavigationDrawer
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Tuantqd
     * Create view on the First time
     */
    public void createMainView() {
        //Check Screen orientaion
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            showAllSongFragment(R.id.container);
        } else {
            showAllSongFragment(R.id.container_left);
//            showMediaFragment(R.id.container_right);
        }
    }

    /**
     * Tuantqd
     * Show All Song Fragment
     */
    public void showAllSongFragment(int intRes) {
        if (mAllSongFragment == null) {
            mAllSongFragment = new AllSongFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(intRes, mAllSongFragment)
                .commit();
    }

    public AllSongFragment getmAllSongFragment() {
        return mAllSongFragment;
    }

    /**
     * Show Favorite Song Fragment
     *
     * @param container
     */
    private void showFavoriteSongFragment(int container) {
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(container, mFavoriteSongFragment)
                .commit();
    }

    public FavoriteSongFragment getmFavoriteSongFragment() {
        return mFavoriteSongFragment;
    }

    /**
     * Tuantqd
     * Check read storage permission granted
     *
     * @return
     */
    private boolean isReadStoragePermissionGranted() {

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            createMainView();
            return true;
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            showInContextUI();

        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            mRequestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return false;

        /*Tuantqd: Code Check Permission (giu lai tham khao them)*/
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED) {
//                return true;
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                return false;
//            }
//        } else { //permission is automatically granted on sdk<23 upon installation
//            return true;
//        }
    }

    /**
     * Tuantqd
     * Show a dialog why this app requires READ_EXTERNAL_STORAGE permission
     */
    private void showInContextUI() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.read_storage_detail_dialog_message)
                .setTitle(R.string.read_storage_permission_dialog_title_educational_UI);
        //3. Add button
        builder.setNegativeButton(R.string.ok_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mRequestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        // 4. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /**
     * Tuantqd
     * Register the permissions callback, which handles the user's response to the
     * system permissions dialog. Save the return value, an instance of
     * ActivityResultLauncher, as an instance variable.
     */
    private ActivityResultLauncher<String> mRequestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    SongProvider.getInstance(this);
                    //Load lai Fragment
                    createMainView();

                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.

                }
            });

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mMainActivityBroadcast, new IntentFilter(MediaPlaybackService.UNBIND_SERVICE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*Tuantqd: Unbind to service*/
        if (mIsBindService) {
            unbindService(mServiceConnection);
            mIsBindService = false;
        }
        unregisterReceiver(mMainActivityBroadcast);
    }


    /**
     * Tuantqd
     * Function to get media Service from MainActivity
     *
     * @return
     */
    public MediaPlaybackService getMediaService() {
        return mMediaService;
    }

    /**
     * Tuantqd
     * Call when submit search text
     *
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Tuantqd
     * Call when change text to search
     *
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Song> newList = new ArrayList<>();
        for (Song song : mAllSongFragment.getmListSongAdapter()) {
            String title = song.getmTitle().toLowerCase();
            if (title.contains(newText)) {
                newList.add(song);
            }
        }
        mAllSongFragment.getmSongAdapter().setFilter(newList);
        return true;
    }

    public boolean ismIsBindService() {
        return mIsBindService;
    }

    /**
     * Tuantqd
     * BroadcastReceiver object to receiver Action UNBIND_SERVICE from Service
     */
    private BroadcastReceiver mMainActivityBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case MediaPlaybackService.UNBIND_SERVICE:
                        if (ismIsBindService()) {
                            mIsBindService = false;
                            unbindService(mServiceConnection);
                        }
                        break;
                }
            }
        }
    };
}