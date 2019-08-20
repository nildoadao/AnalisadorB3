package br.com.analisadorb3.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.analisadorb3.R;
import br.com.analisadorb3.api.ApiConnector;
import br.com.analisadorb3.api.ApiException;
import br.com.analisadorb3.api.WorldTradingConnector;
import br.com.analisadorb3.models.StockQuote;
import br.com.analisadorb3.Adaptors.SearchItemAdapter;

public class SearchActivity extends AppCompatActivity {

    private SearchItemAdapter searchItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final EditText search = findViewById(R.id.search_input);
        searchItemAdapter = new SearchItemAdapter(this, new ArrayList<StockQuote>());
        ListView resultList = findViewById(R.id.search_result_list);
        resultList.setAdapter(searchItemAdapter);
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    new SearchTask().execute(search.getText().toString());
                    return true;
                }
                return false;
            }
        });
        search.requestFocus();
    }

    private void onSearchCompleted(List<StockQuote> list){
        if(list != null){
            searchItemAdapter.getData().clear();

            for(StockQuote stock : list)
                searchItemAdapter.getData().add(stock);

            searchItemAdapter.notifyDataSetChanged();
        }
    }

    public class SearchTask extends AsyncTask<String, Void, List<StockQuote>> {

        @Override
        protected List<StockQuote> doInBackground(String... params) {
            try{
                ApiConnector api = new WorldTradingConnector(getBaseContext());
                return api.searchEndpoint(params[0]);
            }
            catch (ApiException e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<StockQuote> result) {
            super.onPostExecute(result);
            onSearchCompleted(result);
        }
    }
}
