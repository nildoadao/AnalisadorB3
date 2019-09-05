package br.com.analisadorb3.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.analisadorb3.adapters.StockItemAdapter;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.util.SettingsUtil;
import br.com.analisadorb3.view.StockItemHolder;

public class MainViewModel extends AndroidViewModel {

    private StockItemAdapter stockAdapter;
    private List<StockQuote> favouriteStocks;
    public ObservableBoolean isLoading;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        isLoading = new ObservableBoolean();
        isLoading.set(false);
        stockAdapter = new StockItemAdapter(new ArrayList<StockQuote>());
        favouriteStocks = new ArrayList<>();
    }

    public void fetchData(){
        try{
            isLoading.set(true);
            SettingsUtil settings = new SettingsUtil(getApplication().getApplicationContext());
            new LoadDataAsync().execute(settings.getFavouriteStocks());
        }
        catch (Exception e){
            isLoading.set(false);
            //error handling
        }
    }

    private void onLoadDataCompleted(List<StockQuote> list){
        if(list != null){
            favouriteStocks = list;
            stockAdapter = new StockItemAdapter(favouriteStocks);
        }
        isLoading.set(false);
    }

    public RecyclerView.Adapter<StockItemHolder> getAdapter(){
        return stockAdapter;
    }

    private class LoadDataAsync extends AsyncTask<List<String>, Void, List<StockQuote>>{

        @Override
        protected List<StockQuote> doInBackground(List<String>... params) {
            try{
                ApiConnector api = new WorldTradingConnector(getApplication().getApplicationContext());
                return api.getLastQuote(params[0]);
            }
            catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<StockQuote> stockQuotes) {
            super.onPostExecute(stockQuotes);
            onLoadDataCompleted(stockQuotes);
        }
    }

}
