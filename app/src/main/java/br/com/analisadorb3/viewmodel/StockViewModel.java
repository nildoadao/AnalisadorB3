package br.com.analisadorb3.viewmodel;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import br.com.analisadorb3.api.StockRepository;
import br.com.analisadorb3.models.StockHistoricalData;
import br.com.analisadorb3.models.StockIntraDayData;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.models.StockSearchResult;
import br.com.analisadorb3.util.SettingsUtil;
import br.com.analisadorb3.util.StockChangeStatus;
import br.com.analisadorb3.util.StockUtil;

public class StockViewModel extends AndroidViewModel {

    private StockRepository repository;
    private MutableLiveData<String> stockSymbol = new MutableLiveData<>();
    private MutableLiveData<String> stockCompany = new MutableLiveData<>();
    private MutableLiveData<String> stockPrice = new MutableLiveData<>();
    private MutableLiveData<Boolean> refreshing = new MutableLiveData<>();
    private MutableLiveData<List<StockRealTimeData>> lastQuotes = new MutableLiveData<>();
    private MutableLiveData<List<StockSearchResult>> searchResults = new MutableLiveData<>();
    private MutableLiveData<List<String>> favouriteStocks = new MutableLiveData<>();
    private MutableLiveData<StockRealTimeData> selectedStock = new MutableLiveData<>();
    private MutableLiveData<Map<String, StockHistoricalData>> dailyData = new MutableLiveData<>();
    private MutableLiveData<Map<String, StockIntraDayData>> intraDayData = new MutableLiveData<>();

    public StockViewModel(@NonNull Application application) {
        super(application);
        repository = StockRepository.getInstance();
    }

    public MutableLiveData<Boolean> isSearching(){
        return repository.isRefreshing();
    }

    public MutableLiveData<Map<String, StockIntraDayData>> getIntraDayData(){
        return intraDayData;
    }

    public MutableLiveData<Map<String, StockHistoricalData>> getDailyData() {
        return dailyData;
    }

    public MutableLiveData<List<StockSearchResult>> getSearchResults(){
        return searchResults;
    }

    public MutableLiveData<List<String>> getFavouriteStocks(){
        return favouriteStocks;
    }

    public MutableLiveData<StockRealTimeData> getSelectedStock(){
        return selectedStock;
    }

    public MutableLiveData<String> getStockSymbol() {
        return stockSymbol;
    }

    public MutableLiveData<String> getStockCompany() {
        return stockCompany;
    }

    public MutableLiveData<String> getStockPrice() {
        return stockPrice;
    }


    public boolean followStock(Context context, String symbol){
        return repository.followStock(context, symbol);
    }

    public boolean unfollowStock(Context context, String symbol){
        return repository.unfollowStock(context, symbol);
    }

    public void setSelectedStock(StockRealTimeData stock){
        selectedStock.postValue(stock);
    }

    public void setSelectedStock(String symbol){
        List<StockRealTimeData> results = repository.getLastQuote(symbol);
        if(results.size() > 0)
            setSelectedStock(results.get(0));
    }

    public void search(String searchTerm){
        searchResults.postValue(repository.searchStock(searchTerm));
    }

    public void fetchData(){
        String selectedSymbol = SettingsUtil.getSelectedSymbol(getApplication());
        setSelectedStock(selectedSymbol);
        lastQuotes.postValue(repository.getLastQuote(selectedSymbol));
        LocalDate today = LocalDate.now();
        dailyData.postValue(repository.getDailyTimeSeries(selectedSymbol,
                today.minusMonths(6).toString(), today.toString()));
        intraDayData.postValue(repository.getIntraDayTimeSeries(selectedSymbol));
    }

    public String getPrice(){
        StockRealTimeData selectedStock = getSelectedStock().getValue();
        if(selectedStock != null){
            String price = selectedStock.getPrice();
            String currency = selectedStock.getCurrency();
            return String.format("%s %s", price, currency);
        }
        return "";
    }

    public String getVolume(){
        Double volume;
        try {
            volume = Double.parseDouble(getSelectedStock().getValue().getVolume());
        }
        catch (Exception e){
            volume = 0d;
        }
        return StockUtil.volumePrettify(volume);
    }

    public String getChangeText(){
        StockRealTimeData selectedStock = getSelectedStock().getValue();
        if(selectedStock != null){
            String valueChange = selectedStock.getDayChange();
            String changePercent = selectedStock.getChangePercent();
            return String.format("%s (%s", valueChange, changePercent)
                    + "%)";
        }
        return "";
    }

    public int getChangeTextColor(){

        Double dayChange;
        try {
            dayChange = Double.parseDouble(getSelectedStock().getValue().getDayChange());
        }
        catch (Exception e){
            dayChange = 0d;
        }

        if(dayChange >= 0)
            return Color.GREEN;
        else
            return Color.RED;
    }

    public String getThreeMonthsVariation(){
        Double averageChange = StockUtil.getAverageVariation(getDailyData().getValue(), 6);
        return String.format("%.2f", averageChange);
    }

    public StockChangeStatus getStockStatus(){
        StockRealTimeData stock = selectedStock.getValue();

        if(stock != null){
            double dayChange;
            try {
                dayChange = Double.parseDouble(stock.getDayChange());
            }
            catch (Exception e){
                dayChange = 0d;
            }

            if(dayChange >= 0)
                return StockChangeStatus.VALUE_UP;

            else
                return StockChangeStatus.VALUE_DOWN;
        }
        else
            return StockChangeStatus.VALUE_DOWN;
    }

}
