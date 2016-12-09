package org.abondar.experimental.uibasicsdemo.dialogfragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.abondar.experimental.uibasicsdemo.R;

/**
 * Created by abondar on 12/8/16.
 */
public class HelpDialogFragment extends DialogFragment
        implements View.OnClickListener
{
    public static HelpDialogFragment
    newInstance(int helpResId)
    {
        HelpDialogFragment hdf = new HelpDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("help_resource", helpResId);
        hdf.setArguments(bundle);

        return hdf;
    }


    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        this.setCancelable(true);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style,theme);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle icicle)
    {
        View v = inflater.inflate(
                R.layout.help_dialog, container, false);

        TextView tv = (TextView)v.findViewById(
                R.id.helpMessage);
        tv.setText(getActivity().getResources()
                .getText(getArguments().getInt("help_resource")));

        Button closeBtn = (Button)v.findViewById(
                R.id.btnClose);
        closeBtn.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
