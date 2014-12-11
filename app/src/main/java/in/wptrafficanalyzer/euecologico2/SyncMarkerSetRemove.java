package in.wptrafficanalyzer.euecologico2;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.Executor;


public class SyncMarkerSetRemove extends Fragment {

    static interface TaskCallbacks {

        void onRemovePreExecute();

        void onRemoveProgressUpdate(int percent);

        void onRemoveCancelled();

        void onRemovePostExecute(JSONObjectsList result);
    }

    private TaskCallbacks taskCallbacks;
    private RemoveMarkerSetSynchronizer markerSetSynchronizer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        taskCallbacks = (TaskCallbacks) activity;
    }

    public boolean isRunning() {
        if (markerSetSynchronizer.getStatus() == AsyncTask.Status.RUNNING)
            return true;
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markerSetSynchronizer = new RemoveMarkerSetSynchronizer();
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        taskCallbacks = null;
    }

    public void execute(NameValuePairsList[] nameValuePairsList) {
        // Create and execute the background task.
        markerSetSynchronizer = new RemoveMarkerSetSynchronizer ();
        markerSetSynchronizer.execute(nameValuePairsList);
    }


    private class RemoveMarkerSetSynchronizer extends AsyncTask<NameValuePairsList, Integer, JSONObjectsList> {

        @Override
        protected void onPreExecute() {
            if (taskCallbacks != null) {
                taskCallbacks.onRemovePreExecute();
            }
        }

        private JSONObject JSONObject(NameValuePairsList nameValuePairs, JSONObject jObj) {
            try {
                JSONObject jsonObject = new JSONObject();
                if (jObj.getBoolean("success")) {
                    // category
                    int category = Integer.valueOf(nameValuePairs.get(0).getValue());
                    Position position = new Position();
                    // latitude
                    position.setFirst(nameValuePairs.get(1).getValue());
                    // longitude
                    position.setSecond(nameValuePairs.get(2).getValue());
                    // removed
                    boolean removed = jObj.getBoolean("removed");
                    // confirmed
                    boolean confirmed = jObj.getBoolean("confirmed");
                    jsonObject.put("success", true);
                    jsonObject.put("category", category);
                    jsonObject.put("lat", position.getFirst());
                    jsonObject.put("lng", position.getSecond());
                    jsonObject.put("removed", removed);
                    jsonObject.put("confirmed", confirmed);
                } else {
                    jsonObject.put("success", false);
                    jsonObject.put("message", jObj.get("message"));
                }
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        private JSONObject error(String message) {
            try {
                JSONObject jObj = new JSONObject();
                jObj.put("success", false);
                jObj.put("message", message);
                return jObj;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected JSONObjectsList doInBackground(NameValuePairsList... nameValuePairsLists) {

            JSONParser jsonParser = new JSONParser();
            JSONObjectsList jsonObjectsList = new JSONObjectsList();
            // Add MarkerSet.
            for (NameValuePairsList i : nameValuePairsLists) {
                try {
                    JSONObject jObj = jsonParser.makeHttpRequest("http://euecologico2.net16.net/removemarker.php", "POST", i);
                    JSONObject jsonObject = JSONObject(i, jObj);
                    if (jsonObject != null) {
                        jsonObjectsList.add(jsonObject);
                    }
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    jsonObjectsList.add(error("Connection timed out - please try again"));
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    jsonObjectsList.add(error("Socket timed out - please verify your connection and try again"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    jsonObjectsList.add(error("Unsupported encoding"));
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    jsonObjectsList.add(error("Client protocol error"));
                } catch (IOException e) {
                    e.printStackTrace();
                    jsonObjectsList.add(error("Connection error"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonObjectsList.add(error("Internal error."));
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonObjectsList.add(error("Internal error."));
                }
            }
            return jsonObjectsList;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            if (taskCallbacks != null) {
                taskCallbacks.onRemoveProgressUpdate(percent[0]);
            }
        }

        @Override
        protected void onPostExecute(JSONObjectsList result) {
            if (taskCallbacks != null) {
                taskCallbacks.onRemovePostExecute(result);
            }
        }
    }
}
