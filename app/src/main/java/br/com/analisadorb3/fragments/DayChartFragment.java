package br.com.analisadorb3.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;

import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.util.ChartUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayChartFragment extends Fragment {

    private List<StockQuote> intradayData;

    public DayChartFragment() {
        // Required empty public constructor
    }

    public void setData(List<StockQuote> data){
        this.intradayData = data;
    }

    public static DayChartFragment newInstance(List<StockQuote> data){
        DayChartFragment chart = new DayChartFragment();
        chart.setData(data);
        return chart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_chart, container, false);
        LineChart chart = view.findViewById(R.id.chart);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.setNoDataText("Nenhum dado disponível");
        chart.setData(ChartUtil.getDayChart(chart, intradayData));
        chart.animateX(2500);
        chart.invalidate();
        return view;
    }
}
