package org.abondar.experimental.androidbasics;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.abondar.experimental.androidbasics.dialogfragments.AlertDialogFragment;
import org.abondar.experimental.androidbasics.dialogfragments.HelpDialogFragment;
import org.abondar.experimental.androidbasics.dialogfragments.OnDialogDoneListener;
import org.abondar.experimental.androidbasics.dialogfragments.PromptDialogFragment;
import org.abondar.experimental.androidbasics.fragmentdemo.FragmentsActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnDialogDoneListener {

    public static String PROMPT_DIALOG_TAG = "PromptDialog";
    public static String HELP_DIALOG_TAG = "HelpDialog";
    public static String ALERT_DIALOG_TAG = "AlertDialog";
    private int counter = 0;
    private VideoView videoView;

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

        ToggleButton tb = (ToggleButton) this.findViewById(R.id.toggleButton);


        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.song);


        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mediaPlayer.start();
                } else {
                    mediaPlayer.pause();
                }
            }
        });

        Button startBtn = (Button) this.findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BackGroundService.class);
                intent.putExtra("counter", counter++);
                startService(intent);
            }
        });

        Button stopBtn = (Button) this.findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, BackGroundService.class));
            }
        });

        Button longTaskBtn = (Button) this.findViewById(R.id.longTaskBtn);
        longTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LongTask lt = new LongTask(MainActivity.this, "Task1");
                lt.onPreExecute();
                lt.execute("ss1", "ss2", "ss3");

                LongTask1 lt1 = new LongTask1(MainActivity.this, "Task1");
                lt1.onPreExecute();
                lt1.execute("ss1", "ss2", "ss3");

            }
        });

        sendRepeatedAlarm();

        videoView = (VideoView) this.findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
//        videoView.setVideoURI(Uri.parse("https://youtu.be/rCo7LsuPiCs"));
//        videoView.requestFocus();
//        videoView.start();
//
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.textControl:
                Intent intent = new Intent(this, TextControlsActivity.class);
                startActivity(intent);
            case R.id.imageButton:
                Toast.makeText(view.getContext(), "Test1", Toast.LENGTH_SHORT).show();
                Log.v("Image Button", "SAAAALO");
                broadcastIntent(view);
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
        //don't replace for switch - doesn't work
        if (item.getItemId() == R.id.menu_item1) {
            showMultiControl();
        } else if (item.getItemId() == R.id.menu_item2) {
            showTime();
        } else if (item.getItemId() == R.id.menu_item3) {
            showScale();
        } else if (item.getItemId() == R.id.menu_item4) {
            showDrag();
        } else if (item.getItemId() == R.id.menu_item5) {
            showGravity();
        } else if (item.getItemId() == 103) {
            showPromptDialog();
        } else if (item.getItemId() == 104) {
            showAlertDialog();
        } else if (item.getItemId() == 105) {
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

    private void showScale() {
        Intent intent = new Intent(this, ScaleActivity.class);
        startActivity(intent);
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

    private void showDrag() {
        Intent intent = new Intent(this, DragDropActivity.class);
        startActivity(intent);
    }

    private void showGravity() {
        Intent intent = new Intent(this, GravityActivity.class);
        startActivity(intent);
    }


    private void broadcastIntent(View view) {
        Intent intent = new Intent();
        intent.setAction("com.parse.push.intent.OPEN");
        sendBroadcast(intent);
    }

    private void sendRepeatedAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);

        Intent intent = new Intent(this, AlarmReciever.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 5, pendingIntent);
    }

}
