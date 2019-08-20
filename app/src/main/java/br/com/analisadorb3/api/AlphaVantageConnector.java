package br.com.analisadorb3.api;

import android.content.Context;
import android.content.res.Resources;
import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.StockQuote;

public class AlphaVantageConnector implements ApiConnector {

    private Context context;
    private final String KEY= "XC5MVLREL74KNLOR";

    public AlphaVantageConnector(Context context){
        this.context = context;
    }

    private URL buildLastQuoteUrl(String symbol){
        String urlString = String.format("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                symbol, KEY);
        try{
            return new URL(urlString);
        }
        catch (Exception ex){
            return  null;
        }
    }

    private URL buildDailySeriesUrl(String symbol){
        String urlString = String.format("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                symbol, KEY);
        try{
            return new URL(urlString);
        }
        catch (Exception ex){
            return  null;
        }
    }

    private HttpURLConnection buildUrlConnection(URL url){
        try{
            return (HttpURLConnection) url.openConnection();
        }
        catch (Exception e){
            return null;
        }
    }

    @Override
    public StockQuote getLastQuote(String symbol) throws ApiException {
        URL url = buildLastQuoteUrl(symbol);

        if(url == null)
            throw new ApiException("Bad Url");

        HttpURLConnection connection = buildUrlConnection(url);

        if(connection == null)
            throw new ApiException("Bad Url");

        try {

            int responseCode = connection.getResponseCode();

            if(responseCode != HttpURLConnection.HTTP_OK)
                throw new ApiException("Fail to get Stock data, code "
                    + responseCode);

            String inputLine;
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer response = new StringBuffer();
            while ((inputLine = input.readLine()) != null){
                response.append(inputLine);
            }

            JSONObject json = new JSONObject(response.toString());
            StockQuote data = new StockQuote();
            JSONObject globalQuote = json.getJSONObject("Global Quote");
            String quoteDate = globalQuote.getString("07. latest trading day");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            data.setDate(LocalDate.parse(quoteDate, formatter));
            data.setSymbol(globalQuote.getString("01. symbol"));
            data.setOpen(Double.parseDouble(globalQuote.getString("02. open")));
            data.setHigh(Double.parseDouble(globalQuote.getString("03. high")));
            data.setLow(Double.parseDouble(globalQuote.getString("04. low")));
            data.setPrice(Double.parseDouble(globalQuote.getString("05. price")));
            data.setVolume(Double.parseDouble(globalQuote.getString("06. volume")));
            data.setLastTradingDay(LocalDate.parse(quoteDate, formatter));
            data.setPreviousClose(Double.parseDouble(globalQuote.getString("08. previous close")));
            data.setChange(Double.parseDouble(globalQuote.getString("09. change")));
            data.setChangePercent(globalQuote.getString("10. change percent"));
            return data;
        }
        catch (IOException e){
            throw new ApiException(context.getString(R.string.no_internet));
        }
        catch (JSONException e){
            throw new ApiException(context.getString(R.string.alpha_vantage_request_per_minute_exceeded));
        }
        catch(Exception e){
            throw new ApiException("Fail to communicate with AlphaVantage, "
                    + e.getMessage());
        }
        finally {
            connection.disconnect();
        }
    }

    @Override
    public List<StockQuote> getIntradayTimeSeries(String symbol) throws ApiException {
        return null;
    }

    @Override
    public List<StockQuote> getDailyTimeSeries(String symbol) throws ApiException {
        URL url = buildDailySeriesUrl(symbol);

        if(url == null)
            throw new ApiException("Bad Url");

        HttpURLConnection connection = buildUrlConnection(url);

        if(connection == null)
            throw new ApiException("Bad Url");

        try {

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK)
                throw new ApiException("Fail to get Stock data, code "
                        + responseCode);

            String inputLine;
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer response = new StringBuffer();
            while ((inputLine = input.readLine()) != null) {
                response.append(inputLine);
            }
            JSONObject json = new JSONObject(response.toString());
            JSONObject stockData = json.getJSONObject("Time Series (Daily)");
            Iterator<String> iterator = stockData.keys();
            List<StockQuote> stockList = new ArrayList<>();

            while(iterator.hasNext()) {
                StockQuote stock = new StockQuote();
                String key = iterator.next();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                stock.setDate(LocalDate.parse(key, formatter));
                stock.setOpen(Double.parseDouble(stockData.getJSONObject(key).getString("1. open")));
                stock.setHigh(Double.parseDouble(stockData.getJSONObject(key).getString("2. high")));
                stock.setLow(Double.parseDouble(stockData.getJSONObject(key).getString("3. low")));
                stock.setClose(Double.parseDouble(stockData.getJSONObject(key).getString("4. close")));
                stock.setVolume(Double.parseDouble(stockData.getJSONObject(key).getString("5. volume")));
                stockList.add(stock);
            }
            return stockList;
        }
        catch (IOException e){
            throw new ApiException(context.getString(R.string.no_internet));
        }
        catch (JSONException e){
            throw new ApiException(context.getString(R.string.alpha_vantage_request_per_minute_exceeded));
        }
        catch(Exception e){
            throw new ApiException("Fail to communicate with AlphaVantage, "
                    + e.getMessage());
        }
        finally {
            connection.disconnect();
        }
    }

    @Override
    public List<StockQuote> getMonthTimeSeries(String symbol) throws ApiException {
        return null;
    }

    @Override
    public List<StockQuote> getLastQuote(List<String> symbols) throws ApiException {
        return null;
    }

    @Override
    public List<StockQuote> searchEndpoint(String searchTerm) throws ApiException {
        return null;
    }
}
