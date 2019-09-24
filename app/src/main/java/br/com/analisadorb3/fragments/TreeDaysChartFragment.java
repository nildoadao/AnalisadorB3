package br.com.analisadorb3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;

import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.util.ChartUtil;

public class TreeDaysChartFragment extends Fragment {

    private List<StockRealTimeData> intradayData;

    public TreeDaysChartFragment() {
        // Required empty public constructor
    }

    private void setData(List<StockRealTimeData> data){
        intradayData = data;
    }

    public static TreeDaysChartFragment newInstance(List<StockRealTimeData> data){
        TreeDaysChartFragment chart = new TreeDaysChartFragment();
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
        chart.setData(ChartUtil.getTreeDayChart(chart, intradayData));
        chart.animateX(2500);
        chart.invalidate();
        return view;
    }
}
