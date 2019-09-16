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
import br.com.analisadorb3.models.StockRealTimeData;

public class StockItemAdapter extends ListAdapter<StockRealTimeData, StockItemAdapter.StockItemHolder> {

    public StockItemAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<StockRealTimeData> DIFF_CALLBACK = new DiffUtil.ItemCallback<StockRealTimeData>() {
        @Override
        public boolean areItemsTheSame(@NonNull StockRealTimeData oldItem, @NonNull StockRealTimeData newItem) {
            return oldItem.getSymbol().equals(newItem.getSymbol());
        }

        @Override
        public boolean areContentsTheSame(@NonNull StockRealTimeData oldItem, @NonNull StockRealTimeData newItem) {
            return oldItem.getSymbol().equals(newItem.getSymbol()) &&
                    oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getPrice().equals(newItem.getPrice()) &&
                    oldItem.getCurrency().equals(newItem.getCurrency());
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

    public StockRealTimeData getStockAt(int position){
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
        StockRealTimeData currentStock = getItem(position);
        holder.stockName.setText(currentStock.getName());
        String priceText = String.format("%s %s", currentStock.getPrice(),
                currentStock.getCurrency());
        holder.stockPrice.setText(priceText);
        holder.stockSymbol.setText(currentStock.getSymbol());
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

}
