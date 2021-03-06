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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import br.com.analisadorb3.models.YahooStockData;

public class ChartUtil {

    public static LineData getDayChart(final LineChart chart, final YahooStockData data){

        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> dataList = new ArrayList<>();
        
        Date lastTradingTime = new Date(data.getChart().getResult().get(0).getTimestamp().get(0) * 1000);

        for(int i = 0; i <  data.getChart().getResult().get(0).getTimestamp().size(); i++){
            Long quote = data.getChart().getResult().get(0).getTimestamp().get(i) * 1000;
            Date quoteDate = new Date(quote);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));

            if(quoteDate.getDay() == lastTradingTime.getDay()){

                if(data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(i) == null)
                    continue;

                float price = Float.parseFloat(data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(i).toString());
                entries.add(new Entry(entries.size(), price));
                calendar.setTimeInMillis(quote + calendar.getTimeZone().getRawOffset());
                dataList.add(String.format("%d:%d", calendar.getTime().getHours(), calendar.getTime().getMinutes()));
            }
            else {
                break;
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        if(Float.parseFloat(data.getChart().getResult().get(0).getMeta().getRegularMarketPrice()) >
                Float.parseFloat(data.getChart().getResult().get(0).getMeta().getPreviousClose())){
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
                    return dataList.get(0);
                else if(value == 60)
                    return dataList.get(60);
                else if(value == 120)
                    return dataList.get(120);
                else if(value == 180)
                    return dataList.get(180);
                else if(value == 240)
                    return dataList.get(240);
                else if(value == 300)
                    return dataList.get(300);
                else if(value == 360)
                    return dataList.get(360);
                else if(value == 420)
                    return dataList.get(420);
                else if(value == 480)
                    return dataList.get(480);
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

    public static LineData getTreeDayChart(final LineChart chart, final YahooStockData data){
        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> dataList = new ArrayList<>();

        Date lastTradingTime = new Date(data.getChart().getResult().get(0).getTimestamp().get(0) * 1000);

        for(int i = 0; i <  data.getChart().getResult().get(0).getTimestamp().size(); i++){
            Long quote = data.getChart().getResult().get(0).getTimestamp().get(i) * 1000;
            Date quoteDate = new Date(quote);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));

            if(lastTradingTime.compareTo(quoteDate) >= -3){

                if(data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(i) == null)
                    continue;

                float price = Float.parseFloat(data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(i).toString());
                entries.add(new Entry(entries.size(), price));
                calendar.setTimeInMillis(quote + calendar.getTimeZone().getRawOffset());
                dataList.add(String.format("%d/%d", calendar.getTime().getDay(), calendar.getTime().getMonth()));
            }
            else{
                break;
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        if(Double.parseDouble(data.getChart().getResult().get(0).getMeta().getRegularMarketPrice()) >
                data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(0)){
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
                    return dataList.get(0);
                else if(value == 120)
                    return dataList.get(120);
                else if(value == 240)
                    return dataList.get(240);
                else if(value == 360)
                    return dataList.get(360);
                else if(value == 480)
                    return dataList.get(480);
                else if(value == 600)
                    return dataList.get(600);
                else if(value == 720)
                    return dataList.get(720);
                else if(value == 840)
                    return dataList.get(840);
                else if(value == 960)
                    return dataList.get(960);
                else if(value == 1080)
                    return dataList.get(1080);
                else if(value == 1200)
                    return dataList.get(1200);
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

    public static LineData getSixMonthsChart(final LineChart chart, final YahooStockData data){
        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> dataList = new ArrayList<>();

        Date lastTradingTime = new Date(data.getChart().getResult().get(0).getTimestamp().get(0) * 1000);

        for(int i = 0; i <  data.getChart().getResult().get(0).getTimestamp().size(); i++){
            Long quote = data.getChart().getResult().get(0).getTimestamp().get(i) * 1000;
            Date quoteDate = new Date(quote);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));

            if(lastTradingTime.getTime() - quoteDate.getTime()  < 15778458000L){

                if(data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(i) == null)
                    continue;

                float price = Float.parseFloat(data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(i).toString());
                entries.add(new Entry(entries.size(), price));
                calendar.setTimeInMillis(quote + calendar.getTimeZone().getRawOffset());
                dataList.add(String.format("%d/%d", calendar.getTime().getDay(), calendar.getTime().getMonth()));
            }
            else{
                break;
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        if(Double.parseDouble(data.getChart().getResult().get(0).getMeta().getRegularMarketPrice()) >
                data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(0)){
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
                    return dataList.get(0);
                else if(value == 120)
                    return dataList.get(120);
                else if(value == 240)
                    return dataList.get(240);
                else if(value == 360)
                    return dataList.get(360);
                else if(value == 480)
                    return dataList.get(480);
                else if(value == 600)
                    return dataList.get(600);
                else if(value == 720)
                    return dataList.get(720);
                else if(value == 840)
                    return dataList.get(840);
                else if(value == 960)
                    return dataList.get(960);
                else if(value == 1080)
                    return dataList.get(1080);
                else if(value == 1200)
                    return dataList.get(1200);
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

    public static LineData getMonthChart(final LineChart chart, final YahooStockData data){
        if(data == null)
            return null;

        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> dataList = new ArrayList<>();

        Date lastTradingTime = new Date(data.getChart().getResult().get(0).getTimestamp().get(data.getChart().getResult().get(0).getTimestamp().size() - 1) * 1000);

        for(int i = 0; i <  data.getChart().getResult().get(0).getTimestamp().size(); i++){
            Long quote = data.getChart().getResult().get(0).getTimestamp().get(i) * 1000;
            Date quoteDate = new Date(quote);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));

            if(lastTradingTime.getTime() - quoteDate.getTime()  < 2629743000L){

                if(data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(i) == null)
                    continue;

                float price = Float.parseFloat(data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(i).toString());
                entries.add(new Entry(entries.size(), price));
                calendar.setTimeInMillis(quote + calendar.getTimeZone().getRawOffset());
                dataList.add(String.format("%d/%d", calendar.getTime().getDay(), calendar.getTime().getMonth()));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        if(Double.parseDouble(data.getChart().getResult().get(0).getMeta().getRegularMarketPrice()) >
                data.getChart().getResult().get(0).getIndicators().getQuote().get(0).getClose().get(0)){
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
                    return dataList.get(0);
                else if(value == 120)
                    return dataList.get(120);
                else if(value == 240)
                    return dataList.get(240);
                else if(value == 360)
                    return dataList.get(360);
                else if(value == 480)
                    return dataList.get(480);
                else if(value == 600)
                    return dataList.get(600);
                else if(value == 720)
                    return dataList.get(720);
                else if(value == 840)
                    return dataList.get(840);
                else if(value == 960)
                    return dataList.get(960);
                else if(value == 1080)
                    return dataList.get(1080);
                else if(value == 1200)
                    return dataList.get(1200);
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
