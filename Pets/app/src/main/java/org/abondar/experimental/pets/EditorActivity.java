package org.abondar.experimental.pets;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import org.abondar.experimental.pets.data.PetContract.PetEntry;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText nameEditText;
    private EditText breedEditText;
    private EditText weightEditText;
    private Spinner genderSpinner;
    private int gender = PetEntry.GENDER_UNKNOWN;
    private Uri curPetUri;
    private static final int EXISTING_PET_LOADER = 0;
    private boolean petHasChanged = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            petHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        curPetUri = intent.getData();

        if (curPetUri == null) {
            setTitle(getString(R.string.editor_activity_title_add_pet));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_pet));
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }


        nameEditText = (EditText) this.findViewById(R.id.edit_pet_name);
        breedEditText = (EditText) this.findViewById(R.id.edit_pet_breed);
        weightEditText = (EditText) this.findViewById(R.id.edit_pet_weight);
        genderSpinner = (Spinner) this.findViewById(R.id.spinner_gender);

        setupSpinner();

        nameEditText.setOnTouchListener(touchListener);
        breedEditText.setOnTouchListener(touchListener);
        weightEditText.setOnTouchListener(touchListener);
        genderSpinner.setOnTouchListener(touchListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePet();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!petHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);

                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        genderSpinner.setAdapter(genderSpinnerAdapter);


        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                String selection = (String) parent.getItemAtPosition(pos);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        gender = PetEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        gender = PetEntry.GENDER_FEMALE;
                    } else {
                        gender = PetEntry.GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                gender = PetEntry.GENDER_UNKNOWN;
            }
        });

    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT};


        return new CursorLoader(this, curPetUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            nameEditText.setText(cursor.getString(nameColumnIndex));
            breedEditText.setText(cursor.getString(breedColumnIndex));
            weightEditText.setText(Integer.toString(cursor.getInt(weightColumnIndex)));

            switch (cursor.getInt(genderColumnIndex)) {
                case PetEntry.GENDER_MALE:
                    genderSpinner.setSelection(1);
                    break;
                case PetEntry.GENDER_FEMALE:
                    genderSpinner.setSelection(2);
                    break;
                default:
                    genderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        nameEditText.setText("");
        breedEditText.setText("");
        weightEditText.setText("");
        genderSpinner.setSelection(0);
    }

    @Override
    public void onBackPressed() {
        if (!petHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (curPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void savePet() {

        ContentValues values = new ContentValues();

        String name = nameEditText.getText().toString().trim();
        String breed = breedEditText.getText().toString().trim();
        String weightStr = weightEditText.getText().toString().trim();
        if (curPetUri == null &&
                TextUtils.isEmpty(name) && TextUtils.isEmpty(breed) &&
                TextUtils.isEmpty(weightStr) && gender == PetEntry.GENDER_UNKNOWN) {
            return;

        }

        values.put(PetEntry.COLUMN_PET_NAME, name);
        values.put(PetEntry.COLUMN_PET_BREED, breed);
        values.put(PetEntry.COLUMN_PET_GENDER, gender);

        int weight = 0;
        if (!TextUtils.isEmpty(weightStr)) {
            weight = Integer.parseInt(weightStr);
        }
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);

        if (curPetUri == null) {
            Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(curPetUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deletePet() {

        if (curPetUri != null) {
            int rowsDeleted = getContentResolver().delete(curPetUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}