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
import br.com.analisadorb3.logic.StockCalculator;
import br.com.analisadorb3.models.StockQuote;

public class StockInfoAdapter extends BaseAdapter {
    Context context;
    StockQuote stock;
    List<StockQuote> list;

    private static LayoutInflater inflater = null;

    public StockInfoAdapter(Context context, StockQuote stock, List<StockQuote> list){
        this.context = context;
        this.stock = stock;
        this.list = list;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return stock;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.stock_info_item, null);

        TextView stockSymbol = view.findViewById(R.id.stock_info_symbol);
        TextView stockPrice = view.findViewById(R.id.stock_info_price);
        TextView stockChange = view.findViewById(R.id.stock_info_change);
        TextView stockOpen = view.findViewById(R.id.stock_info_open);
        TextView stockPreviousClose = view.findViewById(R.id.stock_info_previous_close);
        TextView stockVolume = view.findViewById(R.id.stock_info_volume);
        TextView stockDayRange = view.findViewById(R.id.stock_info_day_range);
        TextView stockAverageRange3 = view.findViewById(R.id.stock_info_average_range_3);

        stockSymbol.setText(stock.getSymbol());
        stockPrice.setText(String.format("%.2f %s", stock.getPrice(), stock.getCurrency()));
        stockChange.setText(String.format("%.2f (%s", stock.getChange(), stock.getChangePercent()) + "%)");
        stockOpen.setText(String.format("%s: %.2f", context.getString(R.string.open), stock.getOpen()));
        stockPreviousClose.setText(String.format("%s: %.2f", context.getString(R.string.previous_close),
                stock.getPreviousClose()));
        stockVolume.setText(String.format("%s: %s", context.getString(R.string.volume),
                StockCalculator.doublePrettify(stock.getVolume())));
        stockDayRange.setText(String.format("%s: %.2f - %.2f (%.2f)", context.getString(R.string.day_change),
                stock.getLow(), stock.getHigh(), (stock.getHigh() - stock.getLow())));

        double averageRange = StockCalculator.getAverageVariation(list, 3);
        stockAverageRange3.setText(String.format("%s: %.2f", context.getString(R.string.average_change_3), averageRange));

        if(stock.getChange() >= 0)
            stockChange.setTextColor(Color.argb(255, 0, 127, 0));
        else
            stockChange.setTextColor(Color.RED);

        return view;
    }
}
