package br.com.analisadorb3.fragments;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.analisadorb3.R;
import br.com.analisadorb3.databinding.MainFragmentBinding;
import br.com.analisadorb3.viewmodel.MainViewModel;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;

    private void setViewModel(MainViewModel viewModel){
        this.viewModel = viewModel;
    }

    public static MainFragment newInstance(MainViewModel viewModel) {
        MainFragment fragment = new MainFragment();
        fragment.setViewModel(viewModel);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        binding.setViewmodel(viewModel);
        binding.stockRecyclerView.setAdapter(viewModel.getAdapter());
        binding.stockRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null){
            viewModel.init();
            viewModel.fetchData();
        }
    }
}
