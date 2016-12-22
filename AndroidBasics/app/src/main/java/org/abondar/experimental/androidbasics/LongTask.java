package org.abondar.experimental.androidbasics;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by abondar on 12/14/16.
 */
public class LongTask extends AsyncTask<String,Integer,Integer> {

    Context ctx;
    public String tag = null;
    ProgressDialog pd = null;


    LongTask(Context inCtx, String inTag){
        ctx = inCtx;
        tag = inTag;
    }

    protected void onPreExecute(){
       pd = ProgressDialog.show(ctx,"title","In progress...",true);
    }

    protected  void onPostExecute(Integer result){
        pd.cancel();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        for (String s:strings){
            Log.d(tag,"Processing:"+s);
        }
        for (int i=0;i<3;i++){
            publishProgress(i);
        }
        return 1;
    }
}
