package org.abondar.experimental.uibasicsdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

        TextView tv =(TextView)this.findViewById(R.id.cmtv);
        registerForContextMenu(tv);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.textControl) {
            Intent intent = new Intent(this, TextControlsActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.imageButton) {
            Log.v("Image Button", "SAAAALO");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        int base = Menu.FIRST +100;
        SubMenu sm = menu.addSubMenu(base,base+1,Menu.NONE,"submenu");
        sm.add(base,base+2,base+2,"sub menu item1");
        sm.add(base,base+3,base+3,"sub menu item2");

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item1) {

            Intent intent = new Intent(this, MultiControlActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_item2) {

            Intent intent = new Intent(this, TimeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Sample context menu");
        menu.add(200,200,200 ,"Action bars");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.v("salo","salo");
        return true;
    }
}
