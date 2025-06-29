package com.example.projectmdp.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val USER_DASHBOARD = "user_dashboard"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val ADD_PRODUCT = "add_product"
    const val EDIT_PROFILE = "edit_profile"
    const val TRANSACTION_HISTORY = "transaction_history"
    // Helper function to create the route with an ID
    fun productDetailRoute(productId: String): String {
        return "product_detail/$productId"
    }
}