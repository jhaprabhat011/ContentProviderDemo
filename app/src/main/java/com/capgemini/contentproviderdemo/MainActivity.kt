package com.capgemini.contentproviderdemo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.provider.ContactsContract.CommonDataKinds.*

class MainActivity : AppCompatActivity() {

    lateinit var detailsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        detailsTextView = findViewById(R.id.tv)
    }

    fun buttonClick(view: View) {
        when(view.id){
            R.id.contactB -> {
                // launch contact app
                val i = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(i, 1)
            }
            R.id.callB -> {
                val i = Intent(this, CallLogActivity::class.java)
                startActivity(i)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1){
            when(resultCode){
                RESULT_OK -> {
                    val selectedContact = data?.dataString ?: ""
                    detailsTextView.text = selectedContact
                    getContactDetails(data?.data)

                }
                RESULT_CANCELED ->{
                    Toast.makeText(this, "No Contact selected", Toast.LENGTH_LONG).show()
                    detailsTextView.text = "No Contact Picked"
                }
            }
        }
    }

    private fun getContactDetails(selectedC: Uri?) {
        val contactId = selectedC?.lastPathSegment
        Log.d("MainActivity", "Contact ID: $contactId")

        // query contact db - name and number of contact
        val args = arrayOf(contactId)
        val resultC = contentResolver.query(Phone.CONTENT_URI,
            null, "${Phone.CONTACT_ID} = ?", args, null )

        if(resultC != null && resultC.count > 0 ){
            resultC.moveToFirst()
            val idxName = resultC.getColumnIndex(Phone.DISPLAY_NAME)
            val idxNum = resultC.getColumnIndex(Phone.NUMBER)

            var numTocall = ""
            do {
                val name = resultC.getString(idxName)
                val number = resultC.getString(idxNum)
                detailsTextView.append("\n$name : $number")
                numTocall = number

            }while (resultC.moveToNext())

            val i = Intent(Intent.ACTION_CALL, Uri.parse("tel:$numTocall"))
            startActivity(i)
        }
        else
        {
            Toast.makeText(this, "No Contact number found", Toast.LENGTH_LONG).show()
        }

    }


}