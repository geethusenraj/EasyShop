package com.ec.shop.utils

object Utils {

    fun generateCustomProductDetails(decodedResult: String): String {
        return (when {
            decodedResult.contains("*") -> {
                val textSplit = decodedResult.split("*")
                textSplit[1] + " =  Rs." + textSplit[2] + "-/-"
            }
            else -> ""
        })
    }

}