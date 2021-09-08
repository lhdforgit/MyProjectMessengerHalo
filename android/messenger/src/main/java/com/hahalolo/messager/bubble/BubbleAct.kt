package com.hahalolo.messager.bubble

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class BubbleAct : AppCompatActivity() {

    companion object {

        private fun isMiUi(): Boolean {
            return getSystemProperty("ro.miui.ui.version.name")?.isNotBlank() == true
        }

        private fun isMiuiWithApi28OrMore(): Boolean {
            return isMiUi() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }

        private fun getSystemProperty(propName: String): String? {
            val line: String
            var input: BufferedReader? = null
            try {
                val p = Runtime.getRuntime().exec("getprop $propName")
                input = BufferedReader(InputStreamReader(p.inputStream), 1024)
                line = input.readLine()
                input.close()
            } catch (ex: IOException) {
                return null
            } finally {
                if (input != null) {
                    try {
                        input.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return line
        }

        fun isHavePermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                return false
            }
            return true
        }

        fun goToXiaomiPermissions(activity: Activity, requestCode :Int ): Boolean {
            if (isMiuiWithApi28OrMore()) {
                try {
                    val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                    intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
                    intent.putExtra("extra_pkgname", activity.packageName)
                    activity.startActivityForResult(intent, requestCode)
                    return true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return false
        }

        fun enablePermission(activity: Activity, REQUEST_CODE: Int): Boolean {
            if (!BubbleService.ENABLE_BUBBLE) return false
            if (!isHavePermission(activity)) {
//                context.startActivity(Intent(context, BubbleAct::class.java))
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${activity.packageName}"))
                try {
                    activity.startActivityForResult(intent, REQUEST_CODE)
                    return true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return false
        }
    }

    val REQUEST_CODE = 5469

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        finish()
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            try {
                startActivityForResult(intent, REQUEST_CODE)
                return false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //check for api < 23
        return true
    }

    private fun checkPermissionPopUp(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            try {
                startActivityForResult(intent, REQUEST_CODE)
                return false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        //check for api < 23
        return true
    }
}