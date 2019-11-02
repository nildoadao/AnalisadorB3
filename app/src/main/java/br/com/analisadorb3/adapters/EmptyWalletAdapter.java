package br.com.analisadorb3.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.com.analisadorb3.R;

public class EmptyWalletAdapter extends RecyclerView.Adapter<EmptyWalletAdapter.EmptyWalletHolder> {

    @NonNull
    @Override
    public EmptyWalletHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.empty_wallet, parent, false);
        return new EmptyWalletHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmptyWalletHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class EmptyWalletHolder extends RecyclerView.ViewHolder{

        public EmptyWalletHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
