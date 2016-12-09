package org.abondar.experimental.uibasicsdemo;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.abondar.experimental.uibasicsdemo.dialogfragments.AlertDialogFragment;
import org.abondar.experimental.uibasicsdemo.dialogfragments.HelpDialogFragment;
import org.abondar.experimental.uibasicsdemo.dialogfragments.PromptDialogFragment;
import org.abondar.experimental.uibasicsdemo.fragmentdemo.FragmentsActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static   String PROMPT_DIALOG_TAG = "PromptDialog";
    public static String HELP_DIALOG_TAG = "HelpDialog";
    public static String ALERT_DIALOG_TAG = "AlertDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = (Button) this.findViewById(R.id.textControl);
        b.setOnClickListener(this);

        ImageButton ib = (ImageButton) this.findViewById(R.id.imageButton);
        ib.setImageResource(R.drawable.icon);
        ib.setOnClickListener(this);

        TextView tv = (TextView) this.findViewById(R.id.cmtv);
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
        int base = Menu.FIRST + 100;
        SubMenu sm = menu.addSubMenu(base, base + 1, Menu.NONE, "Dialogs");
        sm.add(base, base + 2, base + 2, "Prompt Dialog");
        sm.add(base, base + 3, base + 3, "Alert Dialog");
        sm.add(base, base + 4, base + 4, "Help Dialog");


        setupSearchView(menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item1:
                showTime();
            case R.id.menu_item2:
                showMultiControl();
            case 103:
                showPromptDialog();
            case 104:
                showAlertDialog();
            case 105:
                showHelpDialog();

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Sample context menu");
        menu.add(200, 200, 200, "Fragments");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Intent intent = new Intent(this, FragmentsActivity.class);
        startActivity(intent);
        return true;
    }

    public void onDialogDone(String tag, boolean cancelled, CharSequence message) {
        String s = tag + " " + message;
        if (cancelled) {
            s = tag + "was cancelled";
        }
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void setupSearchView(Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this, SearchResultActivity.class);
        SearchableInfo info = searchManager.getSearchableInfo(componentName);

        searchView.setSearchableInfo(info);
    }

    private void showMultiControl() {
        Intent intent = new Intent(this, MultiControlActivity.class);
        startActivity(intent);
    }

    private void showTime() {
        Intent intent = new Intent(this, TimeActivity.class);
        startActivity(intent);
    }

    private void showAlertDialog() {
        FragmentTransaction ft =
                getFragmentManager().beginTransaction();

        AlertDialogFragment adf =
                AlertDialogFragment.newInstance(
                        "Alert Message");

        adf.show(ft, ALERT_DIALOG_TAG);
    }

    private void showPromptDialog() {
        FragmentTransaction ft =
                getFragmentManager().beginTransaction();

        PromptDialogFragment pdf =
                PromptDialogFragment.newInstance(
                        "Enter Something");


        pdf.show(ft, PROMPT_DIALOG_TAG);
    }

    private void showHelpDialog() {
        FragmentTransaction ft =
                getFragmentManager().beginTransaction();

        HelpDialogFragment hdf =
                HelpDialogFragment.newInstance(
                        R.string.helpText);

        ft.addToBackStack(HELP_DIALOG_TAG);
        hdf.show(ft, HELP_DIALOG_TAG);
    }


}
