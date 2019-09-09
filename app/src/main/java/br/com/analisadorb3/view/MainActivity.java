package br.com.analisadorb3.view;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import br.com.analisadorb3.R;
import br.com.analisadorb3.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainFragment fragment = MainFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment, "mainFragment").commit();
    }
}
