package br.com.analisadorb3.ui;
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
import br.com.analisadorb3.adaptors.EmptyWalletAdapter;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.models.StockListFragment;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.adaptors.ErrorAdapter;
import br.com.analisadorb3.adaptors.StockAdapter;
import br.com.analisadorb3.util.SettingsUtil;

public class MainActivity extends AppCompatActivity {

    public static final String SYMBOL_MESSAGE = "br.com.analisadorb3.SYMBOL";
    public static final String COMPANY_MESSAGE = "br.com.analisadorb3.COMPANY";
    private SwipeRefreshLayout refresh;
    private StockListFragment stocks;
    private StockAdapter adapter;
    private ErrorAdapter errorAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentManager manager = getSupportFragmentManager();
        stocks = (StockListFragment) manager.findFragmentByTag("stockListFragment");

        if(stocks == null){
            stocks = new StockListFragment();
            stocks.setData(new ArrayList<StockQuote>());
            manager.beginTransaction().add(stocks, "stockListFragment").commit();
            refresh = findViewById(R.id.pullToRefresh);
            refresh.setRefreshing(true);
            new LoadStockTask().execute(getFavouriteSymbols());
        }

        list = findViewById(R.id.stock_list);
        adapter = new StockAdapter(this, stocks.getData());
        adapter.setOnItemClickListener(new StockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Context context, int position) {
                Intent intent = new Intent(context, StockInfoActivity.class);
                intent.putExtra(SYMBOL_MESSAGE, stocks.getData().get(position).getSymbol());
                intent.putExtra(COMPANY_MESSAGE, stocks.getData().get(position).getCompany());
                startActivity(intent);
            }
        });

        adapter.setOnLongClickListener(new StockAdapter.OnLongClickListener() {
            @Override
            public boolean OnLongClick(Context context, int position) {
                String symbol = stocks.getData().get(position).getSymbol();
                String message = getString(R.string.stop_follow) + " " + symbol + " ?";
                StopFollowDialog stopFollowDialog = StopFollowDialog.newInstance(message, symbol);
                stopFollowDialog.setOndialogFinishListener(new StopFollowDialog.OnDialogFinishListener() {
                    @Override
                    public void onDialogFinish(boolean result, String message) {
                        onStopFollowFinished(result, message);
                    }
                });
                stopFollowDialog.show(manager, "stopFollowFragment");
                 return true;
            }
        });

        ImageButton search = findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        list.setAdapter(adapter);
        refresh = findViewById(R.id.pullToRefresh);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!refresh.isRefreshing())
                    refresh.setRefreshing(true);

                new LoadStockTask().execute(getFavouriteSymbols());
            }
        });
    }

    private void onStopFollowFinished(boolean result, String message){
        if(result){
            StockQuote stockToRemove = null;

            for(StockQuote stock : stocks.getData()){
                if(stock.getSymbol() == message){
                    stockToRemove = stock;
                }
            }

            if(stockToRemove != null){
                stocks.getData().remove(stockToRemove);
                adapter.notifyDataSetChanged();
            }

            if(stocks.getData().size() == 0){
                ListView listView = findViewById(R.id.stock_list);
                EmptyWalletAdapter emptyWalletAdapter = new EmptyWalletAdapter(this);
                listView.setAdapter(emptyWalletAdapter);
                return;
            }
        }
    }

    public void onDestroy(){
        super.onDestroy();
        stocks.setData(stocks.getData());
    }

    private List<String> getFavouriteSymbols(){
        SettingsUtil settings = new SettingsUtil(getApplicationContext());
        return settings.getFavouriteStocks();
    }

    private void onRefreshComplete(List<StockQuote> list){
        ListView listView = findViewById(R.id.stock_list);

        if(getFavouriteSymbols().size() == 0){
            EmptyWalletAdapter emptyWalletAdapter = new EmptyWalletAdapter(this);
            listView.setAdapter(emptyWalletAdapter);
            refresh.setRefreshing(false);
            return;
        }

        if(list == null){
            errorAdapter = new ErrorAdapter(this, getString(R.string.no_internet));
            listView.setAdapter(errorAdapter);
            refresh.setRefreshing(false);
        }
        else {
            listView.setAdapter(adapter);
            stocks.getData().clear();

            for(StockQuote stock : list)
                stocks.getData().add(stock);

            adapter.notifyDataSetChanged();
            refresh.setRefreshing(false);
        }
    }

    public class LoadStockTask extends AsyncTask<List<String>, Void, List<StockQuote>> {
        @Override
        protected List<StockQuote> doInBackground(List<String>... params) {
            try {
                ApiConnector api = new WorldTradingConnector(getBaseContext());
                return api.getLastQuote(params[0]);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<StockQuote> result) {
            super.onPostExecute(result);
            onRefreshComplete(result);
        }
    }
}
