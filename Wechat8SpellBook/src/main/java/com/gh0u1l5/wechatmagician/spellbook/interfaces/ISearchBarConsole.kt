package com.gh0u1l5.wechatmagician.spellbook.interfaces

import android.content.Context
import androidx.annotation.WorkerThread

interface ISearchBarConsole {
    /**
     * Called when the user entered a command in the search bar.
     * invoke at worker thread
     *
     * @param context a [Context] that can be used for later operations.
     * @param command the commend entered by the user.
     * @return true if the command should be consumed, otherwise return false.
     */
    @WorkerThread
    fun onHandleCommand(context: Context, command: String) = false
}
