package br.com.analisadorb3.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import br.com.analisadorb3.R;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.ApiException;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.models.StockFragment;
import br.com.analisadorb3.models.StockListFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.adaptors.ErrorAdapter;
import br.com.analisadorb3.adaptors.StockInfoAdapter;

public class StockInfoActivity extends AppCompatActivity {

    private StockFragment lastQuoteFragment;
    private StockListFragment historyQuoteFragment;
    private StockInfoAdapter stockInfoAdapter;
    private String symbol;
    private String company;

    private String getSymbol(){
        return symbol;
    }

    private void setSymbol(String symbol){
        this.symbol = symbol;
    }

    private String getCompany(){
        return company;
    }

    private void setCompany(String company){
        this.company = company;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_info);
        getExtras();
        initStockQuoteFragments();
        initCompanyTextView();
        initStockInfoAdapter();
        initBackButton();
        initRefreshLayout();

    }

    private void initRefreshLayout(){
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.stock_info_swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(true);

                new LoadStockData().execute(getSymbol());
            }
        });
    }

    private void initBackButton(){
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initStockInfoAdapter(){
        stockInfoAdapter = new StockInfoAdapter(this, lastQuoteFragment.getData(), historyQuoteFragment.getData());
        ListView stockList = findViewById(R.id.stock_info_list);
        stockList.setAdapter(stockInfoAdapter);
    }

    private void initCompanyTextView(){
        TextView companyName = findViewById(R.id.stock_info_company);
        companyName.setText(getCompany());
    }

    private void initStockQuoteFragments(){
        FragmentManager manager = getSupportFragmentManager();
        lastQuoteFragment = (StockFragment) manager.findFragmentByTag("lastQuote");
        historyQuoteFragment = (StockListFragment) manager.findFragmentByTag("historyQuote");

        if(lastQuoteFragment == null || historyQuoteFragment == null){
            lastQuoteFragment = new StockFragment();
            lastQuoteFragment.setData(new StockQuote());
            historyQuoteFragment = new StockListFragment();
            historyQuoteFragment.setData(new ArrayList<StockQuote>());
            manager.beginTransaction().add(lastQuoteFragment, "lastQuote").commit();
            manager.beginTransaction().add(historyQuoteFragment, "historyQuote").commit();
            SwipeRefreshLayout refreshLayout = findViewById(R.id.stock_info_swipe_refresh);
            refreshLayout.setRefreshing(true);
            new LoadStockData().execute(getSymbol());
        }
    }

    private void getExtras(){
        Intent intent = getIntent();
        setSymbol(intent.getStringExtra(MainActivity.SYMBOL_MESSAGE));
        setCompany(intent.getStringExtra(MainActivity.COMPANY_MESSAGE));

        if(symbol == null){ // Coming from search
            setSymbol(intent.getStringExtra(SearchActivity.SYMBOL_MESSAGE));
            setCompany(intent.getStringExtra(SearchActivity.COMPANY_MESSAGE));
        }
    }

    public void onDestroy(){
        super.onDestroy();
        historyQuoteFragment.setData(historyQuoteFragment.getData());
        lastQuoteFragment.setData(lastQuoteFragment.getData());
    }

    private void setErrorAdapter(String message){
        ErrorAdapter errorAdapter = new ErrorAdapter(this, message);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.stock_info_swipe_refresh);
        ListView stockList = findViewById(R.id.stock_info_list);
        stockList.setAdapter(errorAdapter);
        refreshLayout.setRefreshing(false);
    }

    private void onLoadStockDataCompleted(StockQuote stock, List<StockQuote> list){

        SwipeRefreshLayout refreshLayout = findViewById(R.id.stock_info_swipe_refresh);
        historyQuoteFragment.getData().clear();

        for(StockQuote quote : list)
            historyQuoteFragment.getData().add(quote);

        lastQuoteFragment.setData(stock);
        stockInfoAdapter = new StockInfoAdapter(this, lastQuoteFragment.getData(), historyQuoteFragment.getData());
        ListView stockList = findViewById(R.id.stock_info_list);
        stockList.setAdapter(stockInfoAdapter);
        refreshLayout.setRefreshing(false);
    }

    public class LoadStockData extends AsyncTask<String, Void, Void>{

        private StockQuote stock;
        private List<StockQuote> stockHistory;
        private String errorMessage;

        private String getErrorMessage(){
            return errorMessage;
        }

        private void setErrorMessage(String message){
            errorMessage = message;
        }
        @Override
        protected Void doInBackground(String... params) {
            try{
                ApiConnector api = new WorldTradingConnector(getBaseContext());
                stock = api.getLastQuote(params[0]);
                stockHistory = api.getDailyTimeSeries(params[0]);
            }
            catch (ApiException e){
                setErrorMessage(e.getMessage());
                stock = null;
                stockHistory = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(stockHistory == null || stock == null)
                setErrorAdapter(getErrorMessage());
            else
                onLoadStockDataCompleted(stock, stockHistory);
        }
    }
}
