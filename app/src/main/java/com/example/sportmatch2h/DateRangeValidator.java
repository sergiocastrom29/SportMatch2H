package com.example.sportmatch2h;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.material.datepicker.CalendarConstraints;

public class DateRangeValidator implements CalendarConstraints.DateValidator {

    private final long minDate;
    private final long maxDate;

    public DateRangeValidator(long minDate, long maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    protected DateRangeValidator(Parcel in) {
        minDate = in.readLong();
        maxDate = in.readLong();
    }

    @Override
    public boolean isValid(long date) {
        return date >= minDate && date <= maxDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(minDate);
        dest.writeLong(maxDate);
    }

    public static final Parcelable.Creator<DateRangeValidator> CREATOR =
            new Parcelable.Creator<DateRangeValidator>() {
                @Override
                public DateRangeValidator createFromParcel(Parcel in) {
                    return new DateRangeValidator(in);
                }

                @Override
                public DateRangeValidator[] newArray(int size) {
                    return new DateRangeValidator[size];
                }
            };
}
