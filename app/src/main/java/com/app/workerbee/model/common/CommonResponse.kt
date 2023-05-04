package com.app.workerbee.model.common

data class CommonResponse<T>(

    var status: Int,
    var code: Int,
    var message: String,
    var oData: T? = null
)
