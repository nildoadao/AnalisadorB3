package br.com.analisadorb3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import br.com.analisadorb3.R;

public class ErrorAdapter extends BaseAdapter {

    private String message;
    private static LayoutInflater inflater = null;

    public ErrorAdapter(Context context, String message){
        this.message = message;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int i) {
        return message;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = inflater.inflate(R.layout.error_item, viewGroup, false);

        TextView errorText = view.findViewById(R.id.error_item_message);
        errorText.setText(message);
        return view;
    }
}
