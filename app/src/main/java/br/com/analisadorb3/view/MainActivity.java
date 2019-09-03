package br.com.analisadorb3.view;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import br.com.analisadorb3.R;
import br.com.analisadorb3.adapters.EmptyWalletAdapter;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.fragments.StockListFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.adapters.ErrorAdapter;
import br.com.analisadorb3.adapters.StockAdapter;
import br.com.analisadorb3.util.SettingsUtil;

public class MainActivity extends AppCompatActivity {

    public static final String SYMBOL_MESSAGE = "br.com.analisadorb3.SYMBOL";
    public static final String COMPANY_MESSAGE = "br.com.analisadorb3.COMPANY";

    private StockListFragment stocksFragment;
    private StockAdapter stockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initStocksFragment();
        initStockAdapter();
        initSearchButton();
        initStockList();
        initRefreshLayout();
    }

    private void initStocksFragment(){
        FragmentManager manager = getSupportFragmentManager();
        stocksFragment = (StockListFragment) manager.findFragmentByTag("stockListFragment");

        if(stocksFragment == null){
            stocksFragment = new StockListFragment();
            stocksFragment.setData(new ArrayList<StockQuote>());
            manager.beginTransaction().add(stocksFragment, "stockListFragment").commit();
            if(getFavouriteSymbols().size() > 0){
                SwipeRefreshLayout refreshLayout = findViewById(R.id.pullToRefresh);
                refreshLayout.setRefreshing(true);
                new LoadStockTask().execute(getFavouriteSymbols());
            }
        }
    }

    private void initStockList(){
        if(getFavouriteSymbols().size() == 0)
            setEmptyWalletAdapter();
        else {
            ListView list = findViewById(R.id.stock_list);
            list.setAdapter(stockAdapter);
        }
    }

    private void initSearchButton(){
        ImageButton search = findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initRefreshLayout(){
        final SwipeRefreshLayout refreshLayout = findViewById(R.id.pullToRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!refreshLayout.isRefreshing())
                    refreshLayout.setRefreshing(true);

                new LoadStockTask().execute(getFavouriteSymbols());
            }
        });
    }

    private void initStockAdapter(){
        stockAdapter = new StockAdapter(this, stocksFragment.getData());
        stockAdapter.setOnItemClickListener(new StockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Context context, int position) {
                Intent intent = new Intent(context, StockInfoActivity.class);
                intent.putExtra(SYMBOL_MESSAGE, stocksFragment.getData().get(position).getSymbol());
                intent.putExtra(COMPANY_MESSAGE, stocksFragment.getData().get(position).getCompany());
                startActivity(intent);
            }
        });

        stockAdapter.setOnLongClickListener(new StockAdapter.OnLongClickListener() {
            @Override
            public boolean OnLongClick(Context context, int position) {
                String symbol = stocksFragment.getData().get(position).getSymbol();
                String message = getString(R.string.stop_follow) + " " + symbol + " ?";
                StopFollowDialog stopFollowDialog = StopFollowDialog.newInstance(message, symbol);
                stopFollowDialog.setOnDialogFinishListener(new StopFollowDialog.OnDialogFinishListener() {
                    @Override
                    public void onDialogFinish(boolean result, String message) {
                        onStopFollowFinished(result, message);
                    }
                });
                stopFollowDialog.show(getSupportFragmentManager(), "stopFollowFragment");
                return true;
            }
        });
    }

    private void onStopFollowFinished(boolean result, String message){
        if(result){
            StockQuote stockToRemove = null;

            for(StockQuote stock : stocksFragment.getData()){
                if(stock.getSymbol().equals(message)){
                    stockToRemove = stock;
                }
            }

            if(stockToRemove != null){
                stocksFragment.getData().remove(stockToRemove);
                stockAdapter.notifyDataSetChanged();
            }

            if(stocksFragment.getData().size() == 0)
                setEmptyWalletAdapter();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        stocksFragment.setData(stocksFragment.getData());
    }

    private List<String> getFavouriteSymbols(){
        SettingsUtil settings = new SettingsUtil(getApplicationContext());
        return settings.getFavouriteStocks();
    }

    private void setErrorAdapter(String message){
        ListView listView = findViewById(R.id.stock_list);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.pullToRefresh);
        ErrorAdapter errorAdapter = new ErrorAdapter(this, message);
        listView.setAdapter(errorAdapter);
        refreshLayout.setRefreshing(false);
    }

    private void setEmptyWalletAdapter(){
        ListView listView = findViewById(R.id.stock_list);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.pullToRefresh);
        EmptyWalletAdapter emptyWalletAdapter = new EmptyWalletAdapter(this);
        listView.setAdapter(emptyWalletAdapter);
        refreshLayout.setRefreshing(false);
    }

    private void onRefreshComplete(List<StockQuote> list){
        ListView listView = findViewById(R.id.stock_list);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.pullToRefresh);
        listView.setAdapter(stockAdapter);
        stocksFragment.getData().clear();

        for(StockQuote stock : list)
            stocksFragment.getData().add(stock);

        stockAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    public class LoadStockTask extends AsyncTask<List<String>, Void, List<StockQuote>> {

        private String errorMessage;

        private void setErrorMessage(String message){
            errorMessage = message;
        }

        private String getErrorMessage(){
            return errorMessage;
        }

        @Override
        protected List<StockQuote> doInBackground(List<String>... params) {
            try {
                ApiConnector api = new WorldTradingConnector(getBaseContext());
                return api.getLastQuote(params[0]);
            } catch (Exception e) {
                setErrorMessage(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<StockQuote> result) {
            super.onPostExecute(result);

            if(result == null)
                setErrorAdapter(getErrorMessage());
            else
                onRefreshComplete(result);
        }
    }
}
