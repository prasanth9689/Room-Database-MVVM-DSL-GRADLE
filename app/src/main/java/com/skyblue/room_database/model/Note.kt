package com.skyblue.room_database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @ColumnInfo(name = "title") val title : String?,
    @ColumnInfo(name = "note") val note : String?,
    @ColumnInfo(name = "date") val date : String?
) : java.io.Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}data class Notes(
    @ColumnInfo(name = "id") val id : Int?,
    @ColumnInfo(name = "title") val title : String?,
    @ColumnInfo(name = "note") val note : String?,
    @ColumnInfo(name = "date") val date : String?
)