package com.kotlin.pdfextractor.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kotlin.pdfextractor.R
import com.kotlin.pdfextractor.databinding.FragmentSplashBinding
import com.kotlin.pdfextractor.utils.ConstantUtils.Companion.STORAGE_PERMISSION
import com.kotlin.pdfextractor.utils.GeneralUtils

class Splash : Fragment() {

    private var binding: FragmentSplashBinding? = null
    private lateinit var globalContext: Context

    private fun initializations(view: View) {
        globalContext = view.context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializations(view)
        if (checkPermissions(globalContext))
            setHandler()
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestHigherAndroidSettingPermission()
            } else {
                requestStoragePermission(globalContext as Activity)
            }
        }
    }

    private fun checkPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestHigherAndroidSettingPermission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(String.format("package:%s", globalContext.packageName))
            openActivityForPermission(intent)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            openActivityForPermission(intent)
        }
    }

    private fun openActivityForPermission(intent: Intent) {
        resultPermissionLauncher.launch(intent)
    }

    private var resultPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    setHandler()
                }
            }
        }

    private fun requestStoragePermission(context: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) && ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.permission_needed))
                .setMessage(context.getString(R.string.permission_message))
                .setPositiveButton(
                    context.getString(R.string.allow)
                ) { _, _ ->
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ),
                        STORAGE_PERMISSION
                    )
                }
                .setNegativeButton(
                    context.getString(R.string.cancel)
                ) { dialog, _ -> dialog.dismiss() }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION
            )
        }
    }

    private fun setHandler() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (GeneralUtils.isCurrentDestination(findNavController(), R.id.des_splash)) {
                findNavController().navigate(R.id.action_des_splash_to_des_home)
            }
        }, 2000)
    }

    fun permissionGrantedFromMain() {
        setHandler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}