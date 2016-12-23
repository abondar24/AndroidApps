package org.abondar.experimental.pets;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.abondar.experimental.pets.data.PetContract.PetEntry;
import org.abondar.experimental.pets.data.PetDbHelper;

public class EditorActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText breedEditText;
    private EditText weightEditText;
    private Spinner genderSpinner;
    private int gender = PetEntry.GENDER_UNKNOWN;
    private PetDbHelper petDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        nameEditText = (EditText) this.findViewById(R.id.edit_pet_name);
        breedEditText = (EditText) this.findViewById(R.id.edit_pet_breed);
        weightEditText = (EditText) this.findViewById(R.id.edit_pet_weight);
        genderSpinner = (Spinner) this.findViewById(R.id.spinner_gender);

        setupSpinner();
        petDbHelper = new PetDbHelper(this);
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
                insertPet();
                finish();
                return true;
            case R.id.action_delete:
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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

    private void insertPet () {
        SQLiteDatabase db = petDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        String name = nameEditText.getText().toString().trim();
        String breed = breedEditText.getText().toString().trim();
        int weight = Integer.valueOf(weightEditText.getText().toString().trim());
        values.put(PetEntry.COLUMN_PET_NAME, name);
        values.put(PetEntry.COLUMN_PET_BREED, breed);
        values.put(PetEntry.COLUMN_PET_GENDER, gender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);

        long newRowId = db.insert(PetEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();

        }
    }
}