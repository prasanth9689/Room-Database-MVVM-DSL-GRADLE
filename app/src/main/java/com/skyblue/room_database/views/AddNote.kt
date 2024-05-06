package com.skyblue.room_database.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.skyblue.room_database.R
import com.skyblue.room_database.databinding.ActivityAddNoteBinding
import com.skyblue.room_database.model.Note
import com.skyblue.room_database.model.Notes
import com.skyblue.room_database.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Date


class AddNote : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    lateinit var viewModel: NoteViewModel

    @SuppressLint("SimpleDateFormat")
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

          if (validateInputs()){
              save()
          }
        }

        onClick()

        val mCurrentNote = intent.getStringExtra("current_note")

        if (mCurrentNote != null){
            Toast.makeText(this@AddNote, "had intent", Toast.LENGTH_SHORT).show()

            val mTitle =  intent.getStringExtra("title")
            val mNote =  intent.getStringExtra("note")

            Log.e("ll__", "Intent values \n" +
                    mTitle + "\n" +
                                   intent.getStringExtra("note") + "\n" +
                                   intent.getStringExtra("id") + "\n" +
                                   intent.getStringExtra("date") + "\n")

            binding.edtTitle.setText(mTitle)
            binding.edtMessage.setText(mNote)
        }
    }

    private fun onClick() {
        binding.back.setOnClickListener(){
            finish()
        }

    }

    private fun save() {
        val mCurrentNote = intent.getStringExtra("current_note")

        val mSimpleDateFormat = SimpleDateFormat("dd/M/yyyy")
        val currentDate = mSimpleDateFormat.format(Date())

        if (mCurrentNote != null){
            Toast.makeText(this@AddNote, "Updated", Toast.LENGTH_SHORT).show()

            val  note = Notes(intent.getStringExtra("id")?.toInt(), binding.edtTitle.text.toString(), binding.edtMessage.text.toString(), currentDate)
            viewModel.updateNote(note)
        } else{
            Toast.makeText(this@AddNote, "New inserted", Toast.LENGTH_SHORT).show()

            val  note = Note(binding.edtTitle.text.toString(), binding.edtMessage.text.toString(), currentDate)
            viewModel.insertNote(note)

            Toast.makeText(this@AddNote, getString(com.skyblue.room_database.R.string.inserted_success), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun validateInputs(): Boolean {
       val mobile = binding.edtTitle.text.toString()
        val message = binding.edtMessage.text.toString()

        if ("" == mobile) {
            binding.edtTitle.setError(getString(com.skyblue.room_database.R.string.empty_field))
            binding.edtTitle.requestFocus()
            return false
        }
        if ("" == message) {
            binding.edtMessage.setError(getString(com.skyblue.room_database.R.string.empty_field))
            binding.edtMessage.requestFocus()
            return false
        }
        return true
    }
}