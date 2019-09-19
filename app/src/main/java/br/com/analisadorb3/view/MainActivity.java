package br.com.analisadorb3.view;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import android.os.Bundle;
import br.com.analisadorb3.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null)
            Navigation.findNavController(this, R.id.navigation_host_fragment).navigateUp();
    }
}
