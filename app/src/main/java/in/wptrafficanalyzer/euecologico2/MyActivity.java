package in.wptrafficanalyzer.euecologico2;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyActivity extends ActionBarActivity implements SyncMarkerSetAdd.TaskCallbacks, SyncMarkerSetRemove.TaskCallbacks, SyncMarkerSetFetchConfirmed.TaskCallbacks, SyncMarkerSetFetchNotConfirmed.TaskCallbacks, SyncMarkerSetFetchRemoved.TaskCallbacks, Filter.OnFragmentInteractionListener, CategoriesAddList.OnFragmentInteractionListener, ConfirmRemoval.OnFragmentInteractionListener {

    private GoogleMap googleMap;
    private Category[] categories;
    private CategoryPositions[] added;
    private CategoryPositions[] removed;
    private CategoryPositions[] added_not_confirmed;
    private CategoryPositions[] removed_not_confirmed;
    private CategoryPositions[] confirmed_temp;
    private CategoryPositions[] not_confirmed_temp;
    private CategoryPositions[] removed_temp;
    private String TAG_ID;
    private MarkerSet markerSet;
    private SyncMarkerSetAdd syncMarkerSetAddFragment;
    private SyncMarkerSetRemove syncMarkerSetRemoveFragment;
    private SyncMarkerSetFetchConfirmed syncMarkerSetFetchConfirmedFragment;
    private SyncMarkerSetFetchNotConfirmed syncMarkerSetFetchNotConfirmedFragment;
    private SyncMarkerSetFetchRemoved syncMarkerSetFetchRemovedFragment;
    private ProgressDialog progressDialog;
    private boolean initialization = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        // Gets reference to SupportMapFragment
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // Creates GoogleMap from SupportMapFragment
        googleMap = fragment.getMap();
        // Enables MyLocation button for the Google Map
        googleMap.setMyLocationEnabled(true);
        // State recover
        if (savedInstanceState == null) {
            // Sets initials zoom and location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-22.8172, -47.0694), 14.0f));
            categories = new Category[]{
                new Category("Reciclável", "Lixo reciclável", R.drawable.recycle, true),
                new Category("Papel", "Descarte de papel", R.drawable.paper, true),
                new Category("Plástico", "Descarte de plástico", R.drawable.plastic, true),
                new Category("Metal", "Descarte de metal", R.drawable.metal, true),
                new Category("Óleo de cozinha", "Coleta de óleo de cozinha", R.drawable.oil, true),
                new Category("Lâmpadas", "Descarte de lâmpadas", R.drawable.light, true),
                new Category("Baterias", "Coleta de pilhas e baterias portateis", R.drawable.battery, true),
                new Category("Eletrônicos", "Descarte de eletrônicos", R.drawable.electronic, true),
                new Category("Cartuchos de impressora", "Coleta de cartuchos de impressora", R.drawable.tonner, true),
                new Category("Livros", "Doação de livros", R.drawable.book, true),
                new Category("Roupas", "Doação de roupas", R.drawable.cloth, true)
            };
            added = new CategoryPositions[categories.length];
            for (int i = 0; i < categories.length; i++)
                added[i] = null;
            removed = new CategoryPositions[categories.length];
            for (int i = 0; i < categories.length; i++)
                removed[i] = null;
            added_not_confirmed = new CategoryPositions[categories.length];
            for (int i = 0; i < categories.length; i++)
                added_not_confirmed[i] = null;
            removed_not_confirmed = new CategoryPositions[categories.length];
            for (int i = 0; i < categories.length; i++)
                removed_not_confirmed[i] = null;
            markerSet = new MarkerSet(googleMap, categories, added, removed, added_not_confirmed, removed_not_confirmed);
            initialization = true;
        }
        else {
            CameraPosition cameraPosition;
            cameraPosition = savedInstanceState.getParcelable("camera");
            // Sets initials zoom and location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom));
            Parcelable[] parcelablesCategories = savedInstanceState.getParcelableArray("categories");
            // Initializes the categories
            categories = new Category[parcelablesCategories.length];
            for (int i = 0; i < parcelablesCategories.length; i++) {
                categories[i] = (Category) parcelablesCategories[i];
            }
            // Initializations of the positions' arrays.
            Parcelable[] parcelablesAdded = savedInstanceState.getParcelableArray("added");
            added = new CategoryPositions[categories.length];
            if (parcelablesAdded != null) {
                for (int i = 0; i < categories.length; i++) {
                    if (parcelablesCategories[i] != null) {
                        added[i] = (CategoryPositions) parcelablesAdded[i];
                    }
                }
            }
            Parcelable[] parcelablesRemoved = savedInstanceState.getParcelableArray("removed");
            removed = new CategoryPositions[categories.length];
            if (parcelablesRemoved != null) {
                for (int i = 0; i < categories.length; i++) {
                    removed[i] = (CategoryPositions) parcelablesRemoved[i];
                }
            }
            Parcelable[] parcelablesAddedNotConfirmed = savedInstanceState.getParcelableArray("added_not_confirmed");
            added_not_confirmed = new CategoryPositions[categories.length];
            if (parcelablesAddedNotConfirmed != null) {
                for (int i = 0; i < categories.length; i++) {
                    added_not_confirmed[i] = (CategoryPositions) parcelablesAddedNotConfirmed[i];
                }
            }
            Parcelable[] parcelablesRemovedNotConfirmed = savedInstanceState.getParcelableArray("removed_not_confirmed");
            removed_not_confirmed = new CategoryPositions[categories.length];
            if (parcelablesRemovedNotConfirmed != null) {
                for (int i = 0; i < categories.length; i++) {
                    removed_not_confirmed[i] = (CategoryPositions) parcelablesRemovedNotConfirmed[i];
                }
            }
            markerSet = new MarkerSet(googleMap, categories, added, removed, added_not_confirmed, removed_not_confirmed);
        }
        // Persistence fragments initialization
        syncMarkerSetAddFragment = (SyncMarkerSetAdd) getSupportFragmentManager().findFragmentByTag("sync_marker_set_add_fragment");
        if (syncMarkerSetAddFragment == null) {
            syncMarkerSetAddFragment = new SyncMarkerSetAdd();
            getSupportFragmentManager().beginTransaction().add(syncMarkerSetAddFragment, "sync_marker_set_add_fragment").commit();
        }
        else {
            if (syncMarkerSetAddFragment.isRunning())
                this.onAddPreExecute();
        }
        syncMarkerSetRemoveFragment = (SyncMarkerSetRemove) getSupportFragmentManager().findFragmentByTag("sync_marker_set_remove_fragment");
        if (syncMarkerSetRemoveFragment == null) {
            syncMarkerSetRemoveFragment = new SyncMarkerSetRemove();
            getSupportFragmentManager().beginTransaction().add(syncMarkerSetRemoveFragment, "sync_marker_set_remove_fragment").commit();
        }
        else {
            if (syncMarkerSetRemoveFragment.isRunning())
                this.onRemovePreExecute();
        }
        syncMarkerSetFetchConfirmedFragment = (SyncMarkerSetFetchConfirmed) getSupportFragmentManager().findFragmentByTag("sync_marker_set_fetch_confirmed_fragment");
        if (syncMarkerSetFetchConfirmedFragment == null) {
            syncMarkerSetFetchConfirmedFragment = new SyncMarkerSetFetchConfirmed();
            getSupportFragmentManager().beginTransaction().add(syncMarkerSetFetchConfirmedFragment, "sync_marker_set_fetch_confirmed_fragment").commit();
        }
        else {
            if (syncMarkerSetFetchConfirmedFragment.isRunning())
                this.onFetchConfirmedPreExecute();
        }
        syncMarkerSetFetchNotConfirmedFragment = (SyncMarkerSetFetchNotConfirmed) getSupportFragmentManager().findFragmentByTag("sync_marker_set_fetch_not_confirmed_fragment");
        if (syncMarkerSetFetchNotConfirmedFragment == null) {
            syncMarkerSetFetchNotConfirmedFragment = new SyncMarkerSetFetchNotConfirmed();
            getSupportFragmentManager().beginTransaction().add(syncMarkerSetFetchNotConfirmedFragment, "sync_marker_set_fetch_not_confirmed_fragment").commit();
        }
        else {
            if (syncMarkerSetFetchNotConfirmedFragment.isRunning())
                this.onFetchNotConfirmedPreExecute();
        }
        syncMarkerSetFetchRemovedFragment = (SyncMarkerSetFetchRemoved) getSupportFragmentManager().findFragmentByTag("sync_marker_set_fetch_removed_fragment");
        if (syncMarkerSetFetchRemovedFragment == null) {
            syncMarkerSetFetchRemovedFragment = new SyncMarkerSetFetchRemoved();
            getSupportFragmentManager().beginTransaction().add(syncMarkerSetFetchRemovedFragment, "sync_marker_set_fetch_removed_fragment").commit();
        }
        else {
            if (syncMarkerSetFetchRemovedFragment.isRunning())
                this.onFetchRemovedPreExecute();
        }
        // Setting OnClickEvent listener for the GoogleMap
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // Converts the LatLng to rounded Position.
                Position position = new Position();
                position.setFirst(Double.toString(Utilities.round(latLng.latitude, 4)));
                position.setSecond(Double.toString(Utilities.round(latLng.longitude, 4)));
                // For each category, verifies if not added.
                boolean[] valid = new boolean[categories.length];
                for (int i = 0; i < categories.length; i++) {
                    if (added[i] == null && removed[i] == null && added_not_confirmed[i] == null) {
                        valid[i] = true;
                    } else {
                        if (added[i] != null && removed[i] == null && added_not_confirmed[i] == null) {
                            if (!added[i].contains(position)) {
                                valid[i] = true;
                            }
                            else {
                            }
                        } else {
                            if (added[i] == null && removed[i] != null && added_not_confirmed[i] == null) {
                                valid[i] = true;
                            } else {
                                if (added[i] == null && removed[i] == null && added_not_confirmed[i] != null) {
                                    if (!added_not_confirmed[i].contains(position)) {
                                        valid[i] = true;
                                    }
                                } else {
                                    if (added[i] != null && removed[i] != null && added_not_confirmed[i] == null) {
                                        if (!added[i].contains(position) || added[i].contains(position) && removed[i].contains(position)) {
                                            valid[i] = true;
                                        }
                                    } else {
                                        if (added[i] != null && removed[i] == null && added_not_confirmed[i] != null) {
                                            if (!added[i].contains(position) && !added_not_confirmed[i].contains(position)) {
                                                valid[i] = true;
                                            }
                                        } else {
                                            if (added == null && removed != null && added_not_confirmed != null) {
                                                if (!added_not_confirmed[i].contains(position)) {
                                                    valid[i] = true;
                                                }
                                            } else {
                                                if (added != null && added_not_confirmed != null && removed != null) {
                                                    if (!added_not_confirmed[i].contains(position) && (!added[i].contains(position) || added[i].contains(position) && removed[i].contains(position))) {
                                                        valid[i] = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Verifies if there are any categories for the given position
                if (Utilities.containsAnyTrues(valid)) {
                    // Shows the categories selection dialog.
                    Category[] categories1 = new Category[categories.length];
                    for (int i = 0; i < categories.length; i++) {
                        categories1[i] = new Category(categories[i]);
                    }
                    CategoriesAddList categoriesAddList = CategoriesAddList.newInstance(categories1, valid, position);
                    getSupportFragmentManager().beginTransaction().add(categoriesAddList, "categories_add_list_fragment").commit();
                } else {
                    Toast.makeText(MyActivity.this, getString(R.string.categories_already_added), Toast.LENGTH_LONG).show();
                }
            }
        });
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                ConfirmRemoval confirmRemoval = ConfirmRemoval.newInstance(Utilities.getCategory(marker, categories), marker.getPosition());
                getSupportFragmentManager().beginTransaction().add(confirmRemoval, "confirm_removal_fragment").commit();
            }
        });
        if (initialization) {
            commit();
        }
    }

    private void commit() {
        // Checks is there is internet connection
        if (Utilities.isNetworkAvailable(this)) {
            TAG_ID = Utilities.getMacAddress(this);
            SyncMarkerSet();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCategoriesAddListInteraction(Position position, boolean[] result) {
        // verifies if the returned object is coherent
        if (result != null) {
            if (Utilities.containsAnyTrues(result)) {
                // adds to the not added list.
                for (int i = 0; i < result.length; i++) {
                    if (result[i]) {
                        //adds to the marker set
                        markerSet.addMarker(i, position);
                        // verifies if the element is contained in the removed_not_confirmed array
                        if (removed_not_confirmed[i] != null) {
                            if (removed_not_confirmed[i].contains(position)) {
                                removed_not_confirmed[i].remove(position);
                            } else {
                                if (added_not_confirmed[i] == null) {
                                    added_not_confirmed[i] = new CategoryPositions();
                                }
                                added_not_confirmed[i].add(position);
                            }
                        } else {
                            if (removed[i] != null) {
                                if (removed[i].contains(position)) {
                                    removed[i].remove(position);
                                } else {
                                    if (added_not_confirmed[i] == null) {
                                        added_not_confirmed[i] = new CategoryPositions();
                                    }
                                    added_not_confirmed[i].add(position);
                                }
                            } else {
                                if (added_not_confirmed[i] == null) {
                                    added_not_confirmed[i] = new CategoryPositions();
                                }
                                added_not_confirmed[i].add(position);
                            }
                        }
                    }
                }
                // try to sync.
                commit();
            }
        }
    }

    @Override
    public void onConfirmRemovalInteraction(LatLng latLng, int i) {
        if (latLng != null) {
            //removes from the marker set
            markerSet.removeMarker(i, latLng);
            // Converts the LatLng to rounded Position.
            Position position = new Position();
            position.setFirst(Double.toString(Utilities.round(latLng.latitude, 4)));
            position.setSecond(Double.toString(Utilities.round(latLng.longitude, 4)));
            // verifies if the element is contained in the added_not_confirmed array
            if (added_not_confirmed[i] != null) {
                if (added_not_confirmed[i].contains(position)) {
                        added_not_confirmed[i].remove(position);
                } else {
                    if (removed_not_confirmed[i] == null) {
                        removed_not_confirmed[i] = new CategoryPositions();
                    }
                    removed_not_confirmed[i].add(position);
                }
            } else {
                if (removed_not_confirmed[i] == null) {
                    removed_not_confirmed[i] = new CategoryPositions();
                }
                removed_not_confirmed[i].add(position);
            }
            // try to sync.
            commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_activity_actions, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml(getString(R.string.eu_ecologico)));
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#ff009900"));
        actionBar.setBackgroundDrawable(colorDrawable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)  {
            case R.id.action_settings:
                return true;
            case R.id.action_update:
                commit();
                return true;
            case R.id.action_filter:
                Filter filter = Filter.newInstance(categories);
                getSupportFragmentManager().beginTransaction().add(filter, "filter").commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFilterInteraction(boolean[] status) {
        if (status != null) {
            for (int i = 0; i < categories.length; i++) {
                markerSet.updateVisibility(status);
                categories[i].setVisibility(status[i]);
            }
        }
    }

    @Override
    public void onResume() {
        // initializes the fragments.
        super.onResume();
        syncMarkerSetAddFragment = (SyncMarkerSetAdd) getSupportFragmentManager().findFragmentByTag("sync_marker_set_add_fragment");
        if (syncMarkerSetAddFragment == null) {
            syncMarkerSetAddFragment = new SyncMarkerSetAdd();
            getSupportFragmentManager().beginTransaction().add(syncMarkerSetAddFragment, "sync_marker_set_add_fragment").commit();
        }
        else {
            if (syncMarkerSetAddFragment.isRunning())
                this.onAddPreExecute();
        }
        syncMarkerSetRemoveFragment = (SyncMarkerSetRemove) getSupportFragmentManager().findFragmentByTag("sync_marker_set_remove_fragment");
        if (syncMarkerSetRemoveFragment == null) {
            syncMarkerSetRemoveFragment = new SyncMarkerSetRemove();
            getSupportFragmentManager().beginTransaction().add(syncMarkerSetRemoveFragment, "sync_marker_set_remove_fragment").commit();
        }
        else {
            if (syncMarkerSetRemoveFragment.isRunning())
                this.onRemovePreExecute();
        }
        syncMarkerSetFetchConfirmedFragment = (SyncMarkerSetFetchConfirmed) getSupportFragmentManager().findFragmentByTag("sync_marker_set_fetch_confirmed_fragment");
        if (syncMarkerSetFetchConfirmedFragment == null) {
            syncMarkerSetFetchConfirmedFragment = new SyncMarkerSetFetchConfirmed();
            getSupportFragmentManager().beginTransaction().add(syncMarkerSetFetchConfirmedFragment, "sync_marker_set_fetch_confirmed_fragment").commit();
        }
        else {
            if (syncMarkerSetFetchConfirmedFragment.isRunning())
                this.onFetchConfirmedPreExecute();
        }
        syncMarkerSetFetchNotConfirmedFragment = (SyncMarkerSetFetchNotConfirmed) getSupportFragmentManager().findFragmentByTag("sync_marker_set_fetch_not_confirmed_fragment");
        if (syncMarkerSetFetchNotConfirmedFragment == null) {
            syncMarkerSetFetchNotConfirmedFragment = new SyncMarkerSetFetchNotConfirmed();
            getSupportFragmentManager().beginTransaction().add(syncMarkerSetFetchNotConfirmedFragment, "sync_marker_set_fetch_not_confirmed_fragment").commit();
        }
        else {
            if (syncMarkerSetFetchNotConfirmedFragment.isRunning())
                this.onFetchNotConfirmedPreExecute();
        }
        syncMarkerSetFetchRemovedFragment = (SyncMarkerSetFetchRemoved) getSupportFragmentManager().findFragmentByTag("sync_marker_set_fetch_removed_fragment");
        if (syncMarkerSetFetchRemovedFragment == null) {
            syncMarkerSetFetchRemovedFragment = new SyncMarkerSetFetchRemoved();
            getSupportFragmentManager().beginTransaction().add(syncMarkerSetFetchRemovedFragment, "sync_marker_set_fetch_removed_fragment").commit();
        }
        else {
            if (syncMarkerSetFetchRemovedFragment.isRunning())
                this.onFetchRemovedPreExecute();
        }
        // initializes the marker set.
        if (markerSet == null) {
            markerSet = new MarkerSet(googleMap, categories, added, removed, added_not_confirmed, removed_not_confirmed);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArray("categories", categories);
        savedInstanceState.putParcelableArray("added", added);
        savedInstanceState.putParcelableArray("removed", removed);
        savedInstanceState.putParcelableArray("added_not_confirmed", added_not_confirmed);
        savedInstanceState.putParcelableArray("removed_not_confirmed", removed_not_confirmed);
        savedInstanceState.putParcelable("camera", googleMap.getCameraPosition());
    }

    public void SyncMarkerSet() {
        // initializations
        confirmed_temp = new CategoryPositions[categories.length];
        not_confirmed_temp = new CategoryPositions[categories.length];
        removed_temp = new CategoryPositions[categories.length];
        // added_not_confirmed
        ArrayList<NameValuePairsList> added_not_confirmed_params_list = new ArrayList<NameValuePairsList>();
        for (int i = 0; i < categories.length; i++) {
            if (added_not_confirmed[i] != null) {
                for (Position j : added_not_confirmed[i]) {
                    NameValuePairsList nameValuePairs = Utilities.buildNameValuePairsList(i, j, TAG_ID);
                    added_not_confirmed_params_list.add(nameValuePairs);
                }
            }
        }
        NameValuePairsList[] nameValuePairsLists = new NameValuePairsList[added_not_confirmed_params_list.size()];
        for (int i = 0; i < nameValuePairsLists.length; i++) {
            nameValuePairsLists[i] = added_not_confirmed_params_list.get(i);
        }
        syncMarkerSetAddFragment.execute(nameValuePairsLists);
    }

    @Override
    public void onAddPreExecute() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.synchronizing_locations));
            progressDialog.setMessage(getString(R.string.synchronizing_with_the_server));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }

    @Override
    public void onAddProgressUpdate(int percent) {

    }

    @Override
    public void onAddCancelled() {

    }

    @Override
    public void onAddPostExecute(JSONObjectsList result) {
        if (result != null) {
            boolean error = false;
            // update
            for (JSONObject i : result) {
                try {
                    if (!i.getBoolean("success")) {
                        error = true;
                        Toast.makeText(this, i.getString("message"), Toast.LENGTH_LONG).show();
                    }
                    else {
                        added_not_confirmed = new CategoryPositions[categories.length];
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_LONG).show();
                }
            }
            if (!error) {
                // resets the added_not_confirmed array
                added_not_confirmed = new CategoryPositions[categories.length];
                for (int i = 0; i < categories.length; i++) {
                    added_not_confirmed[i] = null;
                }
                // removed_not_confirmed
                ArrayList<NameValuePairsList> removed_not_confirmed_params_list = new ArrayList<NameValuePairsList>();
                for (int i = 0; i < categories.length; i++) {
                    if (removed_not_confirmed[i] != null) {
                        for (Position j : removed_not_confirmed[i]) {
                            NameValuePairsList nameValuePairs = Utilities.buildNameValuePairsList(i, j, TAG_ID);
                            removed_not_confirmed_params_list.add(nameValuePairs);
                        }
                    }
                }
                NameValuePairsList[] nameValuePairsLists = new NameValuePairsList[removed_not_confirmed_params_list.size()];
                for (int i = 0; i < nameValuePairsLists.length; i++) {
                    nameValuePairsLists[i] = removed_not_confirmed_params_list.get(i);
                }
                syncMarkerSetRemoveFragment.execute(nameValuePairsLists);
            }
            else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Toast.makeText(this, getString(R.string.error_contacting_the_server), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRemovePreExecute() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.synchronizing_locations));
            progressDialog.setMessage(getString(R.string.synchronizing_with_the_server));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }

    @Override
    public void onRemoveProgressUpdate(int percent) {

    }

    @Override
    public void onRemoveCancelled() {

    }

    @Override
    public void onRemovePostExecute(JSONObjectsList result) {
        if (result != null) {
            boolean error = false;
            // update
            for (JSONObject i : result) {
                try {
                    if (!i.getBoolean("success")) {
                        error = true;
                        Toast.makeText(this, i.getString("message"), Toast.LENGTH_LONG).show();
                    } else {
                        removed_not_confirmed = new CategoryPositions[categories.length];
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_LONG).show();
                }
            }
            if (!error) {
                // resets the removed_not_confirmed array
                removed_not_confirmed = new CategoryPositions[categories.length];
                for (int i = 0; i < categories.length; i++) {
                    removed_not_confirmed[i] = null;
                }
                // id
                NameValuePairsList[] nameValuePairsLists = new NameValuePairsList[1];
                nameValuePairsLists[0] = new NameValuePairsList();
                nameValuePairsLists[0].add(new BasicNameValuePair("id", TAG_ID));
                syncMarkerSetFetchConfirmedFragment.execute(nameValuePairsLists);
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Toast.makeText(this, getString(R.string.error_contacting_the_server), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onFetchConfirmedPreExecute() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.synchronizing_locations));
            progressDialog.setMessage(getString(R.string.synchronizing_with_the_server));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }

    @Override
    public void onFetchConfirmedProgressUpdate(int percent) {

    }

    @Override
    public void onFetchConfirmedCancelled() {

    }

    @Override
    public void onFetchConfirmedPostExecute(JSONObjectsList result) {
        if (result != null) {
            boolean error = false;
            // update
            try {
                if (!result.get(0).getBoolean("success")) {
                    error = true;
                    Toast.makeText(this, result.get(0).getString("message"), Toast.LENGTH_LONG).show();
                } else {
                    confirmed_temp = JObjectParser.CategoryPosition(result.get(0), categories.length);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_LONG).show();
            }
            if (!error) {
                // id
                NameValuePairsList[] nameValuePairsLists = new NameValuePairsList[1];
                nameValuePairsLists[0] = new NameValuePairsList();
                nameValuePairsLists[0].add(new BasicNameValuePair("id", TAG_ID));
                syncMarkerSetFetchNotConfirmedFragment.execute(nameValuePairsLists);
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.error_contacting_the_server), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFetchNotConfirmedPreExecute() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.synchronizing_locations));
            progressDialog.setMessage(getString(R.string.synchronizing_with_the_server));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }

    @Override
    public void onFetchNotConfirmedProgressUpdate(int percent) {

    }

    @Override
    public void onFetchNotConfirmedCancelled() {

    }

    @Override
    public void onFetchNotConfirmedPostExecute(JSONObjectsList result) {
        if (result != null) {
            boolean error = false;
            try {
                if (!result.get(0).getBoolean("success")) {
                    error = true;
                    Toast.makeText(this, result.get(0).getString("message"), Toast.LENGTH_LONG).show();
                } else {
                    not_confirmed_temp = JObjectParser.CategoryPosition(result.get(0), categories.length);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.internal_error), Toast.LENGTH_LONG).show();
            }
            if (!error) {
                // id
                NameValuePairsList[] nameValuePairsLists = new NameValuePairsList[1];
                nameValuePairsLists[0] = new NameValuePairsList();
                nameValuePairsLists[0].add(new BasicNameValuePair("id", TAG_ID));
                syncMarkerSetFetchRemovedFragment.execute(nameValuePairsLists);
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Toast.makeText(this, getString(R.string.error_contacting_the_server), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFetchRemovedPreExecute() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.synchronizing_locations));
            progressDialog.setMessage(getString(R.string.synchronizing_with_the_server));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
    }

    @Override
    public void onFetchRemovedProgressUpdate(int percent) {

    }

    @Override
    public void onFetchRemovedCancelled() {

    }

    @Override
    public void onFetchRemovedPostExecute(JSONObjectsList result) {
        if (result != null) {
            try {
                if (!result.get(0).getBoolean("success")) {
                    Toast.makeText(this, result.get(0).getString("message"), Toast.LENGTH_LONG).show();
                } else {
                    removed_temp = JObjectParser.CategoryPosition(result.get(0), categories.length);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.internal_error),Toast.LENGTH_LONG).show();
            }
            // plot
            added = new CategoryPositions[categories.length];
            for (int i = 0; i < categories.length; i++) {
                added[i] = new CategoryPositions();
                if (confirmed_temp[i] != null) {
                    added[i].addAll(confirmed_temp[i]);
                }
                if (not_confirmed_temp[i] != null) {
                    added[i].addAll(not_confirmed_temp[i]);
                }
            }
            removed = new CategoryPositions[categories.length];
            for (int i = 0; i < categories.length; i++) {
                removed[i] = new CategoryPositions();
                if (removed_temp[i] != null) {
                    removed[i].addAll(removed_temp[i]);
                }
            }
            // creates a new marker set.
            googleMap.clear();
            markerSet = new MarkerSet(googleMap, categories, added, removed);
            Toast.makeText(this, getString(R.string.markers_successfully_updated), Toast.LENGTH_LONG).show();
        } else {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            Toast.makeText(this, getString(R.string.error_contacting_the_server), Toast.LENGTH_LONG).show();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}