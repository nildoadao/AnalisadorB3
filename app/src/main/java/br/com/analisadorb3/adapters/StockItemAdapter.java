package br.com.analisadorb3.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.StockRealTimeData;

public class StockItemAdapter extends RecyclerView.Adapter<StockItemAdapter.StockItemHolder> {

    class StockItemHolder extends RecyclerView.ViewHolder{
        private TextView stockName;
        private TextView stockPrice;
        private TextView stockChangePercent;
        private ImageView arrowStatus;

        public StockItemHolder(@NonNull View itemView) {
            super(itemView);
            stockName = itemView.findViewById(R.id.stock_name);
            stockPrice = itemView.findViewById(R.id.stock_price);
            stockChangePercent = itemView.findViewById(R.id.stock_change_percent);
            arrowStatus = itemView.findViewById(R.id.stock_arrow_status);
        }
    }

    private List<StockRealTimeData> stocks = new ArrayList<>();
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public void setStocks(List<StockRealTimeData> stocks){
        this.stocks = stocks;
        notifyDataSetChanged();
    }

    public StockRealTimeData getStockAt(int position){
        return stocks.get(position);
    }

    public interface OnItemClickListener{
        void onItemClick(Context context, int position);
    }

    public interface OnItemLongClickListener{
        boolean OnItemLongClick(Context context, int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public StockItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_card, parent, false);
        return new StockItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockItemHolder holder, int position) {
        StockRealTimeData currentStock = stocks.get(position);
        holder.stockName.setText(currentStock.getName());
        String priceText = String.format("%s %s", currentStock.getPrice(),
                currentStock.getCurrency());
        holder.stockPrice.setText(priceText);
        String changeText = String.format("%s (%s",
                currentStock.getDayChange(), currentStock.getChangePercent()) + "%)";
        holder.stockChangePercent.setText(changeText);

        Double change;
        try {
            change = Double.parseDouble(currentStock.getDayChange());
        }
        catch (Exception e){
            change = null;
        }

        if(change == null){
            holder.stockChangePercent.setVisibility(View.INVISIBLE);
        }
        else if(change >= 0) {
            holder.stockChangePercent.setTextColor(Color.argb(255, 0, 127, 0));
            holder.arrowStatus.setImageResource(R.drawable.arrow_up);
        }
        else {
            holder.stockChangePercent.setTextColor(Color.RED);
            holder.arrowStatus.setImageResource(R.drawable.arrow_down);
        }
    }

    @Override
    public int getItemCount() {
        if(stocks != null)
            return stocks.size();
        else
            return 0;
    }
}
