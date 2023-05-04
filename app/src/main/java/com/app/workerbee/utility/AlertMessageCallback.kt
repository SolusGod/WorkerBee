package com.app.workerbee.utility

import com.app.workerbee.callback.AlertMessageClickCallbacks

abstract class AlertMessageCallback : AlertMessageClickCallbacks {
    override fun onPositiveButtonClick() {
    }

    override fun onNegativeButtonClick() {
    }
}