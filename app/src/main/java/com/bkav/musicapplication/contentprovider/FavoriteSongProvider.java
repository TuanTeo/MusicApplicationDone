package com.bkav.musicapplication.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bkav.musicapplication.database.FavoriteSongDataBase;

public class FavoriteSongProvider extends ContentProvider {

    //Authority: Thẩm quyền
    private static final String AUTHORITY = "com.bkav.musicapplication.data.FavoriteSong";
    private static final String FAVORITE_SONG_BASE_PATH = "song_data";
    //Uri of FavoriteSong Database
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + FAVORITE_SONG_BASE_PATH);

    /* ??? ( chac la cai muon lay ra ) */
    public static final int TUTORIALS = 100;
    public static final int TUTORIAL_ID = 110;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, FAVORITE_SONG_BASE_PATH, TUTORIALS);
        sUriMatcher.addURI(AUTHORITY, FAVORITE_SONG_BASE_PATH + "/#", TUTORIAL_ID);
    }

    private SQLiteDatabase mObjWriteDB;
    private FavoriteSongDataBase mFavoriteSongDB;

    @Override
    public boolean onCreate() {
        this.mFavoriteSongDB = new FavoriteSongDataBase(getContext());
        mObjWriteDB = mFavoriteSongDB.getWritableDatabase();
        return true;
    }

    /**
     * Tuantqd
     * Truy van du lieu trong FavoriteSongDataBase
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FavoriteSongDataBase.TABLE_SONG);
        int uriType = sUriMatcher.match(uri);
        /*Tuantqd: Code mau (giu lai tham khao)*/
//        switch (uriType){
//            case TUTORIALS:
//                queryBuilder.appendWhere(FavoriteSongDataBase.COLUMN_PATH + "=" +
//                        uri.getPathSegments().get(0));
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown URI");
//        }
        Cursor cursor = queryBuilder.query(mFavoriteSongDB.getReadableDatabase(),projection
                , selection, selectionArgs,null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Tuantqd
     * Add data to FavoriteSongDataBase
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        //Kiểm tra xem ID cua Song đã có trong Database chưa
        Cursor cursor = query(FavoriteSongProvider.CONTENT_URI,null,
                "PATH = ?", new String[]{values.getAsString(FavoriteSongDataBase.COLUMN_PATH)},
                null);

        //Kiem tra trong cursor co phan tu nao khong
        if(cursor.moveToFirst()){   //Co phan tu ( da co trong database ) => khong them
            return null;
        } else {                    //Chua co phan tu => them
            long rowID = mObjWriteDB.insert(FavoriteSongDataBase.TABLE_SONG, null, values);
            if (rowID > 0) {
                Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                return _uri;
            }
            values.get(FavoriteSongDataBase.COLUMN_ALBUM_ID);
            throw new SQLException("Fail to add a record into " + uri);
        }
    }

    /**
     * Tuantqd
     * Xoa du lieu tu FavoriteSongDataBase
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = mFavoriteSongDB.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case TUTORIALS:
                rowsDeleted = sqlDB.delete(FavoriteSongDataBase.TABLE_SONG, selection,
                        selectionArgs);
                break;
            case TUTORIAL_ID:
                String id = uri.getPathSegments().get(0);
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(FavoriteSongDataBase.TABLE_SONG,
                            mFavoriteSongDB.COLUMN_PATH+ "like" + id,null);
                } else {
                    rowsDeleted = sqlDB.delete(FavoriteSongDataBase.TABLE_SONG,
                            mFavoriteSongDB.COLUMN_PATH + "like" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: Lam gi co " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Tuantqd
     * Thay doi du lieu cua cac hang da co trong FavoriteSongDataBase
     * @param uri           //Co so du lieu duoc truy van
     * @param values
     * @param selection     //Cau lenh "Where"
     * @param selectionArgs //Tham so bo sung cho thao tac truy van
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = mFavoriteSongDB.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType){
            case TUTORIALS:
                rowsUpdated = sqlDB.update(FavoriteSongDataBase.TABLE_SONG,
                        values, selection, selectionArgs);
                break;
            case TUTORIAL_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)){
                    /*Tuantqd: Code mau (giu lai tham khao)*/
//                    rowsUpdated = sqlDB.update(FavoriteSongDataBase.TABLE_SONG, values,
//                            FavoriteSongDataBase.COLUMN_PATH + "=" +id + "and" + selection,
//                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
