package com.kotlin.pdfextractor

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.pdfextractor.fragments.Splash
import com.kotlin.pdfextractor.utils.ConstantUtils.Companion.STORAGE_PERMISSION
import com.kotlin.pdfextractor.utils.GeneralUtils.showMessage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (STORAGE_PERMISSION == requestCode) {
            try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_container_Main)
                    navHost?.let { navFragment ->
                        navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                            val fragment1: Splash = fragment as Splash
                            fragment1.permissionGrantedFromMain()
                        }
                    }
                } else {
                    showMessage(this, "Permission Denied!")
                }
            } catch (e: Exception) {

            }
        }
    }

}