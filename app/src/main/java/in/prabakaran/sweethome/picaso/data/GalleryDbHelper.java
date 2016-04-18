package in.prabakaran.sweethome.picaso.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import in.prabakaran.sweethome.picaso.data.GalleryContract.*;

/**
 * Created by Prabakaran on 17-04-2016.
 */
public class GalleryDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "gallery_details.db";

    public GalleryDbHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_GALLERY_DETAILS_TABLE = "CREATE TABLE " + GalleryDetailsEntry.TABLE_NAME + " ("
                + GalleryDetailsEntry._ID + " INTEGER PRIMARY KEY, "
                + GalleryDetailsEntry.COLUMN_FILE_NAME + " TEXT NOT NULL, "
                + GalleryDetailsEntry.COLUMN_FILE_PATH + " TEXT NOT NULL, "
                + GalleryDetailsEntry.COLUMN_FORMAT + " TEXT NOT NULL "
                + ");";

        db.execSQL(SQL_CREATE_GALLERY_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GalleryDetailsEntry.TABLE_NAME);
        onCreate(db);
    }
}
