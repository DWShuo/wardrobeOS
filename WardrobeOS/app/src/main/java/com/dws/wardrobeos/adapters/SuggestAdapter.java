package com.dws.wardrobeos.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dws.wardrobeos.R;
import com.dws.wardrobeos.models.ClothItem;

import java.io.File;
import java.util.List;

public class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.SuggestViewHolder> {

    private Context context;
    private List<ClothItem> mClothes;

    public SuggestAdapter(Context context, List<ClothItem> clothes) {
        mClothes = clothes;
        this.context = context;
    }

    @NonNull
    @Override
    public SuggestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        return new SuggestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestViewHolder holder, int position) {
        ClothItem cloth = mClothes.get(position);

        if (cloth.getPhoto() != null) {
            Log.d("Test", cloth.getPhoto());
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraDemo");
            final File file = new File(mediaStorageDir, cloth.getPhoto());
            if (file.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                holder.imgView.setImageBitmap(bitmap);

                Glide.with(context)
                        .load(bitmap)
                        .into(holder.imgView);

            }
        }
    }

    @Override
    public int getItemCount() {
        return mClothes.size();
    }

    public class SuggestViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imgView;
        public SuggestViewHolder(View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.img_item);
        }
    }
}
