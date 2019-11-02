package br.com.analisadorb3.util;

import android.graphics.Color;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import br.com.analisadorb3.models.StockHistoricalData;
import br.com.analisadorb3.models.StockIntraDayData;

public class ChartUtil {

    public static LineData getDayChart(final LineChart chart, final Map<String, StockIntraDayData> data){

        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> dataList = new ArrayList<>();

        String lastTradingTimeString = "";

        for(String key : data.keySet()){
            lastTradingTimeString = key;
            break;
        }

        LocalDate lastTradingTime = LocalDate.parse(lastTradingTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        for(String key : data.keySet()){
            StockIntraDayData quote = data.get(key);
            LocalDate quoteDate = LocalDate.parse(key, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if(quoteDate.getDayOfYear() == lastTradingTime.getDayOfYear()){
                float price = Float.parseFloat(quote.getClose());
                entries.add(new Entry(entries.size(), price));
                dataList.add(key);
            }
            else {
                break;
            }
        }

        final ArrayList<Entry> reverseEntries = new ArrayList<>();
        for(int j = entries.size() - 1; j >= 0; j--){
            reverseEntries.add(new Entry(reverseEntries.size(), entries.get(j).getY()));
        }

        LineDataSet dataSet = new LineDataSet(reverseEntries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        if(reverseEntries.get(0).getY() < reverseEntries.get(reverseEntries.size() - 1).getY()){
            dataSet.setColor(Color.argb(255, 0, 127, 0));
            dataSet.setValueTextColor(Color.argb(255, 0, 127, 0));
            dataSet.setFillColor(Color.argb(127, 0,255,0));
        }
        else{
            dataSet.setColor(Color.argb(255, 127, 0, 0));
            dataSet.setValueTextColor(Color.argb(255, 127, 0, 0));
            dataSet.setFillColor(Color.argb(127, 255,0,0));
        }

        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });

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
                    return dataList.get(dataList.size() - 1);
                else if(value == 60)
                    return dataList.get(dataList.size() - 61);
                else if(value == 120)
                    return dataList.get(dataList.size() - 121);
                else if(value == 180)
                    return dataList.get(dataList.size() - 181);
                else if(value == 240)
                    return dataList.get(dataList.size() - 241);
                else if(value == 300)
                    return dataList.get(dataList.size() - 301);
                else if(value == 360)
                    return dataList.get(dataList.size() - 361);
                else if(value == 420)
                    return dataList.get(dataList.size() - 421);
                else if(value == 480)
                    return dataList.get(dataList.size() - 481);
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
        return new LineData(dataSet);
    }

    public static LineData getTreeDayChart(final LineChart chart, final Map<String, StockIntraDayData> data){

        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> dataList = new ArrayList<>();

        String lastTradingTimeString = "";

        for(String key : data.keySet()){
            lastTradingTimeString = key;
            break;
        }

        LocalDate lastTradingTime = LocalDate.parse(lastTradingTimeString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        for(String key : data.keySet()){
            StockIntraDayData quote = data.get(key);
            LocalDate quoteDate = LocalDate.parse(key,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            switch(quoteDate.getDayOfWeek()){
                case MONDAY :
                case TUESDAY:
                    if(quoteDate.isAfter(lastTradingTime.minusDays(4))){
                        float price = Float.parseFloat(quote.getClose());
                        entries.add(new Entry(entries.size(), price));
                        dataList.add(key);
                    }
                    break;
                default:
                    if(quoteDate.isAfter(lastTradingTime.minusDays(2))){
                        float price = Float.parseFloat(quote.getClose());
                        entries.add(new Entry(entries.size(), price));
                        dataList.add(key);
                    }
                    break;
            }

            if(quoteDate.isBefore(lastTradingTime.minusDays(4)))
                break;
        }

        final ArrayList<Entry> reverseEntries = new ArrayList<>();
        for(int j = entries.size() - 1; j >= 0; j--){
            reverseEntries.add(new Entry(reverseEntries.size(), entries.get(j).getY()));
        }

        LineDataSet dataSet = new LineDataSet(reverseEntries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        if(reverseEntries.get(0).getY() < reverseEntries.get(reverseEntries.size() - 1).getY()){
            dataSet.setColor(Color.argb(255, 0, 127, 0));
            dataSet.setValueTextColor(Color.argb(255, 0, 127, 0));
            dataSet.setFillColor(Color.argb(64, 0,255,0));
        }
        else{
            dataSet.setColor(Color.argb(255, 127, 0, 0));
            dataSet.setValueTextColor(Color.argb(255, 127, 0, 0));
            dataSet.setFillColor(Color.argb(64, 255,0,0));
        }

        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });
        // Controlling X axis
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        ValueFormatter formatter = new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                if(value == 0)
                    return dataList.get(dataList.size() - 1);
                else if(value == 120)
                    return dataList.get(dataList.size() - 121);
                else if(value == 240)
                    return dataList.get(dataList.size() - 241);
                else if(value == 360)
                    return dataList.get(dataList.size() - 361);
                else if(value == 480)
                    return dataList.get(dataList.size() - 481);
                else if(value == 600)
                    return dataList.get(dataList.size() - 601);
                else if(value == 720)
                    return dataList.get(dataList.size() - 721);
                else if(value == 840)
                    return dataList.get(dataList.size() - 841);
                else if(value == 960)
                    return dataList.get(dataList.size() - 961);
                else if(value == 1080)
                    return dataList.get(dataList.size() - 1081);
                else if(value == 1200)
                    return dataList.get(dataList.size() - 1201);
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
        return new LineData(dataSet);
    }

    public static LineData getSixMonthsChart(final LineChart chart, final Map<String, StockHistoricalData> data){
        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> dataList = new ArrayList<>();

        for(String key : data.keySet()){
            StockHistoricalData quote = data.get(key);
            LocalDate quoteDate = LocalDate.parse(key,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if(quoteDate.isAfter(LocalDate.now().minusMonths(6))){
                float price = Float.parseFloat(quote.getClose());
                entries.add(new Entry(entries.size(), price));
                dataList.add(key);
            }
        }

        final ArrayList<Entry> reverseEntries = new ArrayList<>();
        for(int j = entries.size() - 1; j >= 0; j--){
            reverseEntries.add(new Entry(reverseEntries.size(), entries.get(j).getY()));
        }

        LineDataSet dataSet = new LineDataSet(reverseEntries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        if(reverseEntries.get(0).getY() < reverseEntries.get(reverseEntries.size() - 1).getY()){
            dataSet.setColor(Color.argb(255, 0, 127, 0));
            dataSet.setValueTextColor(Color.argb(255, 0, 127, 0));
            dataSet.setFillColor(Color.argb(64, 0,255,0));
        }
        else{
            dataSet.setColor(Color.argb(255, 127, 0, 0));
            dataSet.setValueTextColor(Color.argb(255, 127, 0, 0));
            dataSet.setFillColor(Color.argb(64, 255,0,0));
        }

        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        ValueFormatter formatter = new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                if(value == 0)
                    return dataList.get(dataList.size() - 1);
                else if(value == 12)
                    return dataList.get(dataList.size() - 13);
                else if(value == 24)
                    return dataList.get(dataList.size() - 25);
                else if(value == 36)
                    return dataList.get(dataList.size() - 37);
                else if(value == 48)
                    return dataList.get(dataList.size() - 49);
                else if(value == 60)
                    return dataList.get(dataList.size() - 63);
                else if(value == 72)
                    return dataList.get(dataList.size() - 73);
                else if(value == 84)
                    return dataList.get(dataList.size() - 85);
                else if(value == 96)
                    return dataList.get(dataList.size() - 97);
                else if(value == 108)
                    return dataList.get(dataList.size() - 109);
                else if(value == 120)
                    return dataList.get(dataList.size() - 121);
                else
                    return "";
            }
        };
        xAxis.setValueFormatter(formatter);
        //xAxis.setValueFormatter(formatter);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        //***
        // Controlling right side of y axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        return new LineData(dataSet);
    }

    public static LineData getMonthChart(final LineChart chart, final Map<String, StockHistoricalData> data){
        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> dataList = new ArrayList<>();

        for(String key : data.keySet()){
            StockHistoricalData quote = data.get(key);
            LocalDate quoteDate = LocalDate.parse(key,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if(quoteDate.isAfter(LocalDate.now().minusMonths(1))){
                float price = Float.parseFloat(quote.getClose());
                entries.add(new Entry(entries.size(), price));
                dataList.add(key);
            }
        }

        final ArrayList<Entry> reverseEntries = new ArrayList<>();
        for(int j = entries.size() - 1; j >= 0; j--){
            reverseEntries.add(new Entry(reverseEntries.size(), entries.get(j).getY()));
        }

        LineDataSet dataSet = new LineDataSet(reverseEntries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        if(reverseEntries.get(0).getY() < reverseEntries.get(reverseEntries.size() - 1).getY()){
            dataSet.setColor(Color.argb(255, 0, 127, 0));
            dataSet.setValueTextColor(Color.argb(255, 0, 127, 0));
            dataSet.setFillColor(Color.argb(64, 0,255,0));
        }
        else{
            dataSet.setColor(Color.argb(255, 127, 0, 0));
            dataSet.setValueTextColor(Color.argb(255, 127, 0, 0));
            dataSet.setFillColor(Color.argb(64, 255,0,0));
        }

        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return chart.getAxisLeft().getAxisMinimum();
            }
        });
        //****
        // Controlling X axis
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        ValueFormatter formatter = new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                if(value == 0)
                    return dataList.get(dataList.size() - 1);
                else if(value == 4)
                    return dataList.get(dataList.size() - 5);
                else if(value == 8)
                    return dataList.get(dataList.size() - 9);
                else if(value == 12)
                    return dataList.get(dataList.size() - 13);
                else if(value == 16)
                    return dataList.get(dataList.size() - 17);
                else if(value == 20)
                    return dataList.get(dataList.size() - 21);
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
        return new LineData(dataSet);
    }
}
