package br.com.analisadorb3.viewmodel;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    private ObservableField<String> stockSymbol = new ObservableField<>();
    private ObservableField<String> stockCompany = new ObservableField<>();
    private ObservableField<String> stockPrice = new ObservableField<>();
    private ObservableField<String> lastClose = new ObservableField<>();
    private ObservableField<String> open = new ObservableField<>();
    private ObservableField<String> low = new ObservableField<>();
    private ObservableField<String> high = new ObservableField<>();
    private ObservableField<String> volume = new ObservableField<>();
    private ObservableField<String> threeMonthsAverageChange = new ObservableField<>();

    public StockViewModel(@NonNull Application application) {
        super(application);
        repository = StockRepository.getInstance();
    }

    public MutableLiveData<Boolean> isSearching(){
        return repository.isRefreshing();
    }

    public MutableLiveData<Map<String, StockIntraDayData>> getIntraDayData(){
        return repository.getIntraDayData();
    }

    public MutableLiveData<Map<String, StockHistoricalData>> getDailyData() {
        return repository.getDailyData();
    }

    public MutableLiveData<List<StockSearchResult>> getSearchResults(){
        return repository.getSearchResults();
    }

    public MutableLiveData<List<String>> getFavouriteStocks(){
        return repository.getFavouriteStocks();
    }

    public MutableLiveData<StockRealTimeData> getSelectedStock(){
        return repository.getSelectedStock();
    }

    public ObservableField<String> getLastClose() {
        return lastClose;
    }

    public ObservableField<String> getOpen() {
        return open;
    }

    public ObservableField<String> getLow() {
        return low;
    }

    public ObservableField<String> getHigh() {
        return high;
    }

    public ObservableField<String> getVolume(){
        return volume;
    }

    public ObservableField<String> getThreeMonthsAverageChange() {
        return threeMonthsAverageChange;
    }

    public ObservableField<String> getStockSymbol() {
        return stockSymbol;
    }

    public ObservableField<String> getStockCompany() {
        return stockCompany;
    }

    public ObservableField<String> getStockPrice() {
        return stockPrice;
    }

    public boolean followStock(Context context, String symbol){
        return repository.followStock(context, symbol);
    }

    public boolean unfollowStock(Context context, String symbol){
        return repository.unfollowStock(context, symbol);
    }

    public void setSelectedStock(String symbol){
        SettingsUtil.setSelectedSymbol(getApplication(), symbol);
        repository.loadSelectedStock(symbol);
    }

    public void search(String searchTerm){
        repository.searchStock(searchTerm);
    }

    public void updateView(){
        stockSymbol.set(getSelectedStock().getValue().getSymbol());
        stockCompany.set(getSelectedStock().getValue().getName());
        stockPrice.set(getPrice());
        lastClose.set(getSelectedStock().getValue().getCloseYesterday());
        open.set(getSelectedStock().getValue().getPriceOpen());
        low.set(getSelectedStock().getValue().getDayLow());
        high.set(getSelectedStock().getValue().getDayHigh());
        volume.set(getVolumeText());
        threeMonthsAverageChange.set(getThreeMonthsVariation());
    }

    public void fetchData(){
        String selectedSymbol = SettingsUtil.getSelectedSymbol(getApplication());
        repository.loadSelectedStock(selectedSymbol);
        repository.getLastQuote(selectedSymbol);
        LocalDate today = LocalDate.now();
        repository.getDailyTimeSeries(selectedSymbol,
                today.minusMonths(6).toString(), today.toString());
        repository.getIntraDayTimeSeries(selectedSymbol);
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

    private String getVolumeText(){
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
        StockRealTimeData stock = getSelectedStock().getValue();

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
