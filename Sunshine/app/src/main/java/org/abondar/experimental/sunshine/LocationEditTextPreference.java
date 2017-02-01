package org.abondar.experimental.sunshine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

/**
 * Created by abondar on 1/22/17.
 */
public class LocationEditTextPreference extends EditTextPreference {
    private static final int DEFAULT_MIN_LOCATION_LENGTH = 3;
    private int minLength;

    public LocationEditTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet,
                R.styleable.LocationEditTextPreference, 0, 0);

        try {
            minLength = typedArray.getInteger(R.styleable.LocationEditTextPreference_minLength,
                    DEFAULT_MIN_LOCATION_LENGTH);
        } finally {
            typedArray.recycle();
        }

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (resultCode == ConnectionResult.SUCCESS) {
            setWidgetLayoutResource(R.layout.settings_current_location);
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        View currentLocation = view.findViewById(R.id.current_location);
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getContext();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Activity settingsActivity = (SettingsActivity) context;
                try {
                    settingsActivity.startActivityForResult(
                            builder.build(settingsActivity), SettingsActivity.PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException
                        | GooglePlayServicesRepairableException e) {

                }
            }
        });

        return view;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        EditText editText = getEditText();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Dialog dialog = getDialog();
                if (dialog instanceof AlertDialog) {
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                    if (editable.length() < minLength) {
                        positiveButton.setEnabled(false);
                    } else {
                        positiveButton.setEnabled(true);
                    }
                }
            }
        });
    }
}
