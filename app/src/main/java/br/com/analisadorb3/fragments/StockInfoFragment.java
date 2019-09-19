package br.com.analisadorb3.fragments;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.com.analisadorb3.R;
import br.com.analisadorb3.databinding.StockInfoFragmentBinding;
import br.com.analisadorb3.viewmodel.StockViewModel;

public class StockInfoFragment extends Fragment {

    private StockViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        StockInfoFragmentBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.stock_info_fragment, container,  false);
        mViewModel = ViewModelProviders.of(this).get(StockViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton backButton = getView().findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }
}
