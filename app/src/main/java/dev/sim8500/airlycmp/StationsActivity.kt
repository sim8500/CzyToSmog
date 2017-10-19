package dev.sim8500.airlycmp

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ListView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.lang.ref.WeakReference
import com.google.android.gms.location.LocationServices
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat


/**
 * Created by sbernad on 16/10/2017.
 */
class StationsActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected class StationsSub constructor(activity: StationsActivity) : Subscriber<List<GiosStationDataModel>>() {
        internal var activityRef: WeakReference<StationsActivity>

        init {
            activityRef = WeakReference(activity)

        }

        override fun onCompleted() {
            Log.d("StationsActivity", "onCompleted()")

        }

        override fun onError(e: Throwable) {
            Log.e("StationsActivity", e.toString())
        }

        override fun onNext(model: List<GiosStationDataModel>) {
            activityRef.get()?.onStationsLoaded(model)
        }
    }

    protected var stationsList : MutableList<GiosStationDataModel> = mutableListOf()
    protected var listView : ListView? = null
    protected var stationsAdapter : StationsAdapter? = null
    protected var sortByButton : Button? = null

    protected var googleClient : GoogleApiClient? = null
    protected var lastLocation : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stations)

        listView = this.findViewById(R.id.stationsListView) as? ListView
        sortByButton = this.findViewById(R.id.sortByButton) as? Button

        sortByButton?.setOnClickListener{ v -> onSortByClicked() }
        if (googleClient == null) {
            googleClient = GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
        }

    }

    public override fun onStart() {
        googleClient?.connect()

        super.onStart()

        if(stationsList.isEmpty()) {
            RequestsManager.getInstance()
                    .getGiosObservableService(GiosDataService::class.java)
                    .stations
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(StationsActivity.StationsSub(this@StationsActivity))
        }
    }

    public override fun onStop() {
        googleClient?.disconnect()

        super.onStop()
    }

    fun onStationsLoaded(model : List<GiosStationDataModel>) {
        if(model != null && model.isNotEmpty()) {
            stationsList.clear()
            stationsList.addAll(model)

            stationsAdapter = StationsAdapter(stations = stationsList, activity = this)

            listView?.adapter = stationsAdapter
            listView?.onItemClickListener = stationsAdapter
            stationsAdapter?.notifyDataSetChanged()
        }

    }

    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
                // Display UI and wait for user interaction
            } else {
                ActivityCompat.requestPermissions(
                        this, arrayOf<String>(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                        2)
            }
        } else {
            // permission has been granted, continue as usual
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleClient)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onSortByClicked() {
        if(lastLocation != null) {
            stationsAdapter?.sortByLocation(lastLocation)
        }
    }

    fun onDetailsClicked(list : List<GiosSensorInfoModel>?, name : String?) {
        if(list != null) {
            var dialog = StationDetailsDialog()
            var dialogModel = GiosStationDetailsModel(sensors = list, paramIndices = null, name = name)
            dialog.prepareDialog(this, dialogModel)

            dialog.show()
        }
    }
}