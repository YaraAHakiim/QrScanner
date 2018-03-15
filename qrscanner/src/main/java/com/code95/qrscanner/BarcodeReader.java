package com.code95.qrscanner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Created by YaRa on 3/1/2018.
 */

public class BarcodeReader {

    private static final String TAG = BarcodeReader.class.getSimpleName();

    private static Context mContext;
    private static SurfaceView mSurfaceView;
    private static OnBarcodeScanned mOnBarcodeScanned;
    private static BarcodeDetector mBarcodeDetector;
    private static CameraSource mCameraSource;

    public static void init(Context context, SurfaceView surfaceView, OnBarcodeScanned onBarcodeScanned) {
        mContext = context;
        mSurfaceView = surfaceView;
        mOnBarcodeScanned = onBarcodeScanned;

        mBarcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        mCameraSource = new CameraSource.Builder(context, mBarcodeDetector)
                .setAutoFocusEnabled(true)
                .build();
    }

    public static void scan() {

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                startCamera();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCameraSource.stop();
            }
        });

        mBarcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0) {
                    Vibrator vibrator = (Vibrator) mContext.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(250);
                    mOnBarcodeScanned.onResult(qrcodes.valueAt(0).rawValue);
                }

            }
        });
    }

    public static void startCamera() {
        if (ActivityCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                mCameraSource.start(mSurfaceView.getHolder());
            } catch (IOException exception) {
            }
        }
    }

    public static void release() {
        mBarcodeDetector.release();
    }
}
