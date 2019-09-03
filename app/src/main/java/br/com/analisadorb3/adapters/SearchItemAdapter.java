package br.com.analisadorb3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;
import br.com.analisadorb3.R;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.util.SettingsUtil;

public class SearchItemAdapter extends BaseAdapter {
    Context context;
    List<StockQuote> stocks;
    SettingsUtil settings;
    private static LayoutInflater inflater = null;
    private OnItemClickListener clickListener;
    private OnButtonClickListener buttonClickListener;

    public interface OnButtonClickListener{
        void onButtonClick(Context context, int position);
    }

    public interface OnItemClickListener{
        void onItemClick(Context context, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.clickListener = listener;
    }

    public void setOnButtonClickListener(OnButtonClickListener listener){
        this.buttonClickListener = listener;
    }

    public SearchItemAdapter(Context context, List<StockQuote> stocks){
        this.context = context;
        this.stocks = stocks;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        settings = new SettingsUtil(context);
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
            view = inflater.inflate(R.layout.search_item_result, viewGroup, false);

        defineBasicInformation(view, stocks.get(i));
        setIconBackground(view, i);
        setListeners(view, i);
        return view;
    }

    private void setListeners(View view, final int position){
        ImageButton icon = view.findViewById(R.id.search_item_icon);
        final boolean favouriteStock = settings.getFavouriteStocks().contains(stocks.get(position).getSymbol());

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttonClickListener != null && !favouriteStock){
                    buttonClickListener.onButtonClick(view.getContext(), position);
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickListener != null)
                    clickListener.onItemClick(view.getContext(), position);
            }
        });
    }

    private void setIconBackground(View view, int position){
        ImageButton icon = view.findViewById(R.id.search_item_icon);
        final boolean favouriteStock = settings.getFavouriteStocks().contains(stocks.get(position).getSymbol());

        if(favouriteStock){
            icon.setBackgroundResource(R.drawable.star_icon);
        }
        else{
            icon.setBackgroundResource(R.drawable.plus_icon);
        }
    }

    private void defineBasicInformation(View view, StockQuote stock){
        TextView symbol = view.findViewById(R.id.search_item_symbol);
        TextView company = view.findViewById(R.id.search_item_company);
        symbol.setText(stock.getSymbol());
        company.setText(stock.getCompany());
    }
}
