package org.pltw.examples.triptracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by klaidley on 4/13/2015.
 */
public class TripListFragment extends ListFragment {

    private static final String TAG = "TripListFragment";
    private ArrayList<Trip> mTrips;
    private boolean mPublicView = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //get the value of mPublicView from the intent. By default, it will be set to false (the second parameter in the call below)
        mPublicView = getActivity().getIntent().getBooleanExtra(Trip.EXTRA_TRIP_PUBLIC_VIEW, false);

        //set the screen title to either My Trips or Public Trips
        if (mPublicView)
            getActivity().setTitle(R.string.action_public_trips);
        else
            getActivity().setTitle(R.string.action_my_trips);

        //Create the list of trips
        mTrips = new ArrayList<Trip>();
        refreshTripList();

        //Create the Adapter that will control the ListView for the fragment
        //The adapter is responsible for feeding the data to the list view
        TripAdapter adapter = new TripAdapter(mTrips);
        setListAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_trip_list, parent, false);

        //register the context menu
        ListView listView = (ListView) v.findViewById(android.R.id.list);
        registerForContextMenu(listView);

        // todo: Activity 3.1.8

        return v;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        // get the org.pltw.examples.triptracker.Trip
        Trip trip = (Trip) (getListAdapter()).getItem(position);

        // start an instance of TripActivity
        // pass parameters using the intent object: all the object attributes of the trip to be viewed/edited.
        // if we do not pass these in as parameters, then we will have to add code in the
        // org.pltw.examples.triptracker.Trip screen to get the data from the back-end database.
        // But that will be more network traffic (slower!) Therefore, when we can avoid an extra trip to the server, we do that!
        Intent intent = new Intent(getActivity(), TripActivity.class);
        intent.putExtra(Trip.EXTRA_TRIP_ID, trip.getObjectId());
        intent.putExtra(Trip.EXTRA_TRIP_NAME, trip.getName());
        intent.putExtra(Trip.EXTRA_TRIP_DESC, trip.getDescription());
        intent.putExtra(Trip.EXTRA_TRIP_START_DATE, trip.getStartDate());
        intent.putExtra(Trip.EXTRA_TRIP_END_DATE, trip.getEndDate());
        intent.putExtra(Trip.EXTRA_TRIP_PUBLIC, trip.isShared());
        intent.putExtra(Trip.EXTRA_TRIP_PUBLIC_VIEW, mPublicView);


        // todo: Activity 3.1.5
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        getActivity().getMenuInflater().inflate(R.menu.menu_trip_list_item_context, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        TripAdapter adapter = (TripAdapter) getListAdapter();
        Trip trip = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_trip:
                //delete the trip from the baas
                deleteTrip(trip);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_trips, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // todo: Activity 3.1.8

                //refresh the list of trips
                refreshTripList();
                return true;

            case R.id.action_new:
                //start the TripActivity and send it the trip_id value of 0, indicating a new trip to be added
                intent = new Intent(getActivity(), TripActivity.class);
                intent.putExtra(Trip.EXTRA_TRIP_ID, "0");
                //To navigate the user back to the same list view (Public Trips or My Trips) after saving the new trip, send the PUBLIC_VIEW as an intent extra
                intent.putExtra(Trip.EXTRA_TRIP_PUBLIC_VIEW, mPublicView);
                startActivity(intent);
                return true;

            // todo: Activity 3.1.6

            case R.id.action_logout:
                // Logs user out and  resets Backendless CurrentUser to null
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void v) {
                        boolean isValidLogin = Backendless.UserService.isValidLogin();
                        if (!isValidLogin) {
                            Log.i(TAG, "Successful logout");
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.i(TAG, "Server reported an error " + backendlessFault.getMessage());
                    }
                });

                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_settings:
                //place holder for settings screen
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class TripAdapter extends ArrayAdapter<Trip> {

        public TripAdapter(ArrayList<Trip> trips) {
            super(getActivity(), 0, trips);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // todo: Activity 3.1.4
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_trip_list_item, null);
            }
            Trip t = getItem(position);

            TextView startDateTextView =
                    (TextView) convertView
                            .findViewById(R.id.trip_list_item_textStartDate);

            TextView nameTextView =
                    (TextView) convertView
                            .findViewById(R.id.trip_list_item_textName);

            nameTextView.setText(t.getName());
            startDateTextView.setText(t.getStartDate().toString());
            return convertView;
        }







    }
    private void refreshTripList() {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        // todo: Activity 3.1.4
        Log.i(TAG, "User = " + user.toString());
        String whereClause = "ownerId = '" + user.getObjectId() + "'";
        DataQueryBuilder query = DataQueryBuilder.create();
        query.setWhereClause(whereClause);
        Backendless.Persistence.of(Trip.class).find(query, new AsyncCallback<List<Trip>>() {
            @Override
            public void handleResponse(List<Trip> trip) {
                mTrips.clear();
                for (Trip t : trip) {
                    if(!mTrips.contains(t)) {
                        mTrips.add(t);
                    }
                }
                ((TripAdapter)getListAdapter()).notifyDataSetChanged();

            }


            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Failed to find trip: " + fault.getMessage());
            }
        });



    }

    @Override
    public void onResume() {
        refreshTripList();
        super.onResume();
    }
    private void deleteTrip(final Trip trip) {

        // todo: Activity 3.1.5
       Thread thread = new Thread(new Runnable() {
           @Override
           public void run() {
               Backendless.Data.of(Trip.class).remove(trip);
           }
       });
       thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "Deleting trip failed: " + e.getMessage());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(e.getMessage());
            builder.setTitle(R.string.trip_error_title);
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        refreshTripList();
    }
}