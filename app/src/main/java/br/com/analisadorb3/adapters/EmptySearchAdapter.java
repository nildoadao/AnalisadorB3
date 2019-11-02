package br.com.analisadorb3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.com.analisadorb3.R;

public class EmptySearchAdapter extends RecyclerView.Adapter<EmptySearchAdapter.EmptySearchHolder> {

    @NonNull
    @Override
    public EmptySearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.empty_search, parent, false);
        return new EmptySearchHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmptySearchHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class EmptySearchHolder extends RecyclerView.ViewHolder{
        public EmptySearchHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
