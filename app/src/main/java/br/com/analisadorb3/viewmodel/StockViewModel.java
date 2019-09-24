package br.com.analisadorb3.viewmodel;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import br.com.analisadorb3.api.StockRepository;
import br.com.analisadorb3.logic.StockCalculator;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.models.StockSearchResult;

public class StockViewModel extends AndroidViewModel {

    private StockRepository repository;

    public StockViewModel(@NonNull Application application) {
        super(application);
        repository = StockRepository.getInstance();
    }

    public MutableLiveData<Boolean> isSearching(){
        return repository.isRefreshing();
    }

    public MutableLiveData<List<StockSearchResult>> getSearchResult(){
        return repository.getSearchResults();
    }

    public MutableLiveData<List<String>> getFavouriteStocks(){
        return repository.getFavouriteStocks();
    }

    public MutableLiveData<StockRealTimeData> getSelectedStock(){
        return repository.getSelectedStock();
    }

    public boolean followStock(Context context, String symbol){
        return repository.followStock(context, symbol);
    }

    public boolean unfollowStock(Context context, String symbol){
        return repository.unfollowStock(context, symbol);
    }

    public void setSelectedStock(StockRealTimeData stock){
        repository.setSelectedStock(stock);
    }

    public void search(String searchTerm){
        repository.searchStock(searchTerm);
    }

    public String getPrice(){
        StockRealTimeData selectedStock = repository.getSelectedStock().getValue();
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
            volume = Double.parseDouble(repository.getSelectedStock().getValue().getVolume());
        }
        catch (Exception e){
            volume = 0d;
        }
        return StockCalculator.doublePrettify(volume);
    }

    public String getChangeText(){
        StockRealTimeData selectedStock = repository.getSelectedStock().getValue();
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
            dayChange = Double.parseDouble(repository.getSelectedStock().getValue().getDayChange());
        }
        catch (Exception e){
            dayChange = 0d;
        }

        if(dayChange >= 0)
            return Color.GREEN;
        else
            return Color.RED;
    }
}
