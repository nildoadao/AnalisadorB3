package br.com.analisadorb3.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;

import br.com.analisadorb3.api.StockRepository;
import br.com.analisadorb3.models.StockRealTimeData;

public class StockInfoViewModel extends AndroidViewModel {

    private StockRealTimeData lastQuote;
    private ObservableBoolean refreshing;
    private StockRepository repository;

    public StockInfoViewModel(@NonNull Application application) {
        super(application);
        repository = StockRepository.getInstance();
    }

}
