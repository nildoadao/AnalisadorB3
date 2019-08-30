package br.com.analisadorb3.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.util.ChartUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SixMonthsChartFragment extends Fragment {

    private List<StockQuote> monthData;

    public void setData(List<StockQuote> data){
        monthData = data;
    }

    public SixMonthsChartFragment() {
        // Required empty public constructor
    }

    public static SixMonthsChartFragment newInstance(List<StockQuote> data){
        SixMonthsChartFragment chart = new SixMonthsChartFragment();
        chart.setData(data);
        return chart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_chart, container, false);
        LineChart chart = view.findViewById(R.id.chart);
        chart.setData(ChartUtil.getSixMonthsChart(chart, monthData));
        chart.animateX(2500);
        chart.invalidate();
        return view;
    }

}
