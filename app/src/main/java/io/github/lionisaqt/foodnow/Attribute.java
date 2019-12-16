package io.github.lionisaqt.foodnow;

import android.os.Parcel;
import android.os.Parcelable;

public class Attribute implements Parcelable {
    public final String name;
    public boolean isChecked;

    public Attribute(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    protected Attribute(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Attribute> CREATOR = new Creator<Attribute>() {
        @Override
        public Attribute createFromParcel(Parcel in) {
            return new Attribute(in);
        }

        @Override
        public Attribute[] newArray(int size) {
            return new Attribute[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
