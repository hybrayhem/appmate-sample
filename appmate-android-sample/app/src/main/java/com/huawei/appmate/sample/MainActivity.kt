package com.huawei.appmate.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.huawei.appmate.PurchaseClient
import com.huawei.appmate.callback.PurchaseResultListener
import com.huawei.appmate.callback.ReceivedDataListener
import com.huawei.appmate.model.*
import com.huawei.appmate.sample.ui.ProductsAdapter


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storeAccessText: TextView = findViewById(R.id.textStoreAccess)
        getUserId(storeAccessText)

        val purchaseList = ArrayList<PurchaseInfo>()
        val itemCountText: TextView = findViewById(R.id.textItemCount)

        getPurchasesHistory { list ->
            purchaseList.clear()
            purchaseList.addAll(list.filter { it.purchaseStatus == 1 || it.purchaseStatus == 4 })   // 1: owned consumable, 4: non-consumable

            val count = purchaseList.filter { it.productId == "sample_consumable" }.size.toString()
            itemCountText.text = count
            setItemsBar(count.toInt())
        }

        val btnSpend: Button = findViewById(R.id.buttonSpend)
        btnSpend.setOnClickListener {
            var lastPurchaseToken = ""
            if (purchaseList.isNotEmpty())
                lastPurchaseToken =
                    purchaseList.last { it.productId == "sample_consumable" }.purchaseToken

            consumeProduct(lastPurchaseToken)
        }

        // Set recycler view for product list
        val rvProducts = findViewById<RecyclerView>(R.id.recyclerViewProducts)
        val productList = ArrayList<Product>()
        val rvAdapter = ProductsAdapter(productList, ::purchaseProduct, purchaseList)
        rvProducts.adapter = rvAdapter
        rvProducts.layoutManager = LinearLayoutManager(this)

        storeAccessText.setOnClickListener {
            getPurchases { }

            getPurchasesHistory { list ->
                purchaseList.clear()
                purchaseList.addAll(list.filter { it.purchaseStatus == 1 || it.purchaseStatus == 4 })   // 1: owned consumable, 4: non-consumable

                val count =
                    purchaseList.filter { it.productId == "sample_consumable" }.size.toString()
                itemCountText.text = count
                setItemsBar(count.toInt())
            }
            rvAdapter.notifyDataSetChanged()
        }

        getProducts { list ->
            rvAdapter.setProductList(list)
            rvAdapter.notifyItemInserted(productList.size - 1)
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

                makeText(
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

    private fun purchaseProduct(product: Product) {
        PurchaseClient.instance.purchase(
            activity = this,
            purchaseRequest = PurchaseRequest(product.productId, product.productType),
            listener = object : PurchaseResultListener<PurchaseResultInfo, GenericError> {
                override fun onSuccess(data: PurchaseResultInfo) {
                    Log.d("AppmateSample", "Purchase succeed '${product.productId}': $data")

                    increaseItemCount()
                }

                override fun onError(error: GenericError) {
                    Log.d(
                        "AppmateSample",
                        "Unable to purchase '${product.productId}': ${error.errorMessage}"
                    )
                }

                override fun onQueryPurchasesResponse(
                    p0: BillingResult,
                    p1: MutableList<Purchase>
                ) {
                    Log.d(
                        "AppmateSample",
                        "Query Purchases Response: " +
                                "Product = ${product.productId},\n" +
                                "p0 = $p0,\n " +
                                "p1 = $p1,\n"
                    )
                }
            }
        )
    }

    // For product purchase
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_BUY) {
            PurchaseClient.instance.getResultListener().onActivityResult(data)
        }
    }

    fun decreaseItemCount() {
        val itemCountText: TextView = findViewById(R.id.textItemCount)
        var count: Int = itemCountText.text.toString().toInt()
        count--
        itemCountText.text = count.toString()

        setItemsBar(count)
    }

    fun increaseItemCount() {
        val itemCountText: TextView = findViewById(R.id.textItemCount)
        var count: Int = itemCountText.text.toString().toInt()
        count++
        itemCountText.text = count.toString()

        setItemsBar(count)
    }

    private fun setItemsBar(count: Int) {
        val item1: ImageView = findViewById(R.id.iconFav1)
        val item2: ImageView = findViewById(R.id.iconFav2)
        val item3: ImageView = findViewById(R.id.iconFav3)
        val item4: ImageView = findViewById(R.id.iconFav4)
        val item5: ImageView = findViewById(R.id.iconFav5)
        val itemList = listOf(item1, item2, item3, item4, item5)

        for (i in 0 until count) {
            itemList[i].setColorFilter(ContextCompat.getColor(applicationContext, R.color.red_500))
        }
        for (i in count until itemList.size) {
            itemList[i].setColorFilter(ContextCompat.getColor(applicationContext, R.color.grey_500))
//            if(i == 4) break
        }
    }

    private fun consumeProduct(purchaseToken: String) {
        PurchaseClient.instance.consumePurchase(purchaseToken,
            object : ReceivedDataListener<String, GenericError> {
                override fun onSucceeded(data: String) {
                    Log.d("AppmateSample", "Consume succeed '$purchaseToken': $data")

                    decreaseItemCount()
                    runOnUiThread {
                        makeText(
                            applicationContext,
                            "Spent!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(error: GenericError) {
                    Log.d(
                        "AppmateSample", "Unable to consume '$purchaseToken': ${error.errorMessage}"
                    )

                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "Unable to spend!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )
    }

    private fun getPurchasesHistory(callback: (List<PurchaseInfo>) -> Unit) {
        PurchaseClient.instance.getPurchasesHistory(
            object : ReceivedDataListener<List<PurchaseInfo>, GenericError> {
                override fun onSucceeded(data: List<PurchaseInfo>) {
                    Log.d(
                        "AppmateSample",
                        "Get purchases history succeed ${data.map { it.orderId }}"
                    )

                    callback(data)
                }

                override fun onError(error: GenericError) {
                    Log.d("AppmateSample", "Unable to get purchases history: ${error.errorMessage}")
                }
            }
        )
    }

    private fun getPurchases(callback: (List<PurchaseInfo>) -> Unit) {
        PurchaseClient.instance.getPurchases(
            object : ReceivedDataListener<List<PurchaseInfo>, GenericError> {
                override fun onSucceeded(data: List<PurchaseInfo>) {
                    Log.d("AppmateSample", "Get purchases succeed ${data.map { it.orderId }}")

                    callback(data)

                    runOnUiThread {
                        makeText(
                            applicationContext,
                            "Get purchases: ${data.size}\n" +
                                    "Non-consumable: ${data.filter { it.productType == ProductType.NONCONSUMABLE }.size}\n" +
                                    "Consumable: ${data.filter { it.productType == ProductType.CONSUMABLE }.size}\n",
//                                    "sample_consumable: ${data.filter { it.productId == "sample_consumable" }.size}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onError(error: GenericError) {
                    Log.d("AppmateSample", "Unable to get purchases: ${error.errorMessage}")
                }
            }
        )
    }

    private fun isPurchased(productId: String) {
        PurchaseClient.instance.isProductPurchased(productId,
            object : ReceivedDataListener<Boolean, GenericError> {
                override fun onSucceeded(data: Boolean) {
                    Log.d("AppmateSample", "Is $productId purchased = $data")
                }

                override fun onError(error: GenericError) {
                    Log.d(
                        "AppmateSample",
                        "Unable to check purchase $productId: ${error.errorMessage}"
                    )
                }
            })
    }


}