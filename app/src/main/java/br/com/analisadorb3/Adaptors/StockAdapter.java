package br.com.analisadorb3.adaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.StockQuote;

public class StockAdapter extends BaseAdapter {
    Context context;
    List<StockQuote> stocks;
    private static LayoutInflater inflater = null;
    private OnItemClickListener listener;
    private OnLongClickListener longClickListener;

    public interface OnItemClickListener{
        void onItemClick(Context context, int position);
    }

    public interface OnLongClickListener{
        boolean OnLongClick(Context context, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public void setOnLongClickListener(OnLongClickListener listener) {this.longClickListener = listener;}

    public StockAdapter(Context context, List<StockQuote> stocks){
        this.context = context;
        this.stocks = stocks;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return stocks.size();
    }

    @Override
    public Object getItem(int i) {
        return stocks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (view == null)
            view = inflater.inflate(R.layout.stock_item, null);

        TextView stockName = view.findViewById(R.id.stock_name);
        TextView stockPrice = view.findViewById(R.id.stock_info_price);
        TextView stockChange = view.findViewById(R.id.stock_change_percent);

        stockName.setText(stocks.get(i).getSymbol());
        stockPrice.setText(String.format("%.2f %s", stocks.get(i).getPrice(),
                stocks.get(i).getCurrency()));
        stockChange.setText(String.format("%.2f (%s",
                stocks.get(i).getChange(), stocks.get(i).getChangePercent()) + "%)");

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onItemClick(view.getContext(), i);
                }
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(longClickListener != null)
                    return longClickListener.OnLongClick(view.getContext(), i);
                return false;
            }
        });

        if(stocks.get(i).getChange() >= 0) {
            stockChange.setTextColor(Color.argb(255, 0, 127, 0));
        }
        else{
            stockChange.setTextColor(Color.RED);
        }
        return view;
    }
}
