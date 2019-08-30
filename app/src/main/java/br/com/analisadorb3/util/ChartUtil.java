package br.com.analisadorb3.util;

import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.analisadorb3.models.StockQuote;

public class ChartUtil {

    public static LineData getDayChart(final LineChart chart, List<StockQuote> data){
        if(data == null)
            return null;

        ArrayList<Entry> entries = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for(int i = data.size() - 1 ; i >= 0; i--){
            StockQuote quote = data.get(i);
            if(quote.getDate().isAfter(currentDate.minusDays(1))){
                float price = Float.parseFloat(quote.getClose());
                entries.add(new Entry(entries.size(), price));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Variação diária");
        dataSet.setColor(Color.argb(255, 0, 0, 127));
        dataSet.setValueTextColor(Color.argb(255,0,0,127));
        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });
        dataSet.setFillColor(Color.argb(255, 183,242,255));
        //****
        // Controlling X axis
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        ValueFormatter formatter = new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                if(value == 0)
                    return "9h";
                else if(value == 60)
                    return "10h";
                else if(value == 120)
                    return "11h";
                else if(value == 180)
                    return "12h";
                else if(value == 240)
                    return "13h";
                else if(value == 300)
                    return "14h";
                else if(value == 360)
                    return "15h";
                else if(value == 420)
                    return "16h";
                else if(value == 480)
                    return "17h";
                else
                    return "";
            }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        //***
        // Controlling right side of y axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        LineData lineData = new LineData(dataSet);
        return lineData;
    }

    public static LineData getTreeDayChart(final LineChart chart, final List<StockQuote> data){
        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for(int i = data.size() - 1 ; i >= 0; i--){
            StockQuote quote = data.get(i);
            if(quote.getDate().isAfter(currentDate.minusDays(3))){
                float price = Float.parseFloat(quote.getClose());
                entries.add(new Entry(entries.size(), price));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Variação 3 dias");
        dataSet.setColor(Color.argb(255, 0, 0, 127));
        dataSet.setValueTextColor(Color.argb(255,0,0,127));
        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });
        dataSet.setFillColor(Color.argb(255, 183,242,255));
        //****
        // Controlling X axis
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        ValueFormatter formatter = new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                if(value == 0)
                    return "9h";
                else if(value == 120)
                    return "11h";
                else if(value == 240)
                    return "13h";
                else if(value == 360)
                    return "15h";
                else if(value == 480)
                    return "17h";
                else if(value == 600)
                    return "10h";
                else if(value == 720)
                    return "12h";
                else if(value == 840)
                    return "14h";
                else if(value == 960)
                    return "15h";
                else if(value == 1080)
                    return "17h";
                else if(value == 1200)
                    return "9h";
                else
                    return "";
            }
        };
        xAxis.setValueFormatter(formatter);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        //***
        // Controlling right side of y axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        LineData lineData = new LineData(dataSet);
        return lineData;
    }

    public static LineData getSixMonthsChart(final LineChart chart, final List<StockQuote> data){
        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for(int i = data.size() - 1 ; i >= 0; i--){
            StockQuote quote = data.get(i);
            if(quote.getDate().isAfter(currentDate.minusMonths(6))){
                float price = Float.parseFloat(quote.getClose());
                entries.add(new Entry(entries.size(), price));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Variação 6 meses");
        dataSet.setColor(Color.argb(255, 0, 0, 127));
        dataSet.setValueTextColor(Color.argb(255,0,0,127));
        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });
        dataSet.setFillColor(Color.argb(255, 183,242,255));
        //****
        // Controlling X axis
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        ValueFormatter formatter = new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                if(value == 0)
                    return data.get(data.size() - 1).getDate().toString();
                else if(value == 24)
                    return data.get(data.size() - 25).getDate().toString();
                else if(value == 48)
                    return data.get(data.size() - 49).getDate().toString();
                else if(value == 72)
                    return data.get(data.size() - 73).getDate().toString();
                else if(value == 96)
                    return data.get(data.size() - 97).getDate().toString();
                else
                    return "";
            }
        };
        xAxis.setValueFormatter(formatter);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        //***
        // Controlling right side of y axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        LineData lineData = new LineData(dataSet);
        return lineData;
    }

    public static LineData getMonthChart(final LineChart chart, final List<StockQuote> data){
        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for(int i = data.size() - 1 ; i >= 0; i--){
            StockQuote quote = data.get(i);
            if(quote.getDate().isAfter(currentDate.minusMonths(1))){
                float price = Float.parseFloat(quote.getClose());
                entries.add(new Entry(entries.size(), price));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Variação 1 mês");
        dataSet.setColor(Color.argb(255, 0, 0, 127));
        dataSet.setValueTextColor(Color.argb(255,0,0,127));
        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });
        dataSet.setFillColor(Color.argb(255, 183,242,255));
        //****
        // Controlling X axis
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        ValueFormatter formatter = new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                if(value == 0)
                    return data.get(data.size() - 1).getDate().toString();
                else if(value == 4)
                    return data.get(data.size() - 5).getDate().toString();
                else if(value == 8)
                    return data.get(data.size() - 9).getDate().toString();
                else if(value == 12)
                    return data.get(data.size() - 13).getDate().toString();
                else if(value == 16)
                    return data.get(data.size() - 17).getDate().toString();
                else
                    return "";
            }
        };
        xAxis.setValueFormatter(formatter);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        //***
        // Controlling right side of y axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        LineData lineData = new LineData(dataSet);
        return lineData;
    }
}