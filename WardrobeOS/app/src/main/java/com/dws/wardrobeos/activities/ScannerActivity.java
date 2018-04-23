package com.dws.wardrobeos.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.dws.wardrobeos.Constants;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String mCode;

    @Override
    public void handleResult(Result result) {

        if (!result.getText().isEmpty()) {
            mCode = result.getText();

            Intent intent = new Intent(this, NewActivity.class);
            intent.putExtra(Constants.SOURCE_TYPE, false);
            intent.putExtra(Constants.BARCODE_PRODUCT, mCode);

            startActivity(intent);
            finish();

        } else {
            mScannerView.resumeCameraPreview(this);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

}
