package com.skyblue.room_database.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.skyblue.room_database.R
import com.skyblue.room_database.adapter.NoteAdapter
import com.skyblue.room_database.databinding.ActivityMainBinding
import com.skyblue.room_database.model.Note
import com.skyblue.room_database.room.NoteDatabase
import com.skyblue.room_database.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.Collections
import java.util.Random
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var context: Context = this@MainActivity
    private lateinit var database : NoteDatabase
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NoteAdapter
    lateinit var selectedNote : Note

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){

            val note = result.data?.getSerializableExtra("note") as? Note
            if (note != null){
                viewModel.updateNote(note)
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        viewModel  = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)

        viewModel.allnotes.observe(this){list ->
            list?.let {
                adapter.updateList(list)
            }
        }
        database = NoteDatabase.getDatabase(this)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //   val position = viewHolder.adapterPosition
                //   adapter.deleteItem(position)

                val deletedNotes: Note =
                    adapter.getItems().get(viewHolder.adapterPosition)


                val position = viewHolder.adapterPosition
                adapter.deleteItem(position)

                viewModel.deleteNote(deletedNotes)

                Snackbar.make(binding.recyclerView,
                    getString(R.string.deleted) , Snackbar.LENGTH_LONG)
                    .setAction(
                        getString(R.string.undo)
                    ) {
                        //     postList.add(position, deletedEmi)
                        //   postAdapter.notifyItemInserted(position)
                        adapter.addItem(position, deletedNotes)
                        adapter.notifyItemInserted(position)
                        //      viewModel.updateNote(deletedNotes)
                    }.show()
            }
        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun initUI() {
        binding.recyclerView.setHasFixedSize(true)
        // binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NoteAdapter(this,this)
        binding.recyclerView.adapter = adapter

        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK){

                val note = result.data?.getSerializableExtra("note") as? Note
                if (note != null){
                    viewModel.insertNote(note)
                }

            }
        }
        binding.noteCreateBtn.setOnClickListener {
            val intent = Intent(this,AddNote::class.java)
            getContent.launch(intent)
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                ////////// call function filter List
                if (newText != null){
                    adapter.filterList(newText)
                }
                return true
            }

        })
    }

    fun onItemClicked(note: Note) {
        val intent = Intent(this@MainActivity,AddNote::class.java)
        intent.putExtra("current_note" , note)
        updateNote.launch(intent)
    }

    fun onLongItemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {
        val popUp = PopupMenu(this,cardView)
        popUp.inflate(R.menu.pop_menu)
        popUp.show()
    }

    fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.deleteItem) {
            viewModel.deleteNote(selectedNote)
            return true
        }
        return false
    }
}