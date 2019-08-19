package br.com.analisadorb3.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import br.com.analisadorb3.R;
import br.com.analisadorb3.api.AlphaVantageConnector;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.models.StockFragment;
import br.com.analisadorb3.models.StockListFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.ui.Adaptors.ErrorAdapter;
import br.com.analisadorb3.ui.Adaptors.StockInfoAdapter;

public class StockInfoActivity extends AppCompatActivity {

    private StockFragment lastQuote;
    private StockListFragment historyQuote;
    private String errorMessage;
    private StockQuote stock;
    private StockInfoAdapter stockInfoAdapter;
    private ErrorAdapter errorAdapter;
    private SwipeRefreshLayout refresh;

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
            lastQuote.setData(stock);
            manager.beginTransaction().add(lastQuote, "lastQuote").commit();
        }

        if(historyQuote == null){
            historyQuote = new StockListFragment();
            historyQuote.setData(new ArrayList<StockQuote>());
            manager.beginTransaction().add(historyQuote, "historyQuote").commit();
            refresh.setRefreshing(true);
            new LoadStockHistory().execute(stock.getSymbol());
        }

        TextView companyName = findViewById(R.id.stock_info_company);
        companyName.setText(stock.getCompany());

        stockInfoAdapter = new StockInfoAdapter(this, stock, historyQuote.getData());

        GridView stockGrid = findViewById(R.id.stock_info_grid);
        stockGrid.setAdapter(stockInfoAdapter);

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

                new LoadStockHistory().execute(stock.getSymbol());
            }
        });
    }

    private void getExtras(){
        Intent intent = getIntent();
        String symbol = intent.getStringExtra(MainActivity.SYMBOL_MESSAGE);
        String company = intent.getStringExtra(MainActivity.COMPANY_MESSAGE);
        double price = intent.getDoubleExtra(MainActivity.PRICE_MESSAGE, 0);
        String currency = intent.getStringExtra(MainActivity.CURRENCY_MESSAGE);
        double change = intent.getDoubleExtra(MainActivity.CHANGE_MESSAGE, 0);
        String changePercent = intent.getStringExtra(MainActivity.CHANGE_PERCENT_MESSAGE);
        double open = intent.getDoubleExtra(MainActivity.OPEN_MESSAGE, 0);
        double previousClose = intent.getDoubleExtra(MainActivity.PREVIOUS_CLOSE_MESSAGE, 0);
        double volume = intent.getDoubleExtra(MainActivity.VOLUME_MESSAGE, 0);
        double dayHigh = intent.getDoubleExtra(MainActivity.DAY_HIGH_MESSAGE, 0);
        double dayLow = intent.getDoubleExtra(MainActivity.DAY_LOW_MESSAGE, 0);
        stock = new StockQuote();
        stock.setSymbol(symbol);
        stock.setCompany(company);
        stock.setPrice(price);
        stock.setCurrency(currency);
        stock.setChange(change);
        stock.setChangePercent(changePercent);
        stock.setOpen(open);
        stock.setPreviousClose(previousClose);
        stock.setVolume(volume);
        stock.setHigh(dayHigh);
        stock.setLow(dayLow);
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

            GridView stockGrid = findViewById(R.id.stock_info_grid);
            stockGrid.setAdapter(stockInfoAdapter);
            stockInfoAdapter.notifyDataSetChanged();
            refresh.setRefreshing(false);
        }
        else {
            errorAdapter = new ErrorAdapter(this, errorMessage);
            GridView stock_grid = findViewById(R.id.stock_info_grid);
            stock_grid.setAdapter(errorAdapter);
            refresh.setRefreshing(false);
        }
    }

    public class LoadStockHistory extends AsyncTask<String, Void, List<StockQuote>>{
        @Override
        protected List<StockQuote> doInBackground(String... params) {
            List<StockQuote> list;
            try{
                ApiConnector api = new AlphaVantageConnector();
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
}
