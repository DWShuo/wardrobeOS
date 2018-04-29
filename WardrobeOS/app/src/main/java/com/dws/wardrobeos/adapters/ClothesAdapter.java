package com.dws.wardrobeos.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.dws.wardrobeos.R;
import com.dws.wardrobeos.models.ClothItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ClothesViewHolder> {

    private ClickListener clickListener;

    private List<ClothItem> mClothes;
    private Context context;

    public ClothesAdapter(Context context, List<ClothItem> clothes, ClickListener listener) {
        mClothes = clothes;
        this.context = context;
        this.clickListener = listener;
    }

    @Override
    public ClothesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cloth, parent, false);
        return new ClothesViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ClothesViewHolder holder, int position) {
        ClothItem cloth = mClothes.get(position);
        holder.brandView.setText(cloth.getBrand());
        holder.typeView.setText(cloth.getType());
        holder.colorView.setBackgroundColor(cloth.getColor());
        holder.infoView.setText(cloth.getInfo());
        if (cloth.getSource()) {
            if (cloth.getPhoto() != null) {
                if (cloth.getPhoto().length() != 0) {
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
        } else {
            if (!cloth.getPhoto().isEmpty()) {
                Glide.with(context).load(cloth.getPhoto()).into(holder.imgView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mClothes.size();
    }

    class ClothesViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private final ClickListener mItemClickListener;
        private WeakReference<ClickListener> listenerRef;

        @BindView(R.id.tv_brand) AppCompatTextView brandView;
        @BindView(R.id.tv_info) AppCompatTextView infoView;
        @BindView(R.id.tv_type) AppCompatTextView typeView;
        @BindView(R.id.view_color) View colorView;
        @BindView(R.id.iv_item_image) AppCompatImageView imgView;
        @BindView(R.id.container) RelativeLayout container;

        public ClothesViewHolder(View itemView, ClickListener itemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            listenerRef = new WeakReference<>(itemClickListener);

            this.mItemClickListener = itemClickListener;
            container.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemClickListener != null) {
                listenerRef.get().longPressed(getAdapterPosition());
            }
            return true;
        }
    }

    public interface ClickListener {
        void longPressed(int position);
    }
}
