package com.huawei.appmate.sample

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.huawei.appmate.PurchaseClient
import com.huawei.appmate.callback.ReceivedDataListener
import com.huawei.appmate.model.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storeAccessText: TextView = findViewById(R.id.textStoreAccess)
        getUserId(storeAccessText)

        storeAccessText.setOnClickListener {
            getUserId(storeAccessText)
        }
    }

    // Check SDK availability by getting User ID
    private fun getUserId(textView: TextView) {
        PurchaseClient.instance.getUserId(object : ReceivedDataListener<String?, GenericError> {
            override fun onSucceeded(data: String?) {
                Log.d("AppmateSample", "Get user id succeed $data")
                textView.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_baseline_check_circle_24,
                    0
                )
                textView.text = resources.getString(R.string.sdk_available)

                Toast.makeText(
                    applicationContext,
                    "User ID: $data",
                    Toast.LENGTH_LONG
                ).show()

            }

            override fun onError(error: GenericError) {
                Log.d("AppmateSample", "Unable to get user id: $error")
                textView.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_baseline_not_interested_24,
                    0
                )
                textView.text = resources.getString(R.string.sdk_unavailable)
            }
        })
    }
}