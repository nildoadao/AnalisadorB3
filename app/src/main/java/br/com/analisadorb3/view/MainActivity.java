package br.com.analisadorb3.view;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.com.analisadorb3.R;
import br.com.analisadorb3.fragments.MainFragment;
import br.com.analisadorb3.util.SettingsUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*SettingsUtil settingsUtil = new SettingsUtil(this);
        settingsUtil.saveFavouriteStock("B3SA3.SA");
        settingsUtil.saveFavouriteStock("ITSA4.SA");
        settingsUtil.saveFavouriteStock("VVAR3.SA");
        settingsUtil.saveFavouriteStock("MGLU3.SA");
        settingsUtil.saveFavouriteStock("OIBR3.SA");*/
        //MainFragment fragment = MainFragment.newInstance();
        Navigation.findNavController(this, R.id.navigation_host_fragment).navigateUp();
        //getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment, "mainFragment").commit();
    }
}
