package in.wptrafficanalyzer.euecologico2;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class CategoryPositions extends ArrayList<Position> implements Parcelable {

    @Override
    public boolean contains (Object obj) {
        for (Position position : this) {
            if (position.equals(obj)) {
                return true;
            }
        }
        return false;
    }

    public CategoryPositions() {
    }

    @SuppressWarnings("unused")
    public CategoryPositions(Parcel in) {
        this();
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        this.clear();

        // First we have to read the list size
        int size = in.readInt();

        for (int i = 0; i < size; i++) {
            Position position = new Position(in.readString(), in.readString());
            this.add(position);
        }
    }

    public int describeContents() {
        return 0;
    }

    public final Parcelable.Creator<CategoryPositions> CREATOR = new Parcelable.Creator<CategoryPositions>() {
        public CategoryPositions createFromParcel(Parcel in) {
            return new CategoryPositions(in);
        }

        public CategoryPositions[] newArray(int size) {
            return new CategoryPositions[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        int size = this.size();

        // We have to write the list size, we need him recreating the list
        dest.writeInt(size);

        for (Position position : this) {
            dest.writeString(position.getFirst());
            dest.writeString(position.getSecond());
        }
    }
}