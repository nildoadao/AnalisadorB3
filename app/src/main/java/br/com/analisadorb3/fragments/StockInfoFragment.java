package br.com.analisadorb3.fragments;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.analisadorb3.R;
import br.com.analisadorb3.viewmodel.StockInfoViewModel;

public class StockInfoFragment extends Fragment {

    private StockInfoViewModel mViewModel;

    public static StockInfoFragment newInstance() {
        return new StockInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stock_info_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StockInfoViewModel.class);
        // TODO: Use the ViewModel
    }

}
