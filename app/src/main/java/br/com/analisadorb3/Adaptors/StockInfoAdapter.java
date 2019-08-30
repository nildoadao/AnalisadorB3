package br.com.analisadorb3.adaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import br.com.analisadorb3.R;
import br.com.analisadorb3.logic.StockCalculator;
import br.com.analisadorb3.models.StockQuote;

public class StockInfoAdapter extends BaseAdapter {
    Context context;
    StockQuote stock;
    List<StockQuote> list;
    FragmentManager fragmentManager;
    List<StockQuote> dailyData;
    List<StockQuote> monthData;

    private static LayoutInflater inflater = null;

    public StockInfoAdapter(Context context, StockQuote stock, List<StockQuote> list, FragmentManager fragmentManager){
        this.context = context;
        this.stock = stock;
        this.list = list;
        this.fragmentManager = fragmentManager;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDailyData(List<StockQuote> data){
        this.dailyData = data;
    }

    public void setMonthData(List<StockQuote> data){
        this.monthData = data;
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
            view = inflater.inflate(R.layout.stock_info_item, viewGroup, false);
        ViewPager pager = view.findViewById(R.id.stock_info_view_pager);
        StockChartAdapter adapter = new StockChartAdapter(context, fragmentManager);
        adapter.setDailyData(dailyData);
        adapter.setMonthData(monthData);
        pager.setAdapter(adapter);
        TabLayout chartsTab =  view.findViewById(R.id.stock_info_charts_tab);
        chartsTab.setupWithViewPager(pager);
        defineBasicInformation(view);
        defineDayChangeText(view);
        defineVolumeText(view);
        defineDayRangeText(view);
        defineAverageChangeText(view);
        return view;
    }

    private void defineBasicInformation(View view){
        TextView stockSymbol = view.findViewById(R.id.stock_info_symbol);
        TextView stockPrice = view.findViewById(R.id.stock_info_price);
        TextView stockOpen = view.findViewById(R.id.stock_info_open);
        TextView stockPreviousClose = view.findViewById(R.id.stock_info_previous_close);

        stockSymbol.setText(stock.getSymbol());
        stockPrice.setText(String.format("%s %s", stock.getPrice(), stock.getCurrency()));
        stockOpen.setText(String.format("%s: %s", context.getString(R.string.open), stock.getOpen()));
        stockPreviousClose.setText(String.format("%s: %s", context.getString(R.string.previous_close),
                stock.getPreviousClose()));
    }

    private void defineDayChangeText(View view){
        TextView stockChange = view.findViewById(R.id.stock_info_change);
        stockChange.setText(String.format("%s (%s", stock.getChange(), stock.getChangePercent()) + "%)");
        Double dayChange;
        try{
            dayChange = Double.parseDouble(stock.getChange());
        }
        catch (Exception e){
            dayChange = null;
        }

        if(dayChange == null){
            stockChange.setText("-");
        }
        else if(dayChange >= 0)
            stockChange.setTextColor(Color.argb(255, 0, 127, 0));
        else
            stockChange.setTextColor(Color.RED);
    }

    private void defineAverageChangeText(View view){
        TextView stockAverageRange3 = view.findViewById(R.id.stock_info_average_range_3);
        try{
            double averageRange = StockCalculator.getAverageVariation(list, 3);
            stockAverageRange3.setText(String.format("%s: %.2f", context.getString(R.string.average_change_3), averageRange));
        }
        catch (Exception e){
            stockAverageRange3.setText(String.format("%s: N/A", context.getString(R.string.average_change_3)));
        }
    }

    private void defineVolumeText(View view){
        TextView stockVolume = view.findViewById(R.id.stock_info_volume);

        Double volume;
        try{
            volume = Double.parseDouble(stock.getVolume());
        }
        catch (Exception e){
            volume = null;
        }

        if(volume == null){
            stockVolume.setText(String.format("%s: N/A", context.getString(R.string.volume)));
        }
        else {
            stockVolume.setText(String.format("%s: %s", context.getString(R.string.volume),
                    StockCalculator.doublePrettify(volume)));
        }
    }

    private void defineDayRangeText(View view){
        TextView stockDayRange = view.findViewById(R.id.stock_info_day_range);
        Double dayHigh;
        Double dayLow;

        try{
            dayHigh = Double.parseDouble(stock.getHigh());
            dayLow = Double.parseDouble(stock.getLow());
        }
        catch (Exception e){
            dayHigh = null;
            dayLow = null;
        }

        if(dayHigh == null || dayLow == null){
            stockDayRange.setText(String.format("%s: N/A", context.getString(R.string.day_change)));
        }
        else {
            stockDayRange.setText(String.format("%s: %s - %s (%.2f)", context.getString(R.string.day_change),
                    stock.getLow(), stock.getHigh(), (dayHigh - dayLow)));
        }
    }
}
