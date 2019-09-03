package br.com.analisadorb3.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import br.com.analisadorb3.models.StockQuote;

public class StockFragment extends Fragment {
    private StockQuote stock;

    public static StockFragment newInstance(StockQuote data){
        StockFragment fragment = new StockFragment();
        fragment.setData(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setRetainInstance(true);
    }

    public void setData(StockQuote stock){
        this.stock = stock;
    }

    public StockQuote getData(){
        return stock;
    }
}
