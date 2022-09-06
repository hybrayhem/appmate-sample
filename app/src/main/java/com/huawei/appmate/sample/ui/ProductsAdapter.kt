package com.huawei.appmate.sample.ui

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.huawei.appmate.PurchaseClient
import com.huawei.appmate.callback.PurchaseResultListener
import com.huawei.appmate.model.*
import com.huawei.appmate.sample.R

class ProductsAdapter(private val productList: List<Product>, private var activity: Activity) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val purchaseButton: Button = itemView.findViewById(R.id.btnPurchase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_card, parent, false)
        return ViewHolder(v);
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.productName.text = productList[position].productId
        holder.productDescription.text = productList[position].purchaseType

        holder.purchaseButton.text = productList[position].price
        holder.purchaseButton.setOnClickListener {
            purchaseProduct(productList[position])
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    private fun purchaseProduct(product: Product) {
        PurchaseClient.instance.purchase(
            activity = activity,
            purchaseRequest = PurchaseRequest(product.productId, product.productType),
            listener = object : PurchaseResultListener<PurchaseResultInfo, GenericError> {
                override fun onSuccess(data: PurchaseResultInfo) {
                    Log.d("AppmateSample", "Purchase succeed '${product.productId}': $data")
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
}