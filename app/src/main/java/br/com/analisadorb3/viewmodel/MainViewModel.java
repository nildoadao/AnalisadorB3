package br.com.analisadorb3.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import br.com.analisadorb3.api.StockRepository;
import br.com.analisadorb3.models.YahooStockData;
import br.com.analisadorb3.util.SettingsUtil;

public class MainViewModel extends AndroidViewModel {

    private StockRepository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = StockRepository.getInstance();
    }

    public MutableLiveData<Boolean> isRefreshing(){
        return repository.isRefreshing();
    }

    public MutableLiveData<List<String>> getFavouriteStocks(){
        return repository.getFavouriteStocks();
    }

    public void unfollowStock(YahooStockData stock){
        repository.unfollowStock(getApplication(), stock.getChart().getResult().get(0).getMeta().getSymbol());
    }

    public MutableLiveData<List<YahooStockData>> getSavedStocks(){
        return repository.getSavedStocks();
    }

    public void updateStocks(){
        repository.updateSavedStocks(SettingsUtil.getFavouriteStocks(getApplication()));
    }

    public void setSelectedStock(YahooStockData stock){
        repository.clearStockHistory();
        SettingsUtil.setSelectedSymbol(getApplication(), stock.getChart().getResult().get(0).getMeta().getSymbol());
        repository.updateSelectedStock(stock.getChart().getResult().get(0).getMeta().getSymbol(), "1m", "1d");
    }
}
