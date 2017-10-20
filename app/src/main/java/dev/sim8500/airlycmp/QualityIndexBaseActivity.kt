package dev.sim8500.airlycmp

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.util.Log
import rx.Subscriber
import java.lang.ref.WeakReference

/**
 * Created by sbernad on 20/10/2017.
 */
abstract class QualityIndexBaseActivity : AppCompatActivity() {

    public class MeasurementsSub constructor(activity: QualityIndexBaseActivity) : Subscriber<GiosQualityIndexModel>() {
        internal var activityRef: WeakReference<QualityIndexBaseActivity>

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

    open fun onMeasurementsLoaded(model: GiosQualityIndexModel?) {

        val isModelValid = (model?.pm25IndexLevel != null || model?.pm10IndexLevel != null) ?: false

        if (isModelValid) {
            onValidMeasurementsLoaded(model!!)
        }
    }

    protected fun calculateFinalIndex(model: GiosQualityIndexModel?): Int {
        return Math.max(model?.pm10IndexLevel?.id ?: -1, model?.pm25IndexLevel?.id ?: -1)
    }

    abstract fun onValidMeasurementsLoaded(model : GiosQualityIndexModel)
}