package com.rakesh.mobile.qrreader;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by rakesh.jnanagari on 29/01/17.
 */

public class CreateCodeActivity extends AppCompatActivity {

  private final static int QR_CODE_DIMEN = 400;
  private final static int BAR_CODE_WIDTH = 400;
  private final static int BAR_CIDE_HEIGHT = 150;
  private boolean isQRCode;
  private int selectedContentType = 0;
  private String[] contentTypeValues;
  private EditText etContentType;
  private EditText etTextContentValue;
  private Button btCreate;
  private ImageView ivCode;
  private ImageView ivShare;
  private Bitmap generatedBitMap;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_code);

    isQRCode = getIntent().getBooleanExtra(Constants.KEY_GENERATE_QR_CODE, true);

    etContentType = (EditText) findViewById(R.id.et_content_type);
    etTextContentValue = (EditText) findViewById(R.id.et_content_value);
    btCreate = (Button) findViewById(R.id.bt_create);
    ivCode = (ImageView) findViewById(R.id.iv_generated_code);
    ivShare = (ImageView) findViewById(R.id.iv_share);

    etContentType
        .setText(getResources().getStringArray(R.array.content_type_array)[selectedContentType]);

    etContentType.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showContentTypePopup();
      }
    });

    btCreate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        InputMethodManager imm =
            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btCreate.getWindowToken(), 0);
        switch (selectedContentType) {
          case 0:
            if (isValidText(etTextContentValue.getText().toString())) {
              generateCode(etTextContentValue.getText().toString());
            } else {
              Toast.makeText(getApplicationContext(), getString(R.string.invalid_text),
                  Toast.LENGTH_LONG).show();
            }
            break;
          case 1:
            if (isValidUrl(etTextContentValue.getText().toString())) {
              generateCode(etTextContentValue.getText().toString());
            } else {
              Toast.makeText(getApplicationContext(), getString(R.string.invalid_url),
                  Toast.LENGTH_LONG).show();
            }
            break;
        }
      }
    });
    etContentType
        .setText(getResources().getStringArray(R.array.content_type_array)[selectedContentType]);
    etTextContentValue.requestFocus();
  }

  private void showContentTypePopup() {
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.popup_content_type);
    dialog.setCanceledOnTouchOutside(false);
    dialog.setCancelable(true);
    dialog.show();
    dialog.findViewById(R.id.rb_free_text).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectedContentType = 0;
        ivCode.setVisibility(View.GONE);
        ivShare.setVisibility(View.GONE);
        etContentType.setText(
            getResources().getStringArray(R.array.content_type_array)[selectedContentType]);
        etTextContentValue.setLines(5);
        etTextContentValue.setHint(getString(R.string.free_text_hint));
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.rb_url).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        selectedContentType = 1;
        ivCode.setVisibility(View.GONE);
        ivShare.setVisibility(View.GONE);
        etContentType.setText(
            getResources().getStringArray(R.array.content_type_array)[selectedContentType]);
        etTextContentValue.setLines(1);
        etTextContentValue.setHint(getString(R.string.url_hint));
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.rb_images).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ivCode.setVisibility(View.GONE);
        ivShare.setVisibility(View.GONE);
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.rb_invitation).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ivCode.setVisibility(View.GONE);
        ivShare.setVisibility(View.GONE);
        dialog.dismiss();
      }
    });
    dialog.findViewById(R.id.rb_visiting_card).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ivCode.setVisibility(View.GONE);
        ivShare.setVisibility(View.GONE);
        dialog.dismiss();
      }
    });
  }

  private void generateCode(String content) {
    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
    try {
      if (!isQRCode) {
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.CODE_128,
            BAR_CODE_WIDTH, BAR_CIDE_HEIGHT);
        ivCode.setImageBitmap(generatedBitMap = getBitMap(bitMatrix));
        ivCode.setVisibility(View.VISIBLE);
        ivShare.setVisibility(View.VISIBLE);
      } else {
        BitMatrix bitMatrix =
            multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, QR_CODE_DIMEN, QR_CODE_DIMEN);
        ivCode.setImageBitmap(generatedBitMap = getBitMap(bitMatrix));
        ivShare.setVisibility(View.VISIBLE);
        ivCode.setVisibility(View.VISIBLE);
      }
      ivShare.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

          String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
          File myDir = new File(root + "/saved_images");
          myDir.mkdirs();
          Random generator = new Random();
          int n = 10000;
          n = generator.nextInt(n);
          String fname = "Image-" + n + ".jpg";
          File file = new File(myDir, fname);
          if (file.exists())
            file.delete();
          try {
            FileOutputStream out = new FileOutputStream(file);
            generatedBitMap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
          }
          catch (Exception e) {
            e.printStackTrace();
          }


          // Tell the media scanner about the new file so that it is
          // immediately available to the user.
          MediaScannerConnection.scanFile(CreateCodeActivity.this, new String[] { file.toString() }, null,
                  new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                      Log.i("ExternalStorage", "Scanned " + path + ":");
                      Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                  });

          Uri uri = Uri.parse("file://"+file.getAbsolutePath());
          Intent share = new Intent(Intent.ACTION_SEND);
          share.putExtra(Intent.EXTRA_STREAM, uri);
          share.setType("image/*");
          share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
          startActivity(Intent.createChooser(share, "Share image File"));
        }
      });
    } catch (WriterException e) {
      e.printStackTrace();
    }

  }

  private Bitmap getBitMap(BitMatrix bitMatrix) {
    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
    return barcodeEncoder.createBitmap(bitMatrix);
  }

  private boolean isValidText(String content) {
    if (null != content && !content.isEmpty()) {
      return true;
    }
    return false;
  }

  private boolean isValidUrl(String url) {
    return URLUtil.isValidUrl(url);
  }
}
