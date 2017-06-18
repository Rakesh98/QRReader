package com.rakesh.mobile.qrreader;

import java.util.ArrayList;
import java.util.List;

import com.rakesh.mobile.qrreader.data_base.History;
import com.rakesh.mobile.qrreader.data_base.ScanDataBaseHelper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

/**
 * Created by rakesh.jnanagari on 05/02/17.
 */

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.IHistoryAdapter {

  private RecyclerView rvHistoryList;
  private HistoryAdapter mHistoryAdapter;
  private List<History> historyList;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_history);
    rvHistoryList = (RecyclerView) findViewById(R.id.rv_history);
    readFromDB();
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
    toolbar.setTitle(getString(R.string.history));
  }


  @Override
  public void onDeleteClick(int position) {

  }

  @Override
  public void onItemClick(int position) {
    if (isValidUrl(historyList.get(position).getResult())) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(historyList.get(position).getResult()));
      startActivity(intent);
    } else {
      ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
      ClipData clip = ClipData.newPlainText(getString(R.string.app_name),
          fromHtml(historyList.get(position).getResult()));
      clipboard.setPrimaryClip(clip);
      Toast.makeText(this, getString(R.string.copied_text_to_clip_board), Toast.LENGTH_SHORT)
          .show();
    }
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


  private boolean isValidUrl(String url) {
    return URLUtil.isValidUrl(url);
  }

  private void readFromDB() {
    SQLiteDatabase database = new ScanDataBaseHelper(this).getReadableDatabase();

    Cursor cursor = database.rawQuery("select * from " + ScanDataBaseHelper.SCAN_TABLE_NAME, null);
    if (cursor.moveToFirst()) {
      historyList = new ArrayList<>();
      History history;
      while (cursor.isAfterLast() == false) {
        history = new History();
        history.setResult(
            cursor.getString(cursor.getColumnIndex(ScanDataBaseHelper.SCAN_COLUMN_RESULT)));
        history
            .setDate(cursor.getString(cursor.getColumnIndex(ScanDataBaseHelper.SCAN_COLUMN_DATE)));
        history
            .setType(cursor.getString(cursor.getColumnIndex(ScanDataBaseHelper.SCAN_COLUMN_TYPE)));
          history.setScanned(0 == cursor.getInt(cursor.getColumnIndex(ScanDataBaseHelper.SCAN_COLUMN_IS_SCANNED)));
        historyList.add(history);
        cursor.moveToNext();
      }
      mHistoryAdapter = new HistoryAdapter(historyList, this);
      rvHistoryList.setLayoutManager(new LinearLayoutManager(this));
      rvHistoryList.setAdapter(mHistoryAdapter);
    }
  }
}
