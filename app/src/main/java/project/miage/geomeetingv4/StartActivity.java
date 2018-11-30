package project.miage.geomeetingv4;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class StartActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private AddMeetingFragment addMeetingFragment;
    private SendRequestFragment sendRequestFragment;
    private RecentMeetingFragment recentMeetingFragment;
    private Fragment mainFragment;

    private int REQUEST_PERMISSION = 3;

    private static final int RECENT_MENU_FRAG = 0;
    private static final int NEW_MENU_FRAG = 1;
    private static final int HISTORY_MENU_FRAG = 2;
    private static final int SEND_REQUEST_FRAG = 3;

    private TextView displayDate;
    private Button pickdateBtn;
    private Button  pickTime;
    private GoogleMap googleMap;

    int PLACE_PICKER_REQUEST = 1;
    private LocationManager locationManager;
    private android.location.Location location;
    private Location userPosition;
    private LatLng meetingPos;

    private Menu menu;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_recent:

                    return true;
                case R.id.navigation_ajouter:
                    showFragment(NEW_MENU_FRAG);
                    return true;
                case R.id.navigation_historique:

                    return true;
            }
            return false;
        }
    };
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        if (id == R.id.action_addContact) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_start);


        pickdateBtn = (Button) findViewById(R.id.pickdateBtn);
        pickTime = (Button) findViewById(R.id.pickTime);

        mainFragment = getSupportFragmentManager().findFragmentById(R.id.main_frame_layout);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    public void showFragment(int Fragmentid){

        switch (Fragmentid){

            case RECENT_MENU_FRAG :
                this.showRecentMenuFrag();
                break;
            case NEW_MENU_FRAG:
                this.showAddMenuFrag();
                break;
            case HISTORY_MENU_FRAG:

                break;

            default:
                break;
        }
    }

    public void showAddMenuFrag(){
        if (this.addMeetingFragment == null) this.addMeetingFragment = AddMeetingFragment.newInstance();
        this.startTransactionFragment(addMeetingFragment);
    }

    public void showRecentMenuFrag(){

    }

    public void showSendRequestFrag(){
        if (this.sendRequestFragment == null) {
            this.sendRequestFragment = SendRequestFragment.newInstance();
        }
        this.startTransactionFragment(sendRequestFragment);

    }

    private void startTransactionFragment(Fragment fragment) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, fragment).commit();
        }
    }

    public void selectDestinataires(View view){
        showSendRequestFrag();
    }
    public void showMapView(View view){
        userPosition = getLastKnownLocation();
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void showDatePickerDialog(View view){
        displayDate = (TextView) findViewById(R.id.displayDateTime);
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            displayDate.setText("Le " + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year );
                            if (dayOfMonth == Calendar.DAY_OF_MONTH) {
                                displayDate.setText("Aujourd'hui");
                            }

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
    }

    public void showTimePicker(View view){
        displayDate = (TextView) findViewById(R.id.displayDateTime);
        Calendar c = Calendar.getInstance();
        c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        displayDate.setText(displayDate.getText()+" à "+hourOfDay + ":" + minute);

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                meetingPos = place.getLatLng();

                googleMap = new AddMeetingFragment().getGoogleMap();
                Log.i("**** TEST GOOGLE MAP ", googleMap.toString());

                Toast.makeText(this, toastMsg, LENGTH_LONG).show();
            }
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }



}
