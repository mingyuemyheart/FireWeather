package com.cxwl.shawn.thunder.dto

import android.os.Parcel
import android.os.Parcelable

/**
 * 气象站
 */
class StationDto : Parcelable {

    var id: String? = null
    var stationName: String? = null
    var lat = 0.0
    var lng = 0.0
    var windDir: String? = null
    var windSpeed: String? = null
    var temp: String? = null
    var pressure: String? = null
    var humidity: String? = null
    var precipitation1h: String? = null
    var datatime: String? = null
    var x = 0f //x轴坐标点
    var y = 0f //y轴坐标点

    constructor(source: Parcel) : this(
    )

    constructor()

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {}

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<StationDto> = object : Parcelable.Creator<StationDto> {
            override fun createFromParcel(source: Parcel): StationDto = StationDto(source)
            override fun newArray(size: Int): Array<StationDto?> = arrayOfNulls(size)
        }
    }
}
