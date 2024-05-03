package com.skyblue.room_database.views

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.skyblue.room_database.R
import com.skyblue.room_database.databinding.ActivityAddNoteBinding
import com.skyblue.room_database.model.Note
import com.skyblue.room_database.viewmodel.NoteViewModel

class AddNote : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel  = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        binding.checkBtn.setOnClickListener(){
            val  note = Note(binding.edtTitle.text.toString(), binding.edtMessage.text.toString(), "05-02-2024")
            viewModel.insertNote(note)

            Toast.makeText(this@AddNote, getString(R.string.inserted_success), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}