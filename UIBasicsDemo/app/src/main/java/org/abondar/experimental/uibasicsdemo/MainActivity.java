package org.abondar.experimental.uibasicsdemo;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import org.abondar.experimental.uibasicsdemo.fragments.FragmentsActivity;

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
        setupSearchView(menu);

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
        menu.add(200,200,200 ,"Fragments");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Intent intent = new Intent(this, FragmentsActivity.class);
        startActivity(intent);
        return true;
    }

    private void setupSearchView(Menu menu){
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this,SearchResultActivity.class);
        SearchableInfo info = searchManager.getSearchableInfo(componentName);

        searchView.setSearchableInfo(info);
    }
}
