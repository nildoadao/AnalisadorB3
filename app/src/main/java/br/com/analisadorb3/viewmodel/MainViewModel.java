package br.com.analisadorb3.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import br.com.analisadorb3.api.StockRepository;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.util.SettingsUtil;

public class MainViewModel extends AndroidViewModel {

    private StockRepository repository;
    private MutableLiveData<List<StockRealTimeData>> savedStocks = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = StockRepository.getInstance();
    }

    public MutableLiveData<Boolean> isRefreshing(){
        return repository.isRefreshing();
    }

    public void unfollowStock(StockRealTimeData stock){
        repository.unfollowStock(getApplication(), stock.getSymbol());
    }

    public MutableLiveData<List<StockRealTimeData>> getSavedStocks(){
        return repository.getSavedStocks();
    }

    public void updateStocks(){
        repository.getLastQuote(SettingsUtil.getFavouriteStocks(getApplication()));
    }

    public void setSelectedStock(StockRealTimeData stock){
        SettingsUtil.setSelectedSymbol(getApplication(), stock.getSymbol());
    }
}
