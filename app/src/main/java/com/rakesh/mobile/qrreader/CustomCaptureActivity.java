package com.rakesh.mobile.qrreader;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by rakesh.jnanagari on 28/01/17.
 */

public class CustomCaptureActivity extends Activity {
  private static final int MY_PERMISSIONS_REQUEST_CAMERA = 498;
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
    setStatusBarColor();
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
    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.CAMERA},
              MY_PERMISSIONS_REQUEST_CAMERA);
    } else {
      capture.onResume();
    }
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
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_CAMERA: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

          // permission was granted, yay! Do the
          // contacts-related task you need to do.

          capture.onResume();

        } else {

          // permission denied, boo! Disable the
          // functionality that depends on this permission.
          Toast.makeText(this, getString(R.string.request_camera_permission), Toast.LENGTH_LONG).show();
          finish();
        }
        return;
      }

      // other 'case' lines to check for other
      // permissions this app might request

    }
  }


  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    capture.onSaveInstanceState(outState);
  }

//  @Override
//  public void onRequestPermissionsResult(int requestCode, String permissions[],
//      int[] grantResults) {
//    capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
//  }


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

  protected void setStatusBarColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
      window.setStatusBarColor(ContextCompat.getColor(this,R.color.transparent_background_dark));
    }
  }
}
