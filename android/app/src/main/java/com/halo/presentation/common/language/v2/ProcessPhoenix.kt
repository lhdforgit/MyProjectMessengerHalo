/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation.common.language.v2

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.os.Parcelable
import android.os.Process
import androidx.appcompat.app.AppCompatActivity


class ProcessPhoenix : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intents : ArrayList<out Parcelable>? = intent?.getParcelableArrayListExtra<Intent>(KEY_RESTART_INTENTS)
        intents?.apply {
            val array = arrayOfNulls<Intent>(intents.size)
            if(array.isNotEmpty()){
                startActivities(intents.toArray(array))
                finishAffinity()
            }
        }
    }

    companion object {
        private const val KEY_RESTART_INTENTS = "phoenix_restart_intents"
        /**
         * Call to restart the application process using the [default][Intent.CATEGORY_DEFAULT]
         * activity as an intent.
         *
         *
         * Behavior of the current process after invoking this method is undefined.
         */
        fun triggerRebirth(context: Context) {
            triggerRebirth(context, getRestartIntent(context))
        }

        fun triggerRebirth(context: AppCompatActivity) {
            triggerRebirth(context, getRestartIntent(context))
        }

        /**
         * Call to restart the application process using the specified intents.
         *
         *
         * Behavior of the current process after invoking this method is undefined.
         */
        fun triggerRebirth(context: Context, vararg nextIntents: Intent) {
            val intent = Intent(context, ProcessPhoenix::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK) // In case we are called with non-Activity context.
            val list : ArrayList<out Intent> = ArrayList(listOf(*nextIntents))
            intent.putParcelableArrayListExtra(KEY_RESTART_INTENTS,list)
            context.startActivity(intent)
            if(context is Activity)context.finishAfterTransition()
            Runtime.getRuntime().exit(0) // Kill kill kill!
        }

        private fun getRestartIntent(context: Context): Intent {
            val packageName: String = context.packageName
            val defaultIntent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
            if (defaultIntent != null) {
                defaultIntent.addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
                return defaultIntent
            }
            throw IllegalStateException("Unable to determine default activity for "
                    + packageName
                    + ". Does an activity specify the DEFAULT category in its intent filter?")
        }

        /**
         * Checks if the current process is a temporary Phoenix Process.
         * This can be used to avoid initialisation of unused resources or to prevent running code that
         * is not multi-process ready.
         *
         * @return true if the current process is a temporary Phoenix Process
         */
        fun isPhoenixProcess(context: Context): Boolean {
            val currentPid: Int = Process.myPid()
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningProcesses = manager.runningAppProcesses
            if (runningProcesses != null) {
                for (processInfo in runningProcesses) {
                    if (processInfo.pid == currentPid && processInfo.processName.endsWith(":phoenix")) {
                        return true
                    }
                }
            }
            return false
        }
    }
}