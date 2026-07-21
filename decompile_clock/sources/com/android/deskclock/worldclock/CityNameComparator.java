package com.android.deskclock.worldclock;

import java.text.Collator;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class CityNameComparator implements Comparator<CityObj> {
    private Collator mCollator = Collator.getInstance();

    @Override // java.util.Comparator
    public int compare(CityObj cityObj, CityObj cityObj2) {
        return this.mCollator.compare(cityObj.mCityName, cityObj2.mCityName);
    }
}
