package br.com.analisadorb3.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import br.com.analisadorb3.R;
import br.com.analisadorb3.api.AlphaVantageConnector;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.ApiException;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.models.StockFragment;
import br.com.analisadorb3.models.StockListFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.adaptors.ErrorAdapter;
import br.com.analisadorb3.adaptors.StockInfoAdapter;

public class StockInfoActivity extends AppCompatActivity {

    private StockFragment lastQuote;
    private StockListFragment historyQuote;
    private String errorMessage;
    private StockInfoAdapter stockInfoAdapter;
    private ErrorAdapter errorAdapter;
    private SwipeRefreshLayout refresh;
    private String symbol;
    private String company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info);
        getExtras();
        refresh = findViewById(R.id.stock_info_swipe_refresh);
        FragmentManager manager = getSupportFragmentManager();
        lastQuote = (StockFragment) manager.findFragmentByTag("lastQuote");
        historyQuote = (StockListFragment) manager.findFragmentByTag("historyQuote");

        if(lastQuote == null){
            lastQuote = new StockFragment();
            historyQuote = new StockListFragment();
            lastQuote.setData(new StockQuote());
            historyQuote.setData(new ArrayList<StockQuote>());
            manager.beginTransaction().add(lastQuote, "lastQuote").commit();
            manager.beginTransaction().add(historyQuote, "historyQuote").commit();
            refresh.setRefreshing(true);
            new LoadStockQuote().execute(symbol);
            new LoadStockHistory().execute(symbol);
        }

        TextView companyName = findViewById(R.id.stock_info_company);
        companyName.setText(company);
        stockInfoAdapter = new StockInfoAdapter(this, lastQuote.getData(), historyQuote.getData());
        ListView stockList = findViewById(R.id.stock_info_list);
        stockList.setAdapter(stockInfoAdapter);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!refresh.isRefreshing())
                    refresh.setRefreshing(true);

                new LoadStockHistory().execute(symbol);
            }
        });
    }

    private void getExtras(){
        Intent intent = getIntent();
        symbol = intent.getStringExtra(MainActivity.SYMBOL_MESSAGE);
        company = intent.getStringExtra(MainActivity.COMPANY_MESSAGE);

        if(symbol == null){ // Coming from search
            symbol = intent.getStringExtra(SearchActivity.SYMBOL_MESSAGE);
            company = intent.getStringExtra(SearchActivity.COMPANY_MESSAGE);
        }
    }

    public void onDestroy(){
        super.onDestroy();
        historyQuote.setData(historyQuote.getData());
        lastQuote.setData(lastQuote.getData());
    }

    private void onLoadStockHistoryCompleted(List<StockQuote> list){
        if(list != null){
            historyQuote.getData().clear();
            for(StockQuote quote : list)
                historyQuote.getData().add(quote);

            stockInfoAdapter = new StockInfoAdapter(this, lastQuote.getData(), historyQuote.getData());
            ListView stockList = findViewById(R.id.stock_info_list);
            stockList.setAdapter(stockInfoAdapter);
            refresh.setRefreshing(false);
        }
        else {
            errorAdapter = new ErrorAdapter(this, errorMessage);
            ListView stockList = findViewById(R.id.stock_info_list);
            stockList.setAdapter(errorAdapter);
            refresh.setRefreshing(false);
        }
    }

    private void onLoadStockQuoteCompleted(StockQuote result){
        if(result != null){
            lastQuote.setData(result);
            stockInfoAdapter = new StockInfoAdapter(this, lastQuote.getData(), historyQuote.getData());
            ListView stockList = findViewById(R.id.stock_info_list);
            stockList.setAdapter(stockInfoAdapter);
        }
        else {
            errorAdapter = new ErrorAdapter(this, errorMessage);
            ListView stockList = findViewById(R.id.stock_info_list);
            stockList.setAdapter(errorAdapter);
            refresh.setRefreshing(false);
        }
    }

    public class LoadStockHistory extends AsyncTask<String, Void, List<StockQuote>>{
        @Override
        protected List<StockQuote> doInBackground(String... params) {
            List<StockQuote> list;
            try{
                ApiConnector api = new AlphaVantageConnector(getBaseContext());
                list = api.getDailyTimeSeries(params[0]);
            }
            catch (Exception ex){
                errorMessage = ex.getMessage();
                return null;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<StockQuote> result) {
            super.onPostExecute(result);
            onLoadStockHistoryCompleted(result);
        }
    }

    public class LoadStockQuote extends AsyncTask<String, Void, StockQuote>{

        @Override
        protected StockQuote doInBackground(String... params) {
            try{
                ApiConnector api = new WorldTradingConnector(getBaseContext());
                return api.getLastQuote(params[0]);
            }
            catch (ApiException e){
                errorMessage = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(StockQuote stockQuote) {
            super.onPostExecute(stockQuote);
            onLoadStockQuoteCompleted(stockQuote);
        }
    }
}
