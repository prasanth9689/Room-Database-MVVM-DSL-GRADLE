package com.skyblue.room_database.views

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.skyblue.room_database.R
import com.skyblue.room_database.adapter.NoteAdapter
import com.skyblue.room_database.databinding.ActivityMainBinding
import com.skyblue.room_database.model.Note
import com.skyblue.room_database.room.NoteDatabase
import com.skyblue.room_database.viewmodel.NoteViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var context: Context = this@MainActivity
    private lateinit var database : NoteDatabase
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NoteAdapter
    lateinit var selectedNote : Note

//    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
//        if (result.resultCode == Activity.RESULT_OK){
//
//            val note = result.data?.getSerializableExtra("note") as? Note
//            if (note != null){
//                viewModel.updateNote(note)
//            }
//        }
//    }


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
                              viewModel.insertNote(deletedNotes)
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
        Toast.makeText(this@MainActivity,  "Title :" + note.title, Toast.LENGTH_SHORT).show()

        val mId = note.id.toString()

        val intent = Intent(this@MainActivity,AddNote::class.java)
        intent.putExtra("current_note" , "1")
        intent.putExtra("id", mId)
        intent.putExtra("title", note.title)
        intent.putExtra("note", note.note)
        intent.putExtra("date", note.date)
       startActivity(intent)
    }

    fun onLongItemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {
        val popUp = PopupMenu(this,cardView)
        popUp.inflate(R.menu.pop_menu)

        popUp.setOnMenuItemClickListener({ item ->

            viewModel.deleteNote(selectedNote)
            Toast.makeText(
                this@MainActivity, "Deleted success",
                Toast.LENGTH_SHORT
            ).show()
            true
        })

        popUp.show()
    }

//    fun onMenuItemClick(item: MenuItem?): Boolean {
//        if (item?.itemId == R.id.deleteItem) {
//            viewModel.deleteNote(selectedNote)
//            Log.e("hl__", "hello")
//            return true
//        }
//        return false
//    }

//    private fun onMenuItemClick(item: MenuItem?): Boolean {
//        if (item?.itemId == R.id.deleteItem) {
//            viewModel.deleteNote(selectedNote)
//            return true
//        }
//        return false
//    }
}