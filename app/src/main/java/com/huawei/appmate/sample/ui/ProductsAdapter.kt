package com.huawei.appmate.sample.ui

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huawei.appmate.model.*
import com.huawei.appmate.sample.R

class ProductsAdapter(
    private val productList: ArrayList<Product>,
    private val purchaseCallback: (Product) -> Unit
) :
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
            purchaseCallback(productList[position])
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun setProductList(list: List<Product>) {
        productList.clear()
        productList.addAll(list)
    }

}