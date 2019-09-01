package br.com.analisadorb3.adaptors;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    StockQuote lastQuote;
    FragmentManager fragmentManager;
    List<StockQuote> intraDayData;
    List<StockQuote> dailyData;

    private static LayoutInflater inflater = null;

    public StockInfoAdapter(Context context, FragmentManager fragmentManager){
        this.context = context;
        this.fragmentManager = fragmentManager;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setLastQuote(StockQuote lastQuote){
        this.lastQuote = lastQuote;
    }

    public void setIntraDayData(List<StockQuote> data){
        this.intraDayData = data;
    }

    public void setDailyData(List<StockQuote> data){
        this.dailyData = data;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return lastQuote;
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
        adapter.setIntraDayData(intraDayData);
        adapter.setDailyData(dailyData);
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

        stockSymbol.setText(lastQuote.getSymbol());
        stockPrice.setText(String.format("%s %s", lastQuote.getPrice(), lastQuote.getCurrency()));
        stockOpen.setText(String.format("%s: %s", context.getString(R.string.open), lastQuote.getOpen()));
        stockPreviousClose.setText(String.format("%s: %s", context.getString(R.string.previous_close),
                lastQuote.getPreviousClose()));
    }

    private void defineDayChangeText(View view){
        TextView stockChange = view.findViewById(R.id.stock_info_change);
        stockChange.setText(String.format("%s (%s", lastQuote.getChange(), lastQuote.getChangePercent()) + "%)");
        Double dayChange;
        try{
            dayChange = Double.parseDouble(lastQuote.getChange());
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
            double averageRange = StockCalculator.getAverageVariation(dailyData, 3);
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
            volume = Double.parseDouble(lastQuote.getVolume());
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
            dayHigh = Double.parseDouble(lastQuote.getHigh());
            dayLow = Double.parseDouble(lastQuote.getLow());
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
                    lastQuote.getLow(), lastQuote.getHigh(), (dayHigh - dayLow)));
        }
    }
}
