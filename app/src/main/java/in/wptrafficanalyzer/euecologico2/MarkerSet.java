package in.wptrafficanalyzer.euecologico2;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.HashMap;

// Preciso refatorar isso... shame on me

public class MarkerSet {

    GoogleMap map;
    Category[] categories;
    ArrayList<Position> positions;
    HashMap<String, Polygon> polygonList;
    CategoryPositions[] added;
    MarkerList[] markerLists;

    public MarkerSet(GoogleMap map, Category[] categories, CategoryPositions[] added, CategoryPositions[] removed) {
        this.map = map;
        this.categories = categories;
        positions = new ArrayList<Position>();
        markerLists = new MarkerList[categories.length];
        polygonList = new HashMap<String, Polygon>();
        CategoryPositions[] categoryPositions = new CategoryPositions[categories.length];
        for (int i = 0; i < categories.length; i++) {
            markerLists[i] = new MarkerList();
            categoryPositions[i] = new CategoryPositions();
            if (added[i] != null) {
                categoryPositions[i].addAll(added[i]);
            }
            if (removed[i] != null) {
                categoryPositions[i].removeAll(removed[i]);
            }
            if (categoryPositions[i] != null) {
                for (Position j : categoryPositions[i]) {
                    addMarker(i, j);
                }
            }
        }
    }

    public MarkerSet(GoogleMap map, Category[] categories, CategoryPositions[] added, CategoryPositions[] removed, CategoryPositions[] added_not_confirmed, CategoryPositions[] removed_not_confirmed) {
        this.map = map;
        this.categories = categories;
        this.added = new CategoryPositions[categories.length];
        positions = new ArrayList<Position>();
        markerLists = new MarkerList[categories.length];
        polygonList = new HashMap<String, Polygon>();
        CategoryPositions[] categoryPositions = new CategoryPositions[categories.length];
        for (int i = 0; i < categories.length; i++) {
            markerLists[i] = new MarkerList();
            categoryPositions[i] = new CategoryPositions();
            if (added[i] != null) {
                categoryPositions[i].addAll(added[i]);
            }
            if (removed[i] != null) {
                categoryPositions[i].removeAll(removed[i]);
            }
            if (added_not_confirmed[i] != null) {
                categoryPositions[i].addAll(added_not_confirmed[i]);
            }
            if (removed_not_confirmed[i] != null) {
                categoryPositions[i].removeAll(removed_not_confirmed[i]);
            }
            if (categoryPositions[i] != null) {
                for (Position j : categoryPositions[i]) {
                    addMarker(i, j);
                }
            }
        }
        this.added = categoryPositions;
    }

    public void addMarkers(CategoryPositions[] categoryPositions) {
        for (int i = 0; i < categories.length; i++) {
            if (categoryPositions[i] != null) {
                if (added[i] != null) {
                    for (Position position : categoryPositions[i]) {
                        if (!added[i].contains(position)) {
                            Log.d("--------------------------------", "Não Contém, portanto eu vou adicionar!!!");
                        }
                    }
                    added[i].addAll(categoryPositions[i]);
                } else {
                    added[i] = new CategoryPositions();
                    for (Position position : categoryPositions[i]) {
                        addMarker(i, position);
                    }
                    added[i].addAll(categoryPositions[i]);
                }
            }
        }
    }

    public void removeMarkers(CategoryPositions[] categoryPositions) {
        for (int i = 0; i < categories.length; i++) {
            if (categoryPositions[i] != null) {
                if (added[i] != null) {
                    for (Position position : categoryPositions[i]) {
                        if (added[i].contains(position)) {
                            removeMarker(i, new LatLng(Double.parseDouble(position.getFirst()), Double.parseDouble(position.getSecond())));
                        }
                    }
                    added[i].removeAll(categoryPositions[i]);
                }
            }
        }
    }

    public void updateVisibility(boolean[] visibility) {
        for (int i = 0; i < categories.length; i++) {
            if (markerLists[i] != null) {
                for (Marker j : markerLists[i]) {
                    j.setVisible(visibility[i]);
                }
            }
        }
    }

    private LatLng positionToAdjustedLatLng(int i, Position position) {
        double lat = Double.parseDouble(position.getFirst());
        double lng = Double.parseDouble(position.getSecond());
        double adjust = 0.00005;
        switch (i) {
            case 0:
                lat -= adjust - 0.000005;
                break;
            case 1:
                lat += adjust - 0.00002;
                break;
            case 2:
                lat -= 0.000005;
                lng -= adjust - 0.00002;
                break;
            case 3:
                lat -= 0.000005;
                lng += adjust - 0.00002;
                break;
            case 4:
                lat -= adjust - 0.000005;
                lng -= adjust - 0.00002;
                break;
            case 5:
                lat += adjust - 0.00002;
                lng += adjust - 0.00002;
                break;
            case 6:
                lat += adjust - 0.00002;
                lng -= adjust - 0.00002;
                break;
            case 7:
                lat -= adjust - 0.000005;
                lng += adjust - 0.00002;
                break;
            case 8:
                lat -= adjust / 2;
                break;
            case 9:
                lat += adjust / 2 - 0.00002;
                break;
            case 10:
                lng -= adjust / 2;
                break;
            case 11:
                lng += adjust / 2;
                break;
            case 12:
                lat -= adjust / 2;
                lng -= adjust / 2;
                break;
            case 13:
                lat += adjust / 2;
                lng += adjust / 2;
                break;
            case 14:
                lat += adjust / 2;
                lng -= adjust / 2;
                break;
            case 15:
                lat -= adjust / 2;
                lng += adjust / 2;
                break;
            default:
                break;
        }
        return new LatLng(lat, lng);
    }

    private Pair binarySearch(Position position, int i, int n) {
        if (n < i) {
            return new Pair(false, i);
        }
        int mid = i + (n - i) / 2;
        double n1Lat = Double.parseDouble(positions.get(i).getFirst());
        double n1Lng = Double.parseDouble(positions.get(i).getSecond());
        double n2Lat = Double.parseDouble(position.getFirst());
        double n2Lng = Double.parseDouble(position.getSecond());
        if (n1Lat > n2Lat || n1Lat == n2Lat && n1Lng > n2Lng) {
            return binarySearch(position, i, mid - 1);
        } else if (n1Lat < n2Lat || n1Lat == n2Lat && n1Lng < n2Lng) {
            return binarySearch(position, mid + 1, n);
        } else {
            return new Pair(true, i);
        }
    }

    public void addShape(Position position) {
        PolygonOptions polygonOptions = new PolygonOptions()
                .add(new LatLng(Double.parseDouble(position.getFirst()) - 0.00005, Double.parseDouble(position.getSecond()) - 0.00005))
                .add(new LatLng(Double.parseDouble(position.getFirst()) - 0.00005, Double.parseDouble(position.getSecond()) + 0.00005))
                .add(new LatLng(Double.parseDouble(position.getFirst()) + 0.00005, Double.parseDouble(position.getSecond()) + 0.00005))
                .add(new LatLng(Double.parseDouble(position.getFirst()) + 0.00005, Double.parseDouble(position.getSecond()) - 0.00005))
                .strokeColor(Color.parseColor("#8000ff00"))
                .fillColor(Color.parseColor("#8000ff00"));
        Polygon polygon = map.addPolygon(polygonOptions);
        polygonList.put(position.getFirst() + position.getSecond(), polygon);
    }

    public boolean contains(int i, LatLng latLng) {
            for (Marker j : markerLists[i]) {
                if (j.getPosition().equals(latLng)) {
                    return true;
                }
            }
        return false;
    }

    public void addMarker(int i, Position position) {

        if (positions.isEmpty()) {
            addShape(position);
            positions.add(position);
        } else {
            double n1Lat = Double.parseDouble(positions.get(0).getFirst());
            double n1Lng = Double.parseDouble(positions.get(0).getSecond());
            double n2Lat = Double.parseDouble(position.getFirst());
            double n2Lng = Double.parseDouble(position.getSecond());
            if (positions.size() == 1) {
                if (n1Lat == n2Lat && n1Lng == n2Lng) {
                    positions.add(position);
                } else if (n1Lat > n2Lat || n1Lat == n2Lat && n1Lng > n2Lng) {
                    addShape(position);
                    positions.add(0, position);
                } else {
                    addShape(position);
                    positions.add(1, position);
                }
            } else {
                Pair pair = binarySearch(position, 0, positions.size() - 1);
                positions.add(pair.getSecond(), position);
                if (!pair.getFirst()) {
                    addShape(position);
                }
            }
        }
        // Add marker
        MarkerOptions markerOptions = new MarkerOptions()
                .position(positionToAdjustedLatLng(i, position))
                .title(categories[i].getTitle())
                .snippet(categories[i].getSnippet())
                .icon(BitmapDescriptorFactory.fromResource(categories[i].getIcon()));
        Marker marker = map.addMarker(markerOptions);
        markerLists[i].add(marker);
        marker.setVisible(categories[i].getVisibility());
    }


    public void removeMarker(int i, LatLng latLng) {
        Marker temp = null;
        for (Marker j : markerLists[i]) {
            if (j.getPosition().equals(latLng)) {
                temp = j;
                break;
            }
        }
        if (temp != null) {
            temp.remove();
            markerLists[i].remove(temp);
            // Verify and remove the shape.
            // Convert.
            Position position = new Position(Double.toString(Utilities.round(latLng.latitude, 4)), Double.toString(Utilities.round(latLng.longitude, 4)));
            double n1Lat = Double.parseDouble(positions.get(0).getFirst());
            double n1Lng = Double.parseDouble(positions.get(0).getSecond());
            double n2Lat = Double.parseDouble(position.getFirst());
            double n2Lng = Double.parseDouble(position.getSecond());
            // Remove the position.
            if (positions.size() == 1) {
                positions.remove(0);
            } else {
                Pair pair = binarySearch(position, 0, positions.size() - 1);
                positions.remove(pair.getSecond());
            }
            // Verify and remove the shape.
            if (positions.isEmpty()) {
                Polygon polygon = polygonList.get(position.getFirst() + position.getSecond());
                polygon.remove();
                polygonList.remove(position.getFirst() + position.getSecond());
            } else {
                if (positions.size() == 1 && n1Lat != n2Lat && n1Lng != n2Lng) {
                    Polygon polygon = polygonList.get(position.getFirst() + position.getSecond());
                    polygon.remove();
                    polygonList.remove(position.getFirst() + position.getSecond());
                } else {
                    Pair pair = binarySearch(position, 0, positions.size() - 1);
                    if (!pair.getFirst()) {
                        Polygon polygon = polygonList.get(position.getFirst() + position.getSecond());
                        polygon.remove();
                        polygonList.remove(position.getFirst() + position.getSecond());
                    }
                }
            }
        }
    }
}