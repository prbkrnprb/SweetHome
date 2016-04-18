package in.prabakaran.sweethome.picaso.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Prabakaran on 17-04-2016.
 */
public class GalleryContract {

    public static final String CONTENT_AUTHORITY = "in.prabakaran.sweethome.picaso";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_GALLERY_DETAILS = "gallery_details";

    public static final class GalleryDetailsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GALLERY_DETAILS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_GALLERY_DETAILS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + CONTENT_AUTHORITY + "/"
                        + PATH_GALLERY_DETAILS;

        public static final String TABLE_NAME = "gallery_details";

        public static final String COLUMN_FILE_NAME = "file_name";
        public static final String COLUMN_FILE_PATH  = "file_path";
        public static final String COLUMN_FORMAT = "file_format";

        public static Uri buildGalleryDetailsUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI,_id);
        }

        public static String getGalleryIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
}
