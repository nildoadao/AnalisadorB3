package br.com.analisadorb3.api;

import android.content.Context;
import android.content.res.Resources;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.models.StockQuote;

public class WorldTradingConnector implements ApiConnector {

    private Context context;
    private final String KEY = "rwNhIENB5s0xpmzAOzBmyBBW944f8uoBUHkT7qEwVsKRXrzFRmLqpFDEo8Eq";

    public WorldTradingConnector(Context context){
        this.context = context;
    }

    private URL buildLastQuoteUrl(String symbol){
        String urlString = String.format("https://api.worldtradingdata.com/api/v1/stock?symbol=%s&api_token=%s",
                symbol, KEY);
        try{
            return new URL(urlString);
        }
        catch (Exception ex){
            return  null;
        }
    }

    private URL buildSearchEndpointUrl(String search){
        String urlString = String.format("https://api.worldtradingdata.com/api/v1/stock_search?search_term=%s&search_by" +
                        "symbol,name&limit=10&page=1&api_token=%s", search, KEY);
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
            JSONArray jsonData = json.getJSONArray("data");
            String quoteDate = jsonData.getJSONObject(0).getString("last_trade_time");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            StockQuote data = new StockQuote();
            data.setDate(LocalDate.parse(quoteDate, formatter));
            data.setCompany(jsonData.getJSONObject(0).getString("name"));
            data.setSymbol(jsonData.getJSONObject(0).getString("symbol"));
            data.setCurrency(jsonData.getJSONObject(0).getString("currency"));
            data.setOpen(Double.parseDouble(jsonData.getJSONObject(0).getString("price_open")));
            data.setHigh(Double.parseDouble(jsonData.getJSONObject(0).getString("day_high")));
            data.setLow(Double.parseDouble(jsonData.getJSONObject(0).getString("day_low")));
            data.setPrice(Double.parseDouble(jsonData.getJSONObject(0).getString("price")));
            data.setVolume(Double.parseDouble(jsonData.getJSONObject(0).getString("volume")));
            data.setMarketCapital(Double.parseDouble(jsonData.getJSONObject(0).getString("market_cap")));
            data.setLastTradingDay(LocalDate.parse(quoteDate, formatter));
            data.setPreviousClose(Double.parseDouble(jsonData.getJSONObject(0).getString("close_yesterday")));
            data.setChange(Double.parseDouble(jsonData.getJSONObject(0).getString("day_change")));
            data.setChangePercent(jsonData.getJSONObject(0).getString("change_pct"));
            return data;
        }
        catch (IOException e){
            throw new ApiException(context.getString(R.string.no_internet));
        }
        catch(Exception e){
            throw new ApiException("Fail to communicate with World Trading, "
                    + e.getMessage());
        }
        finally {
            connection.disconnect();
        }
    }

    @Override
    public List<StockQuote> getLastQuote(List<String> symbols) throws ApiException {
        String symbol = String.join(",", symbols);
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
            JSONArray jsonData = json.getJSONArray("data");
            List<StockQuote> stocks = new ArrayList<StockQuote>();

            for(int i = 0; i < jsonData.length(); i++){
                String quoteDate = jsonData.getJSONObject(i).getString("last_trade_time");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                StockQuote data = new StockQuote();
                data.setDate(LocalDate.parse(quoteDate, formatter));
                data.setCompany(jsonData.getJSONObject(i).getString("name"));
                data.setSymbol(jsonData.getJSONObject(i).getString("symbol"));
                data.setCurrency(jsonData.getJSONObject(i).getString("currency"));
                data.setOpen(Double.parseDouble(jsonData.getJSONObject(i).getString("price_open")));
                data.setHigh(Double.parseDouble(jsonData.getJSONObject(i).getString("day_high")));
                data.setLow(Double.parseDouble(jsonData.getJSONObject(i).getString("day_low")));
                data.setPrice(Double.parseDouble(jsonData.getJSONObject(i).getString("price")));
                data.setVolume(Double.parseDouble(jsonData.getJSONObject(i).getString("volume")));
                data.setMarketCapital(Double.parseDouble(jsonData.getJSONObject(i).getString("market_cap")));
                data.setLastTradingDay(LocalDate.parse(quoteDate, formatter));
                data.setPreviousClose(Double.parseDouble(jsonData.getJSONObject(i).getString("close_yesterday")));
                data.setChange(Double.parseDouble(jsonData.getJSONObject(i).getString("day_change")));
                data.setChangePercent(jsonData.getJSONObject(i).getString("change_pct"));
                stocks.add(data);
            }

            return stocks;
        }
        catch (IOException e){
            throw new ApiException(context.getString(R.string.no_internet));
        }
        catch(Exception e){
            throw new ApiException("Fail to communicate with World Trading, "
                    + e.getMessage());
        }
        finally {
            connection.disconnect();
        }
    }

    @Override
    public List<StockQuote> searchEndpoint(String searchTerm) throws ApiException {
        URL url = buildSearchEndpointUrl(searchTerm);

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
            JSONArray jsonData = json.getJSONArray("data");
            List<StockQuote> stocks = new ArrayList<StockQuote>();

            for(int i = 0; i < jsonData.length(); i++){
                StockQuote data = new StockQuote();
                data.setCompany(jsonData.getJSONObject(i).getString("name"));
                data.setSymbol(jsonData.getJSONObject(i).getString("symbol"));
                data.setCurrency(jsonData.getJSONObject(i).getString("currency"));
                data.setPrice(Double.parseDouble(jsonData.getJSONObject(i).getString("price")));
                stocks.add(data);
            }

            return stocks;
        }
        catch (IOException e){
            throw new ApiException(context.getString(R.string.no_internet));
        }
        catch(Exception e){
            throw new ApiException("Fail to communicate with World Trading, "
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
        return null;
    }

    @Override
    public List<StockQuote> getMonthTimeSeries(String symbol) throws ApiException {
        return null;
    }

}
