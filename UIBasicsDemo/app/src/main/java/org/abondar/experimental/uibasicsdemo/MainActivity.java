package org.abondar.experimental.uibasicsdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = (Button) this.findViewById(R.id.textControl);
        b.setOnClickListener(this);

        ImageButton ib = (ImageButton) this.findViewById(R.id.imageButton);
        ib.setImageResource(R.drawable.icon);
        ib.setOnClickListener(this);

        Button cb = (Button) this.findViewById(R.id.multiControl);
        cb.setOnClickListener(this);

        Button tb = (Button) this.findViewById(R.id.timeControl);
        tb.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.textControl) {
            Intent intent = new Intent(this, TextControlsActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.imageButton) {
            Log.v("Image Button", "SAAAALO");
        } else if (view.getId() == R.id.multiControl) {
            Intent intent = new Intent(this, MultiControlActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.timeControl) {
            Intent intent = new Intent(this, TimeActivity.class);
            startActivity(intent);
        }

    }


}
