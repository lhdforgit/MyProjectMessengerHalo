package com.halo.common.utils.auto_start

import android.content.*
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import java.util.*

interface AutoStartPre {
    fun writeBoolean(key: String?, `is`: Boolean)

    fun isLicensingAutoStart(key: String?): Boolean
}

class AutoStartPreImp(val context: Context) : AutoStartPre {
    override fun writeBoolean(key: String?, `is`: Boolean) {
        val share = context.getSharedPreferences(AutoStartPre, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = share.edit()
        editor.putBoolean(key, `is`)
        editor.apply()
    }

    override fun isLicensingAutoStart(key: String?): Boolean {
        val share = context.getSharedPreferences(AutoStartPre, Context.MODE_PRIVATE)
        return share.getBoolean(key, false)
    }

    companion object {
        const val AutoStartPre = "AutoStartPre"
    }
}


class AutoStartHelper private constructor() {

    private fun isEnableAutoStart(prefUtils: AutoStartPre?): Boolean {
        return prefUtils?.isLicensingAutoStart(PrefUtilsImpl_IS_AUTO_START) == true
    }


    fun getAutoStartPermission(context: Context) {
        val prefUtils: AutoStartPre = AutoStartPreImp(context)
        if (isEnableAutoStart(prefUtils)) {
            return
        } else {
            when (Build.BRAND.toLowerCase(Locale.getDefault())) {
                BRAND_ASUS -> autoStartAsus(context, prefUtils)
                BRAND_XIAOMI -> autoStartXiaomi(context, prefUtils)
                BRAND_LETV -> autoStartLetv(context, prefUtils)
                BRAND_HONOR -> autoStartHonor(context, prefUtils)
                BRAND_OPPO -> autoStartOppo(context, prefUtils)
                BRAND_VIVO -> autoStartVivo(context, prefUtils)
                BRAND_NOKIA -> autoStartNokia(context, prefUtils)
            }
        }
    }

    private fun autoStartAsus(context: Context, prefUtils: AutoStartPre?) {
        if (isPackageExists(context, PACKAGE_ASUS_MAIN)) {
            showAlert(context, { dialog: DialogInterface, _: Int ->
                try {
                    prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                    startIntent(context, PACKAGE_ASUS_MAIN, PACKAGE_ASUS_COMPONENT)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                dialog.dismiss()
            }, {
                prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
            })
        }
    }

    private fun showAlert(context: Context, onClickListener: DialogInterface.OnClickListener, onClickCancelListener : DialogInterface.OnCancelListener) {
//        AlertDialog.Builder(context).setTitle(context.getString(R.string.auto_start_alter_title))
//            .setMessage(context.getString(R.string.auto_start_alter))
//            .setPositiveButton(context.getString(R.string.noti_btn_accept), onClickListener)
//            .setOnCancelListener(onClickCancelListener)
//            .show()
    }

    private fun autoStartXiaomi(context: Context, prefUtils: AutoStartPre?) {
        if (isPackageExists(context, PACKAGE_XIAOMI_MAIN)) {
            showAlert(context, { _: DialogInterface?, _: Int ->
                try {
                    prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                    startIntent(context, PACKAGE_XIAOMI_MAIN, PACKAGE_XIAOMI_COMPONENT)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, {
                prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
            })
        }
    }

    private fun autoStartLetv(context: Context, prefUtils: AutoStartPre?) {
        if (isPackageExists(context, PACKAGE_LETV_MAIN)) {
            showAlert(context, { _, _ ->
                try {
                    prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                    startIntent(context, PACKAGE_LETV_MAIN, PACKAGE_LETV_COMPONENT)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, {
                prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
            })
        }
    }

    private fun autoStartHonor(context: Context, prefUtils: AutoStartPre?) {
        if (isPackageExists(context, PACKAGE_HONOR_MAIN)) {
            showAlert(context, { dialog, which ->
                try {
                    prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                    startIntent(context, PACKAGE_HONOR_MAIN, PACKAGE_HONOR_COMPONENT)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, {
                prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
            })
        }
    }

    private fun autoStartOppo(context: Context, prefUtils: AutoStartPre?) {
        if (isPackageExists(context, PACKAGE_OPPO_MAIN) || isPackageExists(context, PACKAGE_OPPO_FALLBACK)) {
            showAlert(context, { dialog, which ->
                try {
                    prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                    startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT)
                } catch (e: Exception) {
                    e.printStackTrace()
                    try {
                        prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                        startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_FALLBACK)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        try {
                            prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                            startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_FALLBACK_A)
                        } catch (exx: Exception) {
                            exx.printStackTrace()
                        }
                    }
                }
            }, {
                prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
            })
        }
    }

    private fun autoStartVivo(context: Context, prefUtils: AutoStartPre?) {
        if (isPackageExists(context, PACKAGE_VIVO_MAIN) || isPackageExists(context, PACKAGE_VIVO_FALLBACK)) {
            showAlert(context, { _, _ ->
                try {
                    prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                    startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT)
                } catch (e: Exception) {
                    e.printStackTrace()
                    try {
                        prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                        startIntent(context, PACKAGE_VIVO_FALLBACK, PACKAGE_VIVO_COMPONENT_FALLBACK)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        try {
                            prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                            startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT_FALLBACK_A)
                        } catch (exx: Exception) {
                            exx.printStackTrace()
                        }
                    }
                }
            }, {
                prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
            })
        }
    }

    private fun autoStartNokia(context: Context, prefUtils: AutoStartPre?) {
        if (isPackageExists(context, PACKAGE_NOKIA_MAIN)) {
            showAlert(context, { _, _ ->
                try {
                    prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
                    startIntent(context, PACKAGE_NOKIA_MAIN, PACKAGE_NOKIA_COMPONENT)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, {
                prefUtils?.writeBoolean(PrefUtilsImpl_IS_AUTO_START, true)
            })
        }
    }

    @Throws(Exception::class)
    private fun startIntent(context: Context, packageName: String, componentName: String) {
        try {
            val intent = Intent()
            intent.component = ComponentName(packageName, componentName)
            context.startActivity(intent)
        } catch (var5: Exception) {
            var5.printStackTrace()
            throw var5
        }
    }

    private fun isPackageExists(context: Context, targetPackage: String): Boolean {
        val packages: List<ApplicationInfo>
        val pm: PackageManager = context.packageManager
        packages = pm.getInstalledApplications(0)
        for (packageInfo in packages) {
            if (packageInfo.packageName == targetPackage) {
                return true
            }
        }
        return false
    }

    companion object {
        const val PrefUtilsImpl_IS_AUTO_START = "PrefUtilsImpl-isAutoStart"
        val instance: AutoStartHelper
            get() = AutoStartHelper()
        /***
         * Xiaomi
         */
        private const val BRAND_XIAOMI = "xiaomi"
        private const val PACKAGE_XIAOMI_MAIN = "com.miui.securitycenter"
        private const val PACKAGE_XIAOMI_COMPONENT = "com.miui.permcenter.autostart.AutoStartManagementActivity"
        /***
         * Letv
         */
        private const val BRAND_LETV = "letv"
        private const val PACKAGE_LETV_MAIN = "com.letv.android.letvsafe"
        private const val PACKAGE_LETV_COMPONENT = "com.letv.android.letvsafe.AutobootManageActivity"
        /***
         * ASUS ROG
         */
        private const val BRAND_ASUS = "asus"
        private const val PACKAGE_ASUS_MAIN = "com.asus.mobilemanager"
        private const val PACKAGE_ASUS_COMPONENT = "com.asus.mobilemanager.powersaver.PowerSaverSettings"
        /***
         * Honor
         */
        private const val BRAND_HONOR = "honor"
        private const val PACKAGE_HONOR_MAIN = "com.huawei.systemmanager"
        private const val PACKAGE_HONOR_COMPONENT = "com.huawei.systemmanager.optimize.process.ProtectActivity"
        /**
         * Oppo
         */
        private const val BRAND_OPPO = "oppo"
        private const val PACKAGE_OPPO_MAIN = "com.coloros.safecenter"
        private const val PACKAGE_OPPO_FALLBACK = "com.oppo.safe"
        private const val PACKAGE_OPPO_COMPONENT = "com.coloros.safecenter.permission.startup.StartupAppListActivity"
        private const val PACKAGE_OPPO_COMPONENT_FALLBACK = "com.oppo.safe.permission.startup.StartupAppListActivity"
        private const val PACKAGE_OPPO_COMPONENT_FALLBACK_A = "com.coloros.safecenter.startupapp.StartupAppListActivity"
        /**
         * Vivo
         */
        private const val BRAND_VIVO = "vivo"
        private const val PACKAGE_VIVO_MAIN = "com.iqoo.secure"
        private const val PACKAGE_VIVO_FALLBACK = "com.vivo.permissionmanager"
        private const val PACKAGE_VIVO_COMPONENT = "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
        private const val PACKAGE_VIVO_COMPONENT_FALLBACK = "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
        private const val PACKAGE_VIVO_COMPONENT_FALLBACK_A = "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
        /**
         * Nokia
         */
        private const val BRAND_NOKIA = "nokia"
        private const val PACKAGE_NOKIA_MAIN = "com.evenwell.powersaving.g3"
        private const val PACKAGE_NOKIA_COMPONENT = "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity"
    }
}