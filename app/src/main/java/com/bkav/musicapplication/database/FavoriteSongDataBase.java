package com.bkav.musicapplication.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bkav.musicapplication.object.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Tuantqd
 * Favorite Song Database to save Song is favorite
 */
public class FavoriteSongDataBase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Favorite_Song_Manager";

    // Table name
    public static final String TABLE_SONG = "Favorite_Song";

    //Column of Table
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_TRACK = "Track";
    public static final String COLUMN_YEAR = "Year";
    public static final String COLUMN_DURATION = "Duration";
    public static final String COLUMN_PATH = "Path";
    public static final String COLUMN_ALBUM = "Album";
    public static final String COLUMN_ARTIST_ID = "Artist_ID";
    public static final String COLUMN_ARTIST = "Artist";
    public static final String COLUMN_ALBUM_ID = "Album_ID";
    public static final String COLUMN_ID = "_id";

    private ContentResolver mContentResolver;

    /**
     * Tuantqd
     * Constructor
     * @param context
     */
    public FavoriteSongDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "CREATE TABLE " + TABLE_SONG + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_PATH + " TEXT," + COLUMN_TITLE + " TEXT,"
                + COLUMN_TRACK + " INTEGER, " + COLUMN_YEAR + " INTEGER,"
                + COLUMN_DURATION + " INTEGER," + COLUMN_ALBUM + " TEXT,"
                + COLUMN_ARTIST_ID + " INTEGER," + COLUMN_ARTIST + " TEXT,"
                + COLUMN_ALBUM_ID + " TEXT" + ")";
        // Execute Script.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop older table if existed
        String sqlDropTableQuery = "DROP TABLE IF EXISTS";
        db.execSQL(sqlDropTableQuery + TABLE_SONG);
        onCreate(db);
    }

    /**
     * Tuantqd
     * Add a Song to List Favorite Song
     * @param song
     */
    public void addSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PATH, song.getmPath());
        values.put(COLUMN_TITLE, song.getmTitle());
        values.put(COLUMN_TRACK, song.getmTrackNumber());
        values.put(COLUMN_YEAR, song.getmYear());
        values.put(COLUMN_DURATION, song.getmDuration());
        values.put(COLUMN_ALBUM, song.getmAlbumName());
        values.put(COLUMN_ARTIST_ID, song.getmArtistId());
        values.put(COLUMN_ARTIST, song.getmArtistName());
        values.put(COLUMN_ALBUM_ID, song.getmAlbumID());
        values.put(COLUMN_ID, song.getmID());

        //Insert Row
        db.insert(TABLE_SONG, null, values);

        db.close();
    }

    /**
     * Tuantqd
     * Get list All Favorite Song
     * @return
     */
    public List<Song> getAllSong() {
        List<Song> songList = new ArrayList<Song>();

        String SQLquery = "Select * From " + TABLE_SONG;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLquery, null);

        //Looping through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                songList.add(Song.getSong(cursor));
            } while (cursor.moveToNext());
        }

        //Return songList
        return songList;
    }

    /**
     * Tuantqd
     * Delete a Song on ListSong
     * @param song
     */
    public void deleteSong(Song song) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SONG, COLUMN_PATH + " = ?",
                new String[]{String.valueOf(song.getmPath())});
        db.close();
    }
}
