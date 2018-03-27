package com.dws.wardrobeos.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dws.wardrobeos.R;
import com.dws.wardrobeos.models.Album;
import com.dws.wardrobeos.models.AlbumFile;

import java.lang.ref.WeakReference;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflator;
    private int itemSize;
    private ClickListener clickListener;

    private List<AlbumFile> mAlbumFiles;

    public AlbumAdapter(Context context, int itemSize, ClickListener listener) {
        this.clickListener = listener;
        this.itemSize = itemSize;
        this.mInflator = LayoutInflater.from(context);
    }

    public void notifyDataSetChanged(List<AlbumFile> imagePathList) {
        this.mAlbumFiles = imagePathList;
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(mInflator.inflate(R.layout.item_content_image, parent, false), itemSize, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ImageViewHolder) holder).setData(mAlbumFiles.get(position));
    }

    @Override
    public int getItemCount() {
        return mAlbumFiles == null ? 0 : mAlbumFiles.size();
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final int itemSize;
        private final ClickListener mItemClickListener;
        private WeakReference<ClickListener> listenerRef;

        private ImageView mIvImage;

        ImageViewHolder(View itemView, int itemSize, ClickListener itemClickListener) {
            super(itemView);
            itemView.getLayoutParams().height = itemSize;

            listenerRef = new WeakReference<>(itemClickListener);
            this.itemSize = itemSize;
            this.mItemClickListener = itemClickListener;

            mIvImage = itemView.findViewById(R.id.iv_album_content_image);

            itemView.setOnClickListener(this);
        }

        public void setData(AlbumFile albumFile) {
            Album.getAlbumConfig().
                    getAlbumLoader().
                    loadAlbumFile(mIvImage, albumFile, itemSize, itemSize);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
               listenerRef.get().positionClicked(getAdapterPosition());
            }
        }
    }

    public interface ClickListener {
        void positionClicked(int position);
    }
}
