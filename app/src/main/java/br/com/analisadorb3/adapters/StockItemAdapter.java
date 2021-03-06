package br.com.analisadorb3.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.YahooStockData;

public class StockItemAdapter extends ListAdapter<YahooStockData, StockItemAdapter.StockItemHolder> {

    public StockItemAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<YahooStockData> DIFF_CALLBACK = new DiffUtil.ItemCallback<YahooStockData>() {
        @Override
        public boolean areItemsTheSame(@NonNull YahooStockData oldItem, @NonNull YahooStockData newItem) {
            return oldItem.getChart().getResult().get(0).getMeta().getSymbol().equals(newItem.getChart().getResult().get(0).getMeta().getSymbol());
        }

        @Override
        public boolean areContentsTheSame(@NonNull YahooStockData oldItem, @NonNull YahooStockData newItem) {
            return oldItem.getChart().getResult().get(0).getMeta().getSymbol().equals(newItem.getChart().getResult().get(0).getMeta().getSymbol()) &&
                    oldItem.getChart().getResult().get(0).getMeta().getSymbol().equals(newItem.getChart().getResult().get(0).getMeta().getSymbol()) &&
                    oldItem.getChart().getResult().get(0).getMeta().getSymbol().equals(newItem.getChart().getResult().get(0).getMeta().getSymbol()) &&
                    oldItem.getChart().getResult().get(0).getMeta().getSymbol().equals(newItem.getChart().getResult().get(0).getMeta().getSymbol());
        }
    };

    class StockItemHolder extends RecyclerView.ViewHolder{
        private TextView stockName;
        private TextView stockPrice;
        private TextView stockSymbol;
        private TextView stockChangePercent;
        private ImageView arrowStatus;

        public StockItemHolder(@NonNull final View itemView) {
            super(itemView);
            stockName = itemView.findViewById(R.id.stock_name);
            stockPrice = itemView.findViewById(R.id.stock_price);
            stockSymbol = itemView.findViewById(R.id.stock_symbol);
            stockChangePercent = itemView.findViewById(R.id.stock_change_percent);
            arrowStatus = itemView.findViewById(R.id.stock_arrow_status);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(clickListener != null && position != RecyclerView.NO_POSITION){
                        clickListener.onItemClick(itemView.getContext(), position);
                    }
                }
            });
        }
    }

    private OnItemClickListener clickListener;

    public YahooStockData getStockAt(int position){
        return getItem(position);
    }

    public interface OnItemClickListener{
        void onItemClick(Context context, int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
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
        YahooStockData currentStock = getItem(position);
        holder.stockName.setText(currentStock.getChart().getResult().get(0).getMeta().getSymbol());
        String priceText = String.format("%s %s", currentStock.getChart().getResult().get(0).getMeta().getRegularMarketPrice(),
                currentStock.getChart().getResult().get(0).getMeta().getCurrency());
        holder.stockPrice.setText(priceText);
        holder.stockSymbol.setText(currentStock.getChart().getResult().get(0).getMeta().getSymbol());

        Double dayChange = Double.parseDouble(currentStock.getChart().getResult().get(0).getMeta().getRegularMarketPrice())
                - Double.parseDouble(currentStock.getChart().getResult().get(0).getMeta().getPreviousClose());

        Double dayChangePercent = dayChange / Double.parseDouble(currentStock.getChart().getResult().get(0).getMeta().getRegularMarketPrice()) * 100;

        String changeText = String.format("%.2f (%.2f",
                dayChange, dayChangePercent) + "%)";
        holder.stockChangePercent.setText(changeText);

        Double change;
        try {
            change = dayChange;
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

}
