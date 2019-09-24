package br.com.analisadorb3.adaptors;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.fragments.DayChartFragment;
import br.com.analisadorb3.fragments.MonthChartFragment;
import br.com.analisadorb3.fragments.SixMonthsChartFragment;
import br.com.analisadorb3.fragments.TreeDayChartFragment;
import br.com.analisadorb3.models.StockQuote;

public class StockChartAdapter extends FragmentStatePagerAdapter {

    Context context;
    List<StockQuote> intraDayData;
    List<StockQuote> dailyData;

    public StockChartAdapter(Context context, FragmentManager fragmentManager){
        super(fragmentManager);
        this.context = context;
    }

   public void setIntraDayData(List<StockQuote> data){
        this.intraDayData = data;
   }

   public void setDailyData(List<StockQuote> data){
        dailyData = data;
   }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return DayChartFragment.newInstance(intraDayData);
        else if(position == 1)
            return TreeDayChartFragment.newInstance(intraDayData);
        else if(position == 2)
            return MonthChartFragment.newInstance(dailyData);
        else
            return SixMonthsChartFragment.newInstance(dailyData);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return context.getString(R.string.one_day);
            case 1:
                return context.getString(R.string.tree_days);
            case 2:
                return context.getString(R.string.one_month);
            case 3:
                return context.getString(R.string.six_months);
            default:
                return null;
        }
    }
}