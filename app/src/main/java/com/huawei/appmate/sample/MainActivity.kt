package com.huawei.appmate.sample

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huawei.appmate.PurchaseClient
import com.huawei.appmate.callback.ReceivedDataListener
import com.huawei.appmate.model.*
import com.huawei.appmate.sample.ui.ProductsAdapter


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storeAccessText: TextView = findViewById(R.id.textStoreAccess)
        getUserId(storeAccessText)

        // Set recycler view for product list
        val rvProducts = findViewById<RecyclerView>(R.id.recyclerViewProducts)
        val productList = ArrayList<Product>()
        val rvAdapter = ProductsAdapter(productList)
        rvProducts.adapter = rvAdapter
        rvProducts.layoutManager = LinearLayoutManager(this)

        val rvSize = rvAdapter.itemCount
        getProducts { list ->
            productList.addAll(list as ArrayList<Product>)
            rvAdapter.notifyItemRangeChanged(rvSize, productList.size)
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

    // Get products from store
    private fun getProducts(callback: (List<Product>) -> Unit) {
        PurchaseClient.instance.getProducts(object :
            ReceivedDataListener<List<Product>, GenericError> {
            override fun onSucceeded(data: List<Product>) {
                Log.d("AppmateSample", "Get products succeed ${data.map { it.productId }}")
                callback(data)
            }

            override fun onError(error: GenericError) {
                Log.d("AppmateSample", "Unable to get products: ${error.errorMessage}")
            }
        })
    }
}