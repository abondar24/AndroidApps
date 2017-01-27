package org.abondar.experimental.sunshine;

import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.AbsListView;
import android.widget.Checkable;

/**
 * Created by abondar on 1/25/17.
 */
public class ItemChoiceManager {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String SELECTED_ITEMS_KEY = "SIK";
    private int choiceMode;
    private RecyclerView.Adapter adapter;


    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (adapter != null && adapter.hasStableIds()) {
                confirmCheckedPositionsById(adapter.getItemCount());
            }
        }
    };

    private static final int CHECK_POSITION_SEARCH_DISTANCE = 20;

    private SparseBooleanArray checkStates = new SparseBooleanArray();

    private LongSparseArray<Integer> checkedIdStates = new LongSparseArray<>();

    private ItemChoiceManager() {
    }

    public ItemChoiceManager(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }


    public void onClick(RecyclerView.ViewHolder vh) {
        if (choiceMode == AbsListView.CHOICE_MODE_NONE)
            return;

        int checkedItemCount = checkStates.size();
        int position = vh.getAdapterPosition();

        if (position == RecyclerView.NO_POSITION) {
            Log.d(LOG_TAG, "Unable to Set Item State");
            return;

        }

        switch (choiceMode) {
            case AbsListView.CHOICE_MODE_NONE:
                break;
            case AbsListView.CHOICE_MODE_SINGLE: {
                boolean checked = checkStates.get(position, false);
                if (!checked) {
                    for (int i = 0; i < checkedItemCount; i++) {
                        adapter.notifyItemChanged(checkStates.keyAt(i));

                    }
                    checkStates.clear();
                    checkStates.put(position, true);
                    checkedIdStates.clear();
                    checkedIdStates.put(adapter.getItemId(position), position);

                }
                adapter.onBindViewHolder(vh, position);
                break;

            }
            case AbsListView.CHOICE_MODE_MULTIPLE: {
                boolean checked = checkStates.get(position, false);
                checkStates.put(position, !checked);
                adapter.onBindViewHolder(vh, position);
                break;
            }
            case AbsListView.CHOICE_MODE_MULTIPLE_MODAL: {
                throw new RuntimeException("Multiple Modal not implemented in ItemChoiceManager.");
            }

        }

    }


    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        boolean checked = isItemChecked(position);
        if (vh.itemView instanceof Checkable) {
            ((Checkable) vh.itemView).setChecked(checked);

        }
        ViewCompat.setActivated(vh.itemView, checked);

    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        byte[] states = savedInstanceState.getByteArray(SELECTED_ITEMS_KEY);
        if (null != states) {
            Parcel inParcel = Parcel.obtain();
            inParcel.unmarshall(states, 0, states.length);
            inParcel.setDataPosition(0);
            checkStates = inParcel.readSparseBooleanArray();
            final int numStates = inParcel.readInt();
            checkedIdStates.clear();
            for (int i = 0; i < numStates; i++) {
                final long key = inParcel.readLong();
                final int value = inParcel.readInt();
                checkedIdStates.put(key, value);
            }
        }
    }


    public void onSaveInstanceState(Bundle outState) {
        Parcel outParcel = Parcel.obtain();
        outParcel.writeSparseBooleanArray(checkStates);
        final int numStates = checkedIdStates.size();
        outParcel.writeInt(numStates);

        for (int i = 0; i < numStates; i++) {
            outParcel.writeLong(checkedIdStates.keyAt(i));
            outParcel.writeInt(checkedIdStates.valueAt(i));
        }

        byte[] states = outParcel.marshall();
        outState.putByteArray(SELECTED_ITEMS_KEY, states);
        outParcel.recycle();

    }


    public int getSelectedItemPosition() {
        if (checkStates.size() == 0) {
            return RecyclerView.NO_POSITION;

        } else {
            return checkStates.keyAt(0);
        }
    }




    public void setChoiceMode(int choiceMode) {
        if (this.choiceMode != choiceMode) {
            this.choiceMode = choiceMode;
            clearSelections();
        }
    }

    public boolean isItemChecked(int position) {
        return checkStates.get(position);
    }


    private void clearSelections() {
        checkStates.clear();
        checkedIdStates.clear();
    }


    private void confirmCheckedPositionsById(int oldItemCount) {
        checkStates.clear();

        for (int checkedIndex = 0; checkedIndex < checkedIdStates.size(); checkedIndex++) {
            final long id = checkedIdStates.keyAt(checkedIndex);
            final int lastPos = checkedIdStates.valueAt(checkedIndex);
            final long lastPosId = adapter.getItemId(lastPos);

            if (id != lastPosId) {
                final int start = Math.max(0, lastPos - CHECK_POSITION_SEARCH_DISTANCE);
                final int end = Math.min(lastPos + CHECK_POSITION_SEARCH_DISTANCE, oldItemCount);
                boolean found = false;
                for (int searchPos = start; searchPos < end; searchPos++) {
                    final long searchId = adapter.getItemId(searchPos);
                    if (id == searchId) {
                        found = true;
                        checkStates.put(searchPos, true);
                        checkedIdStates.setValueAt(checkedIndex, searchPos);
                        break;
                    }

                }

                if (!found) {
                    checkedIdStates.delete(id);
                    checkedIndex--;
                }

            } else {
                checkStates.put(lastPos, true);
            }
        }
    }


}
