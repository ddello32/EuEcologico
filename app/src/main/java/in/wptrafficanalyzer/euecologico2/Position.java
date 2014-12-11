package in.wptrafficanalyzer.euecologico2;

import android.os.Parcel;
import android.os.Parcelable;

public class Position implements Parcelable {
    private String first;
    private String second;

    public Position() {

    }

    public Position(String first, String second) {
        super();
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (((Object) this).getClass() != obj.getClass()) {
            return false;
        }
        final Position other = (Position) obj;
        return (first.equals(other.getFirst()) && second.equals(other.getSecond()));
    }

    public static final Parcelable.Creator<Position> CREATOR = new Parcelable.Creator<Position>() {
        public Position createFromParcel(Parcel source) {
            Position position = new Position();
            position.first = source.readString();
            position.second = source.readString();
            return position;
        }

        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(first);
        parcel.writeString(second);
    }
}