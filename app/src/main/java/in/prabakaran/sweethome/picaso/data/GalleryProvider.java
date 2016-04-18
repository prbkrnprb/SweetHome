package in.prabakaran.sweethome.picaso.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import in.prabakaran.sweethome.picaso.data.GalleryContract. *;


/**
 * Created by Prabakaran on 18-04-2016.
 */
public class GalleryProvider extends ContentProvider {

    private GalleryDbHelper dbHelper;

    public static final int GALLERY_DETAILS_ALL = 100;
    public static final int GALLERY_DETAILS = 101;

    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(GalleryContract.CONTENT_AUTHORITY,
                GalleryContract.PATH_GALLERY_DETAILS, GALLERY_DETAILS_ALL);
        matcher.addURI(GalleryContract.CONTENT_AUTHORITY,
                GalleryContract.PATH_GALLERY_DETAILS + "/#", GALLERY_DETAILS);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new GalleryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;

        switch (matcher.match(uri)){
            case GALLERY_DETAILS_ALL:
                returnCursor = dbHelper.getReadableDatabase().query(
                        GalleryDetailsEntry.TABLE_NAME,
                        new String[]{GalleryDetailsEntry._ID,
                                    GalleryDetailsEntry.COLUMN_FILE_NAME,
                                    GalleryDetailsEntry.COLUMN_FILE_PATH,
                                    GalleryDetailsEntry.COLUMN_FORMAT},
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case GALLERY_DETAILS:
                returnCursor = dbHelper.getReadableDatabase().query(
                        GalleryDetailsEntry.TABLE_NAME,
                        projection,
                        GalleryDetailsEntry._ID + "=" + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return returnCursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)){
            case GALLERY_DETAILS_ALL:
                return  GalleryDetailsEntry.CONTENT_TYPE;
            case GALLERY_DETAILS:
                return  GalleryDetailsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        switch (matcher.match(uri)){
            case GALLERY_DETAILS_ALL: {
                long _id = db.insert( GalleryDetailsEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri =  GalleryDetailsEntry.buildGalleryDetailsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert into Gallery Details table " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnRows;

        switch (matcher.match(uri)){
            case GALLERY_DETAILS_ALL: {
                returnRows = db.delete( GalleryDetailsEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if(selection == null || returnRows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnRows;

        switch (matcher.match(uri)){
            case GALLERY_DETAILS_ALL: {
                returnRows = db.update( GalleryDetailsEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            case GALLERY_DETAILS: {
                String movieId =  GalleryDetailsEntry.getGalleryIdFromUri(uri);
                returnRows = db.update( GalleryDetailsEntry.TABLE_NAME,values,GalleryDetailsEntry._ID + "= ?",
                        new String[]{movieId});
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if(returnRows > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return returnRows;
    }
}
