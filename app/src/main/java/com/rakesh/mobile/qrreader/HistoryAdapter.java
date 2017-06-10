package com.rakesh.mobile.qrreader;

import java.util.List;

import com.rakesh.mobile.qrreader.data_base.History;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by rakesh.jnanagari on 07/05/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

  private List<History> historyList;
  private IHistoryAdapter iHistoryAdapter;

  public HistoryAdapter(List<History> historieList, IHistoryAdapter iHistoryAdapter) {
    this.historyList = historieList;
    this.iHistoryAdapter = iHistoryAdapter;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_code, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.tvResult.setText(historyList.get(position).getResult());
    holder.tvDate.setText(historyList.get(position).getDate());
    if (historyList.get(position).getType().equals(Constants.QR_CODE)) {
      holder.ivIcon.setImageResource(R.drawable.icon_qr_code);
    } else {
      holder.ivIcon.setImageResource(R.drawable.icon_bar_code);
    }
    holder.root.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        iHistoryAdapter.onItemClick(holder.getAdapterPosition());
      }
    });
    holder.ivDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        iHistoryAdapter.onDeleteClick(holder.getAdapterPosition());
      }
    });
  }

  @Override
  public int getItemCount() {
    return historyList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvResult;
    TextView tvDate;
    ImageView ivIcon;
    ImageView ivDelete;
    View root;

    public ViewHolder(View view) {
      super(view);
      tvResult = (TextView) view.findViewById(R.id.tv_result);
      tvDate = (TextView) view.findViewById(R.id.tv_date);
      ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
      ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
      root = view;
    }
  }

  public interface IHistoryAdapter {
    void onDeleteClick(int position);
    void onItemClick(int position);
  }
}
