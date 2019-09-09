package br.com.analisadorb3.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.analisadorb3.adapters.StockItemAdapter;
import br.com.analisadorb3.api.StockRepository;
import br.com.analisadorb3.models.StockRealTimeData;
import br.com.analisadorb3.util.SettingsUtil;

public class MainViewModel extends AndroidViewModel {

    private StockRepository repository;
    private MutableLiveData<List<StockRealTimeData>> savedStocks;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = StockRepository.getInstance();
    }

    public MutableLiveData<List<StockRealTimeData>> getSavedStocks(){
        return savedStocks;
    }

    public void init(){
        if(savedStocks == null){
            SettingsUtil settingsUtil = new SettingsUtil(getApplication().getBaseContext());
            savedStocks = repository.getLastQuote(settingsUtil.getFavouriteStocks());
        }
    }

    public void updateStocks(){
        SettingsUtil settingsUtil = new SettingsUtil(getApplication().getBaseContext());
        savedStocks = repository.getLastQuote(settingsUtil.getFavouriteStocks());
    }

}
