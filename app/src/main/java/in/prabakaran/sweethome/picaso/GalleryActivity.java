package in.prabakaran.sweethome.picaso;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.prabakaran.sweethome.R;
import in.prabakaran.sweethome.picaso.data.GalleryContract.GalleryDetailsEntry;


public class GalleryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int GALLERY_DETAILS_LOADER = 0;
    Context mContext;
    String LOG_TAG = "GalleryActivity";
    private GalleryThumbnailAdapter mGalleryThumbnailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mContext = getApplicationContext();

        mGalleryThumbnailAdapter = new GalleryThumbnailAdapter(
                mContext,
                null,
                0
        );

        GridView galleryGrid = (GridView) findViewById(R.id.gallery_grid);
        galleryGrid.setAdapter(mGalleryThumbnailAdapter);

        getSupportLoaderManager().initLoader(GALLERY_DETAILS_LOADER, null, this);
        galleryGrid.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_add_picture:
                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SweetHomeApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("SweetHomeApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String path = getOnlyPath(fileUri);
                String fileName = getOnlyFileName(fileUri);
                String fileFormat = getOnlyFormat(fileUri);
                insertPicture(path,fileName,fileFormat);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User cancelled the camera operation", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                Log.e("SweetHomeApp", "failed to capture image. Result code : " + resultCode);
            }
        }
    }

    private String getOnlyPath(Uri uri){
        String sUri = uri.toString();
        return sUri.substring(0,sUri.lastIndexOf("/"));
    }

    private String getOnlyFileName(Uri uri){
        String sUri = uri.toString();
        return sUri.substring(sUri.lastIndexOf("/"),sUri.length());
    }

    private String getOnlyFormat(Uri uri){
        String sUri = uri.toString();
        return sUri.substring(sUri.lastIndexOf("."),sUri.length());
    }

    private void insertPicture(String path, String fileName, String fileFormat){
        ContentValues values = new ContentValues();
        values.put(GalleryDetailsEntry.COLUMN_FILE_PATH, path);
        values.put(GalleryDetailsEntry.COLUMN_FILE_NAME, fileName);
        values.put(GalleryDetailsEntry.COLUMN_FORMAT, fileFormat);

        Uri uri = mContext.getContentResolver().insert(GalleryDetailsEntry.CONTENT_URI, values);
        Log.v(LOG_TAG, "Gallery details inserted : " + ContentUris.parseId(uri));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        uri = GalleryDetailsEntry.CONTENT_URI;

        return new CursorLoader(
                mContext,
                uri,
                new String[]{
                        GalleryDetailsEntry._ID},
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGalleryThumbnailAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGalleryThumbnailAdapter.swapCursor(null);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GalleryThumbnailAdapter.GalleryViewHolder holder = (GalleryThumbnailAdapter.GalleryViewHolder) view.getTag();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(holder.fileCompletePath), "image/*");
        startActivity(intent);
    }
}
