package com.skyblue.room_database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skyblue.room_database.model.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)


    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note_table ORDER BY id DESC")        // all notes get
    fun getAllNotes(): LiveData<List<Note>>


    @Query("UPDATE note_table set title = :title , note = :note WHERE id = :id")
    suspend fun updateNote(id: Int?, title: String?, note: String?)
}