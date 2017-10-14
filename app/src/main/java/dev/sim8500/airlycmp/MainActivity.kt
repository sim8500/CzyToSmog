package dev.sim8500.airlycmp

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView

import java.lang.ref.WeakReference
import java.util.ArrayList

import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class MainActivity : AppCompatActivity(), View.OnClickListener {

    protected var measureFields: MutableList<StationStatusView> = ArrayList(5)
    protected var currentIndex = 0
    protected var clickedIndex = 0
    protected var qualityIndexModels : Array<GiosQualityIndexModel?> = arrayOfNulls(5)

    protected val stationsArray = intArrayOf(R.integer.STATION_KTW, R.integer.STATION_GLC, R.integer.STATION_KRK, R.integer.STATION_KKZ, R.integer.STATION_WRO)

    protected val namesArray = intArrayOf(R.string.STATION_KTW, R.string.STATION_GLC, R.string.STATION_KRK, R.string.STATION_KKZ, R.string.STATION_WRO)

    protected var currentColorIndex : Int? = null;

    protected class MeasurementsSub constructor(activity: MainActivity) : Subscriber<GiosQualityIndexModel>() {
        internal var activityRef: WeakReference<MainActivity>

        init {
            activityRef = WeakReference(activity)

        }

        override fun onCompleted() {
            Log.d("MainActivity", "onCompleted()")

        }

        override fun onError(e: Throwable) {
            Log.e("MainActivity", e.toString())
        }

        override fun onNext(model: GiosQualityIndexModel) {
                activityRef.get()?.onMeasurementsLoaded(model)
        }
    }

    protected class SensorInfoSub constructor(activity: MainActivity) : Subscriber<List<GiosSensorInfoModel>>() {
        internal var activityRef: WeakReference<MainActivity>

        init {
            activityRef = WeakReference(activity)

        }

        override fun onCompleted() {
            Log.d("MainActivity", "onCompleted()")

        }

        override fun onError(e: Throwable) {
            Log.e("MainActivity", e.toString())
        }

        override fun onNext(model: List<GiosSensorInfoModel>) {
            activityRef.get()?.onSensorInfoLoaded(model)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        measureFields.clear()

        var mfields : MutableList<StationStatusView?> = mutableListOf();
        mfields.add(this.findViewById(R.id.measureField1) as? StationStatusView)
        mfields.add(this.findViewById(R.id.measureField2) as? StationStatusView)
        mfields.add(this.findViewById(R.id.measureField3) as? StationStatusView)
        mfields.add(this.findViewById(R.id.measureField4) as? StationStatusView)
        mfields.add(this.findViewById(R.id.measureField5) as? StationStatusView)

        measureFields.addAll(elements = mfields.requireNoNulls())
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onResume() {
        super.onResume()
        for (i in measureFields.indices) {
            measureFields[i].nameTextView.text = getString(namesArray[i])
            measureFields[i].statusTextView.text = "-"
            measureFields[i].statusTextView.setBackgroundColor(Color.WHITE)
            measureFields[i].setOnClickListener(null)
        }
        continueLoading(true)
    }

    protected fun continueLoading(first: Boolean) {
        if (currentIndex >= stationsArray.size) {
            currentIndex = 0
        } else if (!first) {
            ++currentIndex
        }

        if(currentIndex == stationsArray.size) {
            return
        }

        measureFields[currentIndex].nameTextView.setBackgroundColor(getColor(R.color.colorHighlight))

        RequestsManager.getInstance()
                .getGiosObservableService(GiosDataService::class.java)
                .getQualityIndexDataFor(resources.getInteger(stationsArray[currentIndex]))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(MeasurementsSub(this@MainActivity))
    }

    fun onMeasurementsLoaded(model: GiosQualityIndexModel?) {

        if (currentIndex < measureFields.size) {
            Log.d("MainActivity", String.format("onMeasurementsLoaded for %d", currentIndex))
            qualityIndexModels[currentIndex] = model
            val ssv = measureFields[currentIndex]
            ssv.setOnClickListener(this)

            val isModelValid = (model?.pm25IndexLevel != null && model?.pm10IndexLevel != null) ?: false

            if (isModelValid) {
                val finalIndex = calculateFinalIndex(model)
                ssv.statusTextView.setBackgroundColor(QualityIndexHelper.getQualityIndexColor(this, finalIndex))
                ssv.statusTextView.text = finalIndex.toString()
            }
            continueLoading(false)
        }

    }

    fun onSensorInfoLoaded(model: List<GiosSensorInfoModel>?) {
        if(model != null && clickedIndex != -1) {
            var dialog = StationDetailsDialog()
            var dialogModel = GiosStationDetailsModel(sensors = model, paramIndices = qualityIndexModels[clickedIndex], name = this.getString(namesArray[clickedIndex]))
            dialog.prepareDialog(this, dialogModel)

            dialog.show()
        }

    }

    protected fun calculateFinalIndex(model: GiosQualityIndexModel?): Int {
        return Math.max(model?.pm10IndexLevel?.id ?: -1, model?.pm25IndexLevel?.id ?: -1)
    }


    override fun onClick(v: View?) {
        var stationStatusView =  v as? StationStatusView

        clickedIndex = namesArray.indexOfFirst { n -> stationStatusView?.nameTextView?.text?.equals(getString(n)) ?: false }

        if(clickedIndex >= 0)
        {
            RequestsManager.getInstance()
                    .getGiosObservableService(GiosDataService::class.java)
                    .getSensorsInfo(resources.getInteger(stationsArray[clickedIndex]))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(SensorInfoSub(this@MainActivity))
        }
    }
}
