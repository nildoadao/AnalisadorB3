package br.com.analisadorb3.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import br.com.analisadorb3.api.StockRepository;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.util.SettingsUtil;

public class MainViewModel extends AndroidViewModel {

    private StockRepository repository;
    private MutableLiveData<List<StockRealTimeData>> savedStocks;
    private MutableLiveData<Boolean> refreshing = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = StockRepository.getInstance();
        repository.setOnRequestCompletedListener(new StockRepository.OnRequestCompletedListener() {
            @Override
            public void onRequestCompleted() {
                refreshing.setValue(false);
            }
        });
    }

    public MutableLiveData<Boolean> isRefreshing(){
        return refreshing;
    }

    public void unfollowStock(StockRealTimeData stock){
        List<StockRealTimeData> newList = new ArrayList<>();

        for(StockRealTimeData item : savedStocks.getValue()){
            if(!item.getSymbol().equals(stock.getSymbol()))
                newList.add(item);
        }

        savedStocks.setValue(newList);
        SettingsUtil settingsUtil = new SettingsUtil(getApplication().getBaseContext());
        settingsUtil.removeFavouriteStock(stock.getSymbol());

    }

    public MutableLiveData<List<StockRealTimeData>> getSavedStocks(){
        return savedStocks;
    }

    public void init(){
        if(savedStocks == null){
            refreshing.setValue(true);
            SettingsUtil settingsUtil = new SettingsUtil(getApplication().getBaseContext());
            savedStocks = repository.getLastQuote(settingsUtil.getFavouriteStocks());
        }
    }

    public void updateStocks(){
        refreshing.setValue(true);
        SettingsUtil settingsUtil = new SettingsUtil(getApplication().getBaseContext());
        savedStocks.postValue(repository.getLastQuote(settingsUtil.getFavouriteStocks()).getValue());
    }
}
