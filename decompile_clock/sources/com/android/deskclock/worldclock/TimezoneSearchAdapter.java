package com.android.deskclock.worldclock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.deskclock.R;
import com.android.deskclock.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import miuix.appcompat.app.AppCompatActivity;

/* JADX INFO: loaded from: classes.dex */
public class TimezoneSearchAdapter extends RecyclerView.Adapter implements SectionIndexer {
    private AppCompatActivity mActivity;
    private int[] mPositions;
    private String[] mSections;
    private ArrayList<CityObj> mTimezones;
    private String CITY_ID_OF_TAIPEI = "C136";
    private String CITY_ID_OF_HK = "C115";
    private String CITY_ID_OF_MACAU = "C124";

    public interface AddDataToListener {
        void addData(String str);
    }

    public interface InsertDataListener {
        void insertData(String str);
    }

    public TimezoneSearchAdapter(AppCompatActivity appCompatActivity) {
        this.mActivity = appCompatActivity;
    }

    public void setData(ArrayList<CityObj> arrayList) {
        this.mTimezones = arrayList;
    }

    public void setSections(String[] strArr, int[] iArr) {
        this.mSections = strArr;
        this.mPositions = iArr;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new TimezoneViewHolder(LayoutInflater.from(this.mActivity).inflate(R.layout.timezone_search_item, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof TimezoneViewHolder) {
            TimezoneViewHolder timezoneViewHolder = (TimezoneViewHolder) viewHolder;
            final CityObj cityObj = this.mTimezones.get(i);
            timezoneViewHolder.timezoneName.setText(cityObj.mCityName);
            timezoneViewHolder.timezoneInfo.setText(cityObj.getTimezoneDisplay());
            String str = cityObj.mCountryName + " ";
            if (Util.isInternational() && (cityObj.mCityId.equals(this.CITY_ID_OF_MACAU) || cityObj.mCityId.equals(this.CITY_ID_OF_HK) || cityObj.mCityId.equals(this.CITY_ID_OF_TAIPEI))) {
                str = "";
            }
            timezoneViewHolder.countryInfo.setText(str);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.deskclock.worldclock.TimezoneSearchAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (TimezoneSearchAdapter.this.mActivity instanceof InsertDataListener) {
                        ((InsertDataListener) TimezoneSearchAdapter.this.mActivity).insertData(cityObj.mCityId);
                    } else {
                        ((AddDataToListener) TimezoneSearchAdapter.this.mActivity).addData(cityObj.mCityId);
                    }
                }
            });
        }
    }

    public void clearData() {
        this.mTimezones = null;
        notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        ArrayList<CityObj> arrayList = this.mTimezones;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    @Override // android.widget.SectionIndexer
    public Object[] getSections() {
        return this.mSections;
    }

    @Override // android.widget.SectionIndexer
    public int getPositionForSection(int i) {
        int[] iArr = this.mPositions;
        if (iArr == null) {
            return 0;
        }
        if (i < 0 || i >= iArr.length) {
            return iArr[0];
        }
        return iArr[i];
    }

    @Override // android.widget.SectionIndexer
    public int getSectionForPosition(int i) {
        ArrayList<CityObj> arrayList;
        if (this.mSections == null || this.mPositions == null || (arrayList = this.mTimezones) == null || i < 0 || i >= arrayList.size()) {
            return 0;
        }
        int iBinarySearch = Arrays.binarySearch(this.mPositions, i);
        return iBinarySearch >= 0 ? iBinarySearch : (-iBinarySearch) - 2;
    }

    private class TimezoneViewHolder extends RecyclerView.ViewHolder {
        TextView countryInfo;
        TextView timezoneInfo;
        TextView timezoneName;

        public TimezoneViewHolder(View view) {
            super(view);
            this.timezoneName = (TextView) view.findViewById(R.id.timezone_name);
            this.timezoneInfo = (TextView) view.findViewById(R.id.timezone_info);
            this.countryInfo = (TextView) view.findViewById(R.id.country_info);
        }
    }
}
