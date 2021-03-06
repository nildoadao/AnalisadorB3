package br.com.analisadorb3.view;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import android.os.Bundle;

import java.util.Set;

import br.com.analisadorb3.R;
import br.com.analisadorb3.util.SettingsUtil;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SettingsUtil.saveFavouriteStock(getBaseContext(), "B3SA3.SA");
        SettingsUtil.saveFavouriteStock(getBaseContext(), "PETR4.SA");
        SettingsUtil.saveFavouriteStock(getBaseContext(), "MGLU3.SA");
        if(savedInstanceState == null)
            Navigation.findNavController(this, R.id.navigation_host_fragment).navigateUp();
    }
}
