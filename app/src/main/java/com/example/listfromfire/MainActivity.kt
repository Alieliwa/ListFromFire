package com.example.listfromfire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_note.view.*
import kotlinx.android.synthetic.main.edite.*
import kotlinx.android.synthetic.main.edite.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var mRef: DatabaseReference? = null
    var mNoteList:ArrayList<Note>?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = FirebaseDatabase.getInstance()
        mRef = database.getReference("Notes")
        mNoteList = ArrayList()

        btnadd.setOnClickListener {
            showDialogAddNote()
        }


        note_list_view.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
               var notes = mNoteList?.get(position)

                var DSIntent = Intent(this,DataShow::class.java)
                DSIntent.putExtra("Title Key",notes!!.title)
                DSIntent.putExtra("Note Key",notes.note)

                startActivity(DSIntent)
            }
        note_list_view.onItemLongClickListener  =
            AdapterView.OnItemLongClickListener { parent, view, position, id ->
                val alertBuilder = AlertDialog.Builder(this)
                val view = layoutInflater.inflate(R.layout.edite, null)
                alertBuilder.setView(view)
                val alertDialog = alertBuilder.create()
                alertDialog.show()

               var mnote= mNoteList!!.get(position)

                view.edit_title.setText(mnote.title)
                view.edit_note.setText(mnote.note)

               view.btn_update.setOnClickListener {

                   var tittlee=  view.edit_title.text.toString()
                   var noteee =  view.edit_note.text.toString()
                     mRef!!.child(mnote.id.toString())
                       .setValue(Note(mnote.id!!,tittlee,noteee,getCurrentDate()))
                   alertDialog.dismiss()
               }
                view.btn_delet.setOnClickListener {

                    mRef?.child(mnote.id.toString())?.removeValue()
                    alertDialog.dismiss()
                }


                false
            }
    }
    override fun onStart(){
        super.onStart()
        mRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(pO: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                mNoteList?.clear()
                for (n in p0!!.children) {
                    var note = n.getValue(Note::class.java)
                    mNoteList?.add(0,note!!)
                }
                val noteAdapter = NoteAdapter(applicationContext, mNoteList!!)
                note_list_view.adapter = noteAdapter
            }
        })

    }
    fun showDialogAddNote() {
        val alertBuilder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.add_note, null)
        alertBuilder.setView(view)
        val alertDialog = alertBuilder.create()
        alertDialog.show()
        view.add_new_note.setOnClickListener {
            val title = view.txt_title.text.toString()
            val note = view.txt_note.text.toString()

            if (title.isNotEmpty() && note.isNotEmpty()) {
                var id = mRef!!.push().key!! //reference 1
                var myNote = Note(id, title, note, getCurrentDate())
                mRef!!.child(id).setValue(myNote)
                alertDialog.dismiss()
            }
            else {
                Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun getCurrentDate(): String{
        val calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat("EEEE hh:mm a ")
        val strDate = mdformat.format(calendar.time)
        return strDate
    }
}