package br.com.analisadorb3.api;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.analisadorb3.models.HistoricalDataResponse;
import br.com.analisadorb3.models.RealTimeDataResponse;
import br.com.analisadorb3.models.StockHistoricalData;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.models.StockSearchResponse;
import br.com.analisadorb3.models.StockSearchResult;
import br.com.analisadorb3.util.SettingsUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockRepository {

    private final String WORLD_TRADING_TOKEN = "rwNhIENB5s0xpmzAOzBmyBBW944f8uoBUHkT7qEwVsKRXrzFRmLqpFDEo8Eq";

    private static StockRepository stockRepository;
    private WorldTradingApi worldTradingApi;
    private String errorMessage;
    private MutableLiveData<Boolean> refreshing = new MutableLiveData<>();
    private MutableLiveData<List<StockRealTimeData>> lastQuotes = new MutableLiveData<>();
    private MutableLiveData<List<StockSearchResult>> searchResults = new MutableLiveData<>();
    private MutableLiveData<List<String>> favouriteStocks = new MutableLiveData<>();
    private MutableLiveData<StockRealTimeData> selectedStock = new MutableLiveData<>();
    private MutableLiveData<List<Map<String, StockHistoricalData>>> dailyData = new MutableLiveData<>();

    public static StockRepository getInstance(){
        if(stockRepository == null)
            stockRepository = new StockRepository();

        return stockRepository;
    }

    public StockRepository(){
        worldTradingApi = RetrofitService.createService(WorldTradingApi.class, false);
        refreshing.setValue(false);
    }

    public MutableLiveData<List<StockRealTimeData>> getLastQuotes(){
        return lastQuotes;
    }

    public MutableLiveData<List<StockSearchResult>> getSearchResults(){
        return searchResults;
    }

    public MutableLiveData<Boolean> isRefreshing(){
        return refreshing;
    }

    public MutableLiveData<List<String>> getFavouriteStocks(){
        return favouriteStocks;
    }

    public MutableLiveData<List<Map<String, StockHistoricalData>>> getDailyData(){
        return dailyData;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public MutableLiveData<StockRealTimeData> getSelectedStock(){
        return selectedStock;
    }

    public void setSelectedStock(StockRealTimeData stock){
        selectedStock.setValue(stock);
        LocalDate currentDate = LocalDate.now();
        LocalDate initDate = currentDate.minusMonths(6);
        getDailyTimeSeries(stock.getSymbol(), initDate.toString(), currentDate.toString());
    }

    public boolean unfollowStock(Context context, String symbol){
        boolean result = SettingsUtil.removeFavouriteStock(context, symbol);
        List<StockRealTimeData> newList = new ArrayList<>();

        for(StockRealTimeData item : lastQuotes.getValue()){
            if(!item.getSymbol().equals(symbol))
                newList.add(item);
        }

        lastQuotes.postValue(newList);
        favouriteStocks.postValue(SettingsUtil.getFavouriteStocks(context));
        return result;
    }

    public boolean followStock(Context context, String symbol){
        boolean result = SettingsUtil.saveFavouriteStock(context, symbol);
        getLastQuote(SettingsUtil.getFavouriteStocks(context));
        favouriteStocks.postValue(SettingsUtil.getFavouriteStocks(context));
        return result;
    }

    public MutableLiveData<List<StockRealTimeData>> getLastQuote(List<String> symbols){
        refreshing.postValue(true);
        worldTradingApi.getLastQuote(TextUtils.join(",", symbols), WORLD_TRADING_TOKEN)
                .enqueue(new Callback<RealTimeDataResponse>() {
                    @Override
                    public void onResponse(Call<RealTimeDataResponse> call, Response<RealTimeDataResponse> response) {
                        if(!response.isSuccessful()){
                            errorMessage = "Status code" + response.code();
                            lastQuotes.postValue(null);
                            refreshing.postValue(false);
                            return;
                        }
                        lastQuotes.postValue(response.body().getData());
                        refreshing.postValue(false);
                    }

                    @Override
                    public void onFailure(Call<RealTimeDataResponse> call, Throwable t) {
                        errorMessage = t.getMessage();
                        lastQuotes.postValue(null);
                        refreshing.postValue(false);
                    }
                });
        return lastQuotes;
    }

    public MutableLiveData<List<Map<String, StockHistoricalData>>> getDailyTimeSeries(String symbol, String dateFrom, String dateTo){
        refreshing.postValue(true);
        worldTradingApi.getDailyTimeSeries(symbol, WORLD_TRADING_TOKEN, dateFrom, dateTo)
                .enqueue(new Callback<HistoricalDataResponse>() {
                    @Override
                    public void onResponse(Call<HistoricalDataResponse> call, Response<HistoricalDataResponse> response) {
                        if(!response.isSuccessful()){
                            dailyData.setValue(null);
                            refreshing.postValue(false);
                            return;
                        }
                        dailyData.setValue(response.body().getHistory());
                        refreshing.postValue(false);
                    }

                    @Override
                    public void onFailure(Call<HistoricalDataResponse> call, Throwable t) {
                        dailyData.setValue(null);
                        refreshing.postValue(false);
                    }
                });
        return dailyData;
    }

    public MutableLiveData<List<StockSearchResult>> searchStock(String searchTerm){
        refreshing.postValue(true);
        worldTradingApi.searchStock(searchTerm, WORLD_TRADING_TOKEN)
                .enqueue(new Callback<StockSearchResponse>() {
                    @Override
                    public void onResponse(Call<StockSearchResponse> call, Response<StockSearchResponse> response) {
                        if(!response.isSuccessful()){
                            searchResults.postValue(null);
                            refreshing.postValue(false);
                            return;
                        }
                        searchResults.postValue(response.body().getData());
                        refreshing.postValue(false);
                    }

                    @Override
                    public void onFailure(Call<StockSearchResponse> call, Throwable t) {
                        searchResults.postValue(null);
                        refreshing.postValue(false);
                    }
                });
        return searchResults;
    }
}
