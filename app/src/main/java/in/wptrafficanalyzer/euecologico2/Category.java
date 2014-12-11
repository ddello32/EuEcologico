package in.wptrafficanalyzer.euecologico2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class Category implements Parcelable {

    private String title;
    private String snippet;
    private int icon;
    private boolean visibility;

    public Category() {

    }

    public Category(String title, String snippet, int icon, boolean visibility) {
        this.title = title;
        this.snippet = snippet;
        this.icon = icon;
        this.visibility = visibility;
    }

    public Category(Category other) {
        title = other.title;
        snippet = other.snippet;
        icon = other.icon;
        visibility = other.visibility;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public int getIcon() {
        return icon;
    }

    public boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public static final Parcelable.Creator<Category> CREATOR = new Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            Category category = new Category();
            category.title = source.readString();
            category.snippet = source.readString();
            category.icon = source.readInt();
            category.visibility = source.readByte() != 0;
            return category;
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(title);
        parcel.writeString(snippet);
        parcel.writeInt(icon);
        parcel.writeByte((byte) (visibility ? 1 : 0));
    }
}
