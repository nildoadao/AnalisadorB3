package br.com.analisadorb3.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
public class MonthChartFragment extends Fragment {

    private List<StockQuote> monthData;

    public MonthChartFragment() {
        // Required empty public constructor
    }

    public void setData(List<StockQuote> data){
        monthData = data;
    }

    public static MonthChartFragment newInstance(List<StockQuote> data){
        MonthChartFragment chart = new MonthChartFragment();
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
        chart.setData(ChartUtil.getMonthChart(chart, monthData));
        chart.animateX(2500);
        chart.invalidate();
        return view;
    }
}
