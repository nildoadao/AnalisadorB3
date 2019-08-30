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
public class TreeDayChartFragment extends Fragment {

    private List<StockQuote> intraDayData;

    public TreeDayChartFragment() {
        // Required empty public constructor
    }

    private void setData(List<StockQuote> data){
        intraDayData = data;
    }

    public static TreeDayChartFragment newInstance(List<StockQuote> data){
        TreeDayChartFragment chart = new TreeDayChartFragment();
        chart.setData(data);
        return chart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_chart, container, false);
        LineChart chart = view.findViewById(R.id.chart);
        chart.setData(ChartUtil.getTreeDayChart(chart, intraDayData));
        chart.animateX(2500);
        chart.invalidate();
        return view;
    }

}
