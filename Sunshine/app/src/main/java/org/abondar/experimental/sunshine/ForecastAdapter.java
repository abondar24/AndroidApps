package org.abondar.experimental.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.abondar.experimental.sunshine.data.WeatherContract;


/**
 * Created by abondar on 1/14/17.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private Cursor cursor;
    private Context context;

    private boolean useTodayLayout = true;
    private final ForecastAdapterOnClickHandler clickHandler;
    private final View emptyView;
    private final ItemChoiceManager choiceManager;


    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView iconView;
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            view.setOnClickListener(this);
            choiceManager.onClick(this);
        }

        @Override
        public void onClick(View view) {
            int adpaterPos = getAdapterPosition();
            cursor.moveToPosition(adpaterPos);
            int dateColumnIndex = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            clickHandler.onClick(cursor.getLong(dateColumnIndex), this);
        }
    }

    public interface ForecastAdapterOnClickHandler {
        void onClick(Long date, ForecastAdapterViewHolder viewHolder);
    }


    ForecastAdapter(Context context, ForecastAdapterOnClickHandler handler, View emptyView, int choiceMode) {
        this.context = context;
        clickHandler = handler;
        this.emptyView = emptyView;
        choiceManager = new ItemChoiceManager(this);
        choiceManager.setChoiceMode(choiceMode);
    }

    @Override
    public ForecastAdapter.ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_TODAY: {
                    layoutId = R.layout.list_item_forecast_today;
                    break;
                }
                case VIEW_TYPE_FUTURE_DAY: {
                    layoutId = R.layout.list_item_forecast;
                    break;
                }
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            view.setFocusable(true);
            return new ForecastAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ForecastAdapter.ForecastAdapterViewHolder holder, int position) {
        cursor.moveToPosition(position);
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int viewType = getItemViewType(position);
        boolean useLongToday = false;

        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                holder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
                useLongToday = true;
                break;

            }
            case VIEW_TYPE_FUTURE_DAY: {
                holder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
                useLongToday = false;
                break;

            }
        }

        ViewCompat.setTransitionName(holder.iconView,"iconView" + position);

        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);

        holder.dateView.setText(Utility.getFriendlyDayString(context, dateInMillis,useLongToday));
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

        holder.descriptionView.setText(description);
        holder.descriptionView.setContentDescription(context.getString(R.string.a11y_forecast, description));

        holder.iconView.setContentDescription(description);

        String high = Utility.formatTemperature(context, cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP));
        holder.highTempView.setText(high);
        holder.highTempView.setContentDescription(context.getString(R.string.a11y_high_temp, high));

        String low = Utility.formatTemperature(context, cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));
        holder.lowTempView.setText(low);
        holder.lowTempView.setContentDescription(context.getString(R.string.a11y_low_temp, low));

        choiceManager.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && this.useTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }

        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
        emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        this.useTodayLayout = useTodayLayout;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        choiceManager.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        choiceManager.onSaveInstanceState(outState);
    }


    public int getSelectedItemPosition() {
        return choiceManager.getSelectedItemPosition();

    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof ForecastAdapterViewHolder ) {
            ForecastAdapterViewHolder vfh = (ForecastAdapterViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }
}
