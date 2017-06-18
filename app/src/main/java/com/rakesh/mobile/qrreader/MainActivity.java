package com.rakesh.mobile.qrreader;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.text.DateFormat;
import java.util.Date;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.rakesh.mobile.qrreader.data_base.ScanDataBaseHelper;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  public static boolean scanning2DCode = true;
  public boolean isScanTypeQR = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fabric.with(this, new Crashlytics());
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    findViewById(R.id.bt_scan_qr_code).setOnClickListener(this);
    findViewById(R.id.bt_scan_bar_code).setOnClickListener(this);
    findViewById(R.id.bt_generate_qr_code).setOnClickListener(this);
    findViewById(R.id.bt_generate_bar_code).setOnClickListener(this);
    findViewById(R.id.bt_history).setOnClickListener(this);
  }
  /*
   * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the menu; this adds items
   * to the action bar if it is present. getMenuInflater().inflate(R.menu.menu_main, menu); return
   * true; }
   * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle action bar item
   * clicks here. The action bar will // automatically handle clicks on the Home/Up button, so long
   * // as you specify a parent activity in AndroidManifest.xml. int id = item.getItemId(); //
   * noinspection SimplifiableIfStatement if (id == R.id.action_settings) { return true; } return
   * super.onOptionsItemSelected(item); }
   */

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bt_scan_qr_code:
        isScanTypeQR = true;
        scanQrCode();
        break;
      case R.id.bt_scan_bar_code:
        isScanTypeQR = false;
        scanBarCode();
        break;
      case R.id.bt_generate_qr_code:
        Intent intentQRcode = new Intent(MainActivity.this, CreateCodeActivity.class);
        intentQRcode.putExtra(Constants.KEY_GENERATE_QR_CODE, true);
        startActivity(intentQRcode);
        break;
      case R.id.bt_generate_bar_code:
        Intent intentBarCode = new Intent(MainActivity.this, CreateCodeActivity.class);
        intentBarCode.putExtra(Constants.KEY_GENERATE_QR_CODE, false);
        startActivity(intentBarCode);
        break;
      case R.id.bt_history:
        startActivity(new Intent(this, HistoryActivity.class));
        break;
    }
  }

  private void scanQrCode() {
    scanning2DCode = true;
    IntentIntegrator scanIntegrator = new IntentIntegrator(this);
    scanIntegrator.setPrompt("Scan");
    scanIntegrator.setBeepEnabled(true);
    // The following line if you want QR code
    scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
    scanIntegrator.setCaptureActivity(CustomCaptureActivity.class);
    scanIntegrator.setOrientationLocked(true);
    scanIntegrator.setBarcodeImageEnabled(true);
    scanIntegrator.initiateScan();
  }

  private void scanBarCode() {
    scanning2DCode = false;
    IntentIntegrator scanIntegrator = new IntentIntegrator(this);
    scanIntegrator.setPrompt("Scan");
    scanIntegrator.setBeepEnabled(true);
    // The following line if you want QR code
    scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
    scanIntegrator.setCaptureActivity(CustomCaptureActivity.class);
    scanIntegrator.setOrientationLocked(true);
    scanIntegrator.setBarcodeImageEnabled(true);
    scanIntegrator.initiateScan();
  }

  // private void generateQRCode(String content, int contentType, int width, int height,
  // ImageView imageView) {
  // MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
  // try {
  // BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
  // imageView.setImageBitmap(getBitMap(bitMatrix));
  // } catch (WriterException e) {
  // e.printStackTrace();
  // }
  // }
  //
  // private void generateBarCode(String content, int contentType, final int width, final int
  // height,
  // ImageView imageView) {
  //
  // MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
  // try {
  // BitMatrix bitMatrix =
  // multiFormatWriter.encode(content, BarcodeFormat.CODE_128, width, height);
  // imageView.setImageBitmap(getBitMap(bitMatrix));
  // } catch (WriterException e) {
  // e.printStackTrace();
  // }
  // }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    String scanContent = "";
    String scanFormat = "";
    IntentResult scanningResult =
        IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    if (scanningResult != null) {
      if (scanningResult.getContents() != null) {
        scanContent = scanningResult.getContents().toString();
        scanFormat = scanningResult.getFormatName().toString();
        if (null != scanContent) {
          processData(scanContent);
        }
      }
    }
  }

  private void showCodeResults(final String content) {
    final Dialog dialog = new Dialog(this, R.style.full_screen_dialog);
    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT);
    dialog.setContentView(R.layout.popup_code_results);
    dialog.setCanceledOnTouchOutside(false);
    dialog.setCancelable(true);
    dialog.show();
    Toolbar toolbar = (Toolbar) dialog.findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

    toolbar.setTitle(getString(R.string.results));
    ((TextView) dialog.findViewById(R.id.tv_results)).setText(fromHtml(content));
    ((TextView) dialog.findViewById(R.id.tv_results))
        .setMovementMethod(LinkMovementMethod.getInstance());
    dialog.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.app_name), fromHtml(content));
        clipboard.setPrimaryClip(clip);
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.bt_copy_clip_board).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

  }

  private void saveToDB(String content) {
    SQLiteDatabase database = new ScanDataBaseHelper(this).getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(ScanDataBaseHelper.SCAN_COLUMN_RESULT, content);
    values.put(ScanDataBaseHelper.SCAN_COLUMN_DATE,
        DateFormat.getDateTimeInstance().format(new Date()));
    values.put(ScanDataBaseHelper.SCAN_COLUMN_TYPE,
        isScanTypeQR ? Constants.QR_CODE : Constants.BAR_CODE);
    values.put(ScanDataBaseHelper.SCAN_COLUMN_IS_SCANNED,
            0);
    database.insert(ScanDataBaseHelper.SCAN_TABLE_NAME, null, values);
  }

  @SuppressWarnings("deprecation")
  public Spanned fromHtml(String html) {
    Spanned result;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    } else {
      result = Html.fromHtml(html);
    }
    return result;
  }

  private void processData(String content) {
    if (isValidUrl(content)) {
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(content));
      startActivity(i);
    } else {
      showCodeResults(content);
    }
    saveToDB(content);
  }

  private boolean isValidUrl(String url) {
    return URLUtil.isValidUrl(url);
  }

}
