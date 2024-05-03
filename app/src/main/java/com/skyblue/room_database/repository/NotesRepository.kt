package com.skyblue.room_database.repository

import androidx.lifecycle.LiveData
import com.skyblue.room_database.model.Note
import com.skyblue.room_database.room.NoteDao

class NotesRepository(private val noteDao: NoteDao) {

    val allNotes : LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insertNote(note : Note) {
        noteDao.insertNote(note)
    }
    suspend fun deleteNote(note: Note){
        noteDao.deleteNote(note)
    }

    suspend fun updateNote(note: Note){
        noteDao.updateNote(note.id , note.title , note.note)
    }
}