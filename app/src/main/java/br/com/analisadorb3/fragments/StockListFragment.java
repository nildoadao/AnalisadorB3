package br.com.analisadorb3.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import java.util.List;

import br.com.analisadorb3.models.StockQuote;

public class StockListFragment extends Fragment {

    private List<StockQuote> stock;

    public static StockListFragment newInstance(List<StockQuote> data){
        StockListFragment fragment = new StockListFragment();
        fragment.setData(data);
        return fragment;
    }

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
