package com.capgemini.contentproviderdemo

import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class CallLogActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var typeSpinner: Spinner
    lateinit var recordListView: ListView

    val callRecords = mutableListOf<String>()
    lateinit var recordAdapter : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_log)

        typeSpinner = findViewById(R.id.spinner)
        recordListView = findViewById(R.id.lv)

        typeSpinner.onItemSelectedListener = this

        recordAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, callRecords)
        recordListView.adapter = recordAdapter
    }


    private fun getCallLog(type: Int){

        // call date, duration, number

        callRecords.clear()
        val args = arrayOf(type.toString())

        val resultC = contentResolver.query(CallLog.Calls.CONTENT_URI, null,
            "${CallLog.Calls.TYPE} = ?", args, null)

        if(resultC != null && resultC.count > 0) {
            resultC.moveToFirst()
            val idxDate = resultC.getColumnIndex(CallLog.Calls.DATE)
            val idxDuration = resultC.getColumnIndex(CallLog.Calls.DURATION)
            val idxNumber = resultC.getColumnIndex(CallLog.Calls.NUMBER)

            do{
                val date = resultC.getLong(idxDate)
                val duration = resultC.getInt(idxDuration)
                val number = resultC.getString(idxNumber)

//                val dateTime = Instant.ofEpochMilli(date.toLong())
//                    .atZone(ZoneId.of("India/Chennai"))
//                val formatted = dateTime.format(DateTimeFormatter
//                    .ofPattern("dd/MM/yyyy HH:mm:ss"))
                val d = Date(date.toLong())

                val cal = Calendar.getInstance()
                cal.time = d
                cal.timeInMillis = date.toLong()

                val record = "Number: $number \nDuration: $duration sec \nDate: ${cal.time}"

                callRecords.add(record)

            }while (resultC.moveToNext())

            Log.d("CallLogActivity","Call Records: $callRecords")

        }
        else
            Toast.makeText(this, "No Records found", Toast.LENGTH_LONG).show()

        recordAdapter.notifyDataSetChanged()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
        when(index) {
            0 -> {
                //outgoing
                getCallLog(CallLog.Calls.OUTGOING_TYPE)
            }
            1 -> {
                // incoming
                getCallLog(CallLog.Calls.INCOMING_TYPE)
            }
            2 -> {
                // missed
                getCallLog(CallLog.Calls.MISSED_TYPE)
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}