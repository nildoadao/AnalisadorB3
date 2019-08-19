package br.com.analisadorb3.models;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class StockFragment extends Fragment {
    private StockQuote stock;

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
