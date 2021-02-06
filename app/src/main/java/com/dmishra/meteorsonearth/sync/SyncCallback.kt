package com.dmishra.meteorsonearth.sync

import android.content.Context

open class SyncCallback(var context: Context) {
    open fun onSyncStarted() {}
    open fun onSyncSuccess() {}
    open fun onSyncFailed() {}
}
