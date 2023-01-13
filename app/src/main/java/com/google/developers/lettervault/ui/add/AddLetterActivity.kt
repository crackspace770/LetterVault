package com.google.developers.lettervault.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.developers.lettervault.R
import com.google.developers.lettervault.ui.home.HomeActivity
import com.google.developers.lettervault.util.TimePickerFragment
import java.text.SimpleDateFormat
import java.util.*

class AddLetterActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private lateinit var viewModel: AddLetterViewModel
    private lateinit var simpleDate: SimpleDateFormat
    private lateinit var subject: EditText
    private lateinit var addMessage: EditText
    private lateinit var close: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_letter)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.action_add_letter)

        // val letter = intent.getParcelableExtra<Letter>(HABIT) as Habit

        val factory = AddLetterViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this, factory)[AddLetterViewModel::class.java]

        viewModel.saved.observe(this) {
            if (it.getContentIfNotHandled() == true)
                onBackPressed()
            else {
                val message = getString(R.string.no_letter)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }


        close = findViewById(R.id.btnClose)
        close.setOnClickListener {
            val closeIntent = Intent(this, HomeActivity::class.java)
            startActivity(closeIntent)
        }
        simpleDate = SimpleDateFormat("dd mm yyyy, h:mm a", Locale.getDefault())
        supportActionBar?.title = getString(R.string.created_title, simpleDate.format(viewModel.created))

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_save -> {
            //    val subject = findViewById<TextInputEditText>(R.id.edSubject).text.toString()
             //   val content = findViewById<TextInputEditText>(R.id.edContent).text.toString()

                val addSubject = findViewById<TextInputEditText>(R.id.edSubject).text.toString()
                val addMessage = findViewById<TextInputEditText>(R.id.edContent).text.toString()

                if (addMessage.isEmpty()){
                    Toast.makeText(this, "Please fill the message", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.save(addSubject, addMessage, applicationContext)
                    viewModel.saved
                    Toast.makeText(this, "Message is saved", Toast.LENGTH_SHORT).show()
                    val finishIntent = Intent(applicationContext, HomeActivity::class.java)
                    startActivity(finishIntent)
                }
                true
            }
            R.id.action_time -> {
                val dialogFragment = TimePickerFragment()
                dialogFragment.show(supportFragmentManager, "timePicker")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        viewModel.setExpirationTime(hourOfDay, minute)
    }




}