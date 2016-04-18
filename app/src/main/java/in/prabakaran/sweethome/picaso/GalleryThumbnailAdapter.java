package in.prabakaran.sweethome.picaso;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import in.prabakaran.sweethome.R;

/**
 * Created by Prabakaran on 18-04-2016.
 */
public class GalleryThumbnailAdapter extends CursorAdapter {

    public GalleryThumbnailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.thumbnail_gallery, parent, false);

        GalleryViewHolder viewHolder = new GalleryViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        GalleryViewHolder galleryViewHolder = (GalleryViewHolder) view.getTag();
        galleryViewHolder.id = cursor.getString(0);
        Uri uri = Uri.fromFile(new File(cursor.getString(2).substring(7,cursor.getString(2).length()) + cursor.getString(1)));
        galleryViewHolder.fileCompletePath = cursor.getString(2) + cursor.getString(1);
        Picasso.with(mContext).load(uri).resize(1000,1000).centerCrop().into(galleryViewHolder.imageView);
//        File imgFile = new File(cursor.getString(2).substring(7,cursor.getString(2).length()) + cursor.getString(1));
//
//        if(imgFile.exists()){
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            galleryViewHolder.imageView.setImageBitmap(myBitmap);
//        }
    }

    public static class GalleryViewHolder{
        public final ImageView imageView;
        public String id;
        public String fileCompletePath;

        public GalleryViewHolder(View v){
            imageView = (ImageView) v.findViewById(R.id.gallery_grid);
            fileCompletePath = null;
        }
    }
}
