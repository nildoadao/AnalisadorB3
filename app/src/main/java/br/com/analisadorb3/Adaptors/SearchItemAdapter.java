package br.com.analisadorb3.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.StockQuote;

public class SearchItemAdapter extends BaseAdapter {
    Context context;
    List<StockQuote> stocks;
    private static LayoutInflater inflater = null;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(Context context, int position);
    }

    public SearchItemAdapter(Context context, List<StockQuote> stocks){
        this.context = context;
        this.stocks = stocks;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<StockQuote> getData(){
        return this.stocks;
    }

    public void setData(List<StockQuote> stocks){
        this.stocks = stocks;
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
            view = inflater.inflate(R.layout.search_item_result, null);

        TextView symbol = view.findViewById(R.id.search_item_symbol);
        TextView company = view.findViewById(R.id.search_item_company);
        symbol.setText(stocks.get(i).getSymbol());
        company.setText(stocks.get(i).getCompany());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null)
                    listener.onItemClick(view.getContext(), i);
            }
        });

        return view;
    }
}
