package com.example.crud.data.model

import android.os.Parcel
import android.os.Parcelable

data class Todo(
    var id: String = "",          // Firebase ID
    var title: String = "",
    var description: String = "",
    var status: String = "pending", // pending, completed, hết_hạn
    var deadline: String="",      // ngày + giờ (format: "24/03/2026 14:30")
    var startTime: Long = 0L,
    var endTime: Long = 0L
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(status)
        parcel.writeString(deadline)
        parcel.writeLong(startTime)
        parcel.writeLong(endTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Todo> {
        override fun createFromParcel(parcel: Parcel): Todo {
            return Todo(parcel)
        }

        override fun newArray(size: Int): Array<Todo?> {
            return arrayOfNulls(size)
        }
    }
}