package dev.sim8500.airlycmp

import android.content.Context
import android.graphics.Color
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sbernad on 14/10/2017.
 */
object QualityIndexHelper {

    fun getQualityIndexColor(context : Context?, index : Int) : Int {

        if(context != null)
        {
            val colorsArray = context.resources.getStringArray(R.array.level_array)

            if(colorsArray != null && colorsArray.size > 0)
            {
                val finalIndex = Math.max(0, Math.min(colorsArray.size-1, index))
                if(finalIndex >= 0 && finalIndex < colorsArray!!.size) {
                    return Color.parseColor(colorsArray!![finalIndex])
                }
            }
        }

        return Color.WHITE;
    }

    fun getParsedDate(dateString: String): Date? {

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.setTimeZone(TimeZone.getTimeZone("Europe/Warsaw"))
        var resultDate: Date? = null
        try {
            resultDate = sdf.parse(dateString)

        }
        catch (ex: ParseException) {
            Log.e("QualityIndexHelper", "Cannot parse date in given format.")
        }

        return resultDate
    }
}