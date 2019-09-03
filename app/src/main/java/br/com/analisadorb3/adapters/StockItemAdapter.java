package br.com.analisadorb3.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.view.StockItemHolder;

public class StockItemAdapter extends RecyclerView.Adapter<StockItemHolder> {

    private final List<StockQuote> stocks;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener{
        void onItemClick(Context context, int position);
    }

    public interface OnItemLongClickListener{
        boolean OnItemLongClick(Context context, int position);
    }
    public StockItemAdapter(List<StockQuote> stocks){
        this.stocks = stocks;
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
        return new StockItemHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StockItemHolder holder, final int position) {
        StockQuote stock = stocks.get(position);
        holder.stockSymbol.setText(stock.getSymbol());
        holder.stockPrice.setText(String.format("%s %s", stock.getPrice(), stock.getCurrency()));
        holder.stockChange.setText(String.format("%s (%s",
                stock.getChange(), stock.getChangePercent()) + "%)");

        Double change;
        try {
            change = Double.parseDouble(stock.getChange());
        }
        catch (Exception e){
            change = null;
        }

        if(change == null){
            holder.stockChange.setVisibility(View.INVISIBLE);
        }
        else if(change >= 0) {
            holder.stockChange.setTextColor(Color.argb(255, 0, 127, 0));
            holder.changeArrow.setImageResource(R.drawable.arrow_up);
        }
        else {
            holder.stockChange.setTextColor(Color.RED);
            holder.changeArrow.setImageResource(R.drawable.arrow_down);
        }

        holder.stockCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickListener != null){
                    clickListener.onItemClick(view.getContext(), position);
                }
            }
        });

        holder.stockCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(longClickListener != null){
                    longClickListener.OnItemLongClick(view.getContext(), position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return stocks != null ? stocks.size() : 0;
    }
}
