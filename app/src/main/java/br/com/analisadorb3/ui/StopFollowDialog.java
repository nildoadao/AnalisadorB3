package br.com.analisadorb3.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import br.com.analisadorb3.R;
import br.com.analisadorb3.util.SettingsUtil;

public class StopFollowDialog extends DialogFragment {

    private OnDialogFinishListener listener;

    public interface OnDialogFinishListener{
        void onDialogFinish(boolean result, String message);
    }

    public void setOndialogFinishListener(OnDialogFinishListener listener){
        this.listener = listener;
    }

    public static StopFollowDialog newInstance(String message, String symbol){
        StopFollowDialog dialog = new StopFollowDialog();
        Bundle args = new Bundle();
        args.putString("title", message);
        args.putString("symbol", symbol);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        final String symbol = getArguments().getString("symbol");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(title)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SettingsUtil settings = new SettingsUtil(getContext());
                        if(settings.removeFavouriteStock(symbol)){
                            Toast.makeText(getContext(), getString(R.string.removed), Toast.LENGTH_SHORT).show();
                            if(listener != null)
                                listener.onDialogFinish(true, symbol);
                        }

                        else{
                            Toast.makeText(getContext(), getString(R.string.remove_fail), Toast.LENGTH_LONG).show();
                            if(listener != null)
                                listener.onDialogFinish(false, symbol);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
