package com.example.projectmdp.ui.module.Products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectmdp.data.model.product.Product

class ProductViewModel: ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    val selectedProduct = MutableLiveData<Product>()
    fun setSelectedProduct(product: Product) {
        selectedProduct.value = product
    }

}