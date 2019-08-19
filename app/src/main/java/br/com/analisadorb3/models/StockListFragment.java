package br.com.analisadorb3.models;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import java.util.List;

public class StockListFragment extends Fragment {
    private List<StockQuote> stock;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setRetainInstance(true);
    }

    public void setData(List<StockQuote> stock){
        this.stock = stock;
    }

    public List<StockQuote> getData(){
        return stock;
    }
}
