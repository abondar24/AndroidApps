package experimental.abondar.org.miwok2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NumbersActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new NumbersFragment()).commit();

            }




}
