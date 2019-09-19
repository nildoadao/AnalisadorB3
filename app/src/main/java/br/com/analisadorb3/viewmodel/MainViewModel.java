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
        return repository.getLastQuotes();
    }

    public void updateStocks(Context context){
        repository.getLastQuote(SettingsUtil.getFavouriteStocks(context));
    }

    public void setSelectedStock(StockRealTimeData stock){
        repository.setSelectedStock(stock);
    }
}
