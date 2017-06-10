package com.rakesh.mobile.qrreader;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by rakesh.jnanagari on 28/01/17.
 */

public class CustomCaptureActivity extends Activity {
  private CaptureManager capture;
  private CompoundBarcodeView barcodeScannerView;
  private boolean isFlashLightOn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    barcodeScannerView = initializeContent();

    capture = new CaptureManager(this, barcodeScannerView);
    capture.initializeFromIntent(getIntent(), savedInstanceState);
    capture.decode();
    if (MainActivity.scanning2DCode) {
      lockOrientationPortrait();
    } else {
      lockOrientationLandscape();
    }

    findViewById(R.id.iv_light_image).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
          if (!isFlashLightOn) {
            isFlashLightOn = true;
            barcodeScannerView.setTorchOn();
          } else {
            isFlashLightOn = false;
            barcodeScannerView.setTorchOff();
          }
        }
      }
    });
  }

  /**
   * Override to use a different layout.
   *
   * @return the CompoundBarcodeView
   */
  protected CompoundBarcodeView initializeContent() {
    setContentView(R.layout.activity_capture);
    return (CompoundBarcodeView) findViewById(R.id.zxing_qr_code_scanner);
  }

  @Override
  protected void onResume() {
    super.onResume();
    capture.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
    capture.onPause();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    capture.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    capture.onSaveInstanceState(outState);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[],
      int[] grantResults) {
    capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }


  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
  }

  /** Locks the device window in landscape mode. */
  public void lockOrientationLandscape() {
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  }

  /** Locks the device window in portrait mode. */
  public void lockOrientationPortrait() {
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

  @Override
  protected void onStop() {
    super.onStop();
    isFlashLightOn = false;
  }
}
