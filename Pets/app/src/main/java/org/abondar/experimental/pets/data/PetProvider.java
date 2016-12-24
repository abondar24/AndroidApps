package org.abondar.experimental.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import org.abondar.experimental.pets.data.PetContract.PetEntry;

/**
 * Created by abondar on 12/23/16.
 */
public class PetProvider extends ContentProvider {
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetDbHelper petDbHelper;

    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        matcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);

    }

    @Override
    public boolean onCreate() {
        petDbHelper = new PetDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selArgs, String sortOrder) {
        SQLiteDatabase db = petDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = matcher.match(uri);

        switch (match) {
            case PETS:
                cursor = db.query(PetEntry.TABLE_NAME, projection, selection,
                        selArgs, null, null, sortOrder);
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetEntry.TABLE_NAME, projection, selection,
                        selArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = matcher.match(uri);

        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertPet(Uri uri, ContentValues values) {
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }

        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }


        SQLiteDatabase database = petDbHelper.getWritableDatabase();
        long id = database.insert(PetEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selArgs) {
        SQLiteDatabase database = petDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = matcher.match(uri);
        switch (match) {
            case PETS:
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selArgs) {

        final int match = matcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selArgs);
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updatePet(Uri uri, ContentValues values, String selection, String[] selArgs) {
        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = petDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PetEntry.TABLE_NAME, values, selection, selArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;

    }
}
