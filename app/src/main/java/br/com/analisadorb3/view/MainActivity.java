package br.com.analisadorb3.view;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import br.com.analisadorb3.R;
import br.com.analisadorb3.fragments.MainFragment;
import br.com.analisadorb3.util.SettingsUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SettingsUtil settingsUtil = new SettingsUtil(this);
        settingsUtil.saveFavouriteStock("B3SA3.SA");
        settingsUtil.saveFavouriteStock("ITSA4.SA");
        settingsUtil.saveFavouriteStock("VVAR3.SA");
        settingsUtil.saveFavouriteStock("MGLU3.SA");
        settingsUtil.saveFavouriteStock("OIBR3.SA");
        MainFragment fragment = MainFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment, "mainFragment").commit();
    }
}
