package org.abondar.experimental.androidbasics.dialogfragments;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.abondar.experimental.androidbasics.MainActivity;
import org.abondar.experimental.androidbasics.R;


/**
 * Created by abondar on 12/8/16.
 */
public class PromptDialogFragment extends DialogFragment implements View.OnClickListener {

    public static PromptDialogFragment newInstance(String prompt) {

        PromptDialogFragment pdf = new PromptDialogFragment();

        //Supply index input as an argument
        Bundle args = new Bundle();
        args.putString("prompt", prompt);
        pdf.setArguments(args);
        return pdf;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        int style = DialogFragment.STYLE_NORMAL;
        int theme = 0;
        setStyle(style, theme);
        this.setCancelable(true);
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle icicle) {
        View v = inflater.inflate(R.layout.prompt_dialog, container, false);

        TextView tv = (TextView) v.findViewById(R.id.promptMessage);
        tv.setText(getArguments().getString("prompt"));

        Button dismissBtn = (Button) v.findViewById(
                R.id.btnDismiss);
        dismissBtn.setOnClickListener(this);

        Button saveBtn = (Button) v.findViewById(
                R.id.btn_save);
        saveBtn.setOnClickListener(this);

        Button helpBtn = (Button) v.findViewById(
                R.id.btnHelp);
        helpBtn.setOnClickListener(this);

        return v;
    }

    @Override
    public void onCancel(DialogInterface di) {
        super.onCancel(di);
    }

    @Override
    public void onDismiss(DialogInterface di) {
        super.onDismiss(di);
    }

    public void onClick(View v)
    {
        OnDialogDoneListener act = (OnDialogDoneListener)getActivity();
        if (v.getId() == R.id.btn_save)
        {
            TextView tv = (TextView)getView().findViewById(R.id.inputText);
            act.onDialogDone(this.getTag(), false, tv.getText());
            dismiss();
            return;
        }
        if (v.getId() == R.id.btnDismiss)
        {
            act.onDialogDone(this.getTag(), true, null);
            dismiss();
            return;
        }
        if (v.getId() == R.id.btnHelp)
        {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(this);

            // in this case, we want to show the help text, but
            // come back to the previous dialog when we're done
            ft.addToBackStack(null);
            //null represents no name for the back stack transaction

            HelpDialogFragment hdf =
                    HelpDialogFragment.newInstance(R.string.help1);
            hdf.show(ft, MainActivity.HELP_DIALOG_TAG);
            return;
        }


    }
}
