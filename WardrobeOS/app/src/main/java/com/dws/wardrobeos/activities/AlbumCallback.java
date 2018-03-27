package com.dws.wardrobeos.activities;

import com.dws.wardrobeos.models.AlbumFile;

import java.util.ArrayList;

public interface AlbumCallback {

    /**
     * Photo album callback selection result.
     *
     * @param albumFiles file path list.
     */
    void onAlbumResult(ArrayList<AlbumFile> albumFiles);

    /**
     * The album canceled the operation.
     */
    void onAlbumCancel();


}