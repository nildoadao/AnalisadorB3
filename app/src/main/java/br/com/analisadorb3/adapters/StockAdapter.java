package br.com.analisadorb3.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null)
            view = inflater.inflate(R.layout.stock_item, viewGroup, false);

        defineBasicInformation(view, stocks.get(i));
        defineChangeText(view, stocks.get(i));
        setListeners(view, i);
        return view;
    }

    private void setListeners(View view, final int position){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onItemClick(view.getContext(), position);
                }
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(longClickListener != null)
                    return longClickListener.OnLongClick(view.getContext(), position);
                return false;
            }
        });
    }

    private void defineBasicInformation(View view, StockQuote stock){
        TextView stockName = view.findViewById(R.id.stock_name);
        TextView stockPrice = view.findViewById(R.id.stock_info_price);
        stockName.setText(stock.getSymbol());
        stockPrice.setText(String.format("%s %s", stock.getPrice(),
                stock.getCurrency()));
    }

    private void defineChangeText(View view, StockQuote stock){
        TextView stockChange = view.findViewById(R.id.stock_change_percent);
        ImageView arrow = view.findViewById(R.id.stock_arrow_status);
        stockChange.setText(String.format("%s (%s",
                stock.getChange(), stock.getChangePercent()) + "%)");

        Double change;
        try {
            change = Double.parseDouble(stock.getChange());
        }
        catch (Exception e){
            change = null;
        }

        if(change == null){
            stockChange.setVisibility(View.INVISIBLE);
        }
        else if(change >= 0) {
            stockChange.setTextColor(Color.argb(255, 0, 127, 0));
            arrow.setImageResource(R.drawable.arrow_up);
        }
        else {
            stockChange.setTextColor(Color.RED);
            arrow.setImageResource(R.drawable.arrow_down);
        }
    }
}
