package nl.looplan.batmate.ui.splash.activities

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import nl.looplan.batmate.BatMatePreferences
import nl.looplan.batmate.R
import nl.looplan.batmate.ui.intro.activities.IntroActivity
import nl.looplan.batmate.ui.scanandrecord.activities.ScanAndRecordActivity

class SplashActivity : AppCompatActivity() {

    val permissionListener = permissionsListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set custom theme.
        setTheme(R.style.AppTheme)

        val user = FirebaseAuth.getInstance().currentUser
        if(user != null) {
            checkPermissions()
        } else {
            startIntroActivityAndFinish()
        }
    }

    private fun checkIfFinishedBeforeAndContinue() {
        val introFinishedBefore = BatMatePreferences.isIntroFinishedByUserBefore()

        if(introFinishedBefore) {
            startMainActivityAndFinish()
        } else {
            startIntroActivityAndFinish()
        }
    }

    private fun startMainActivityAndFinish() {
        startActivity(Intent(this, ScanAndRecordActivity::class.java))
        finish()
    }

    private fun startIntroActivityAndFinish() {
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }

    private fun permissionsListener() : PermissionListener {
        return object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                checkIfFinishedBeforeAndContinue()
            }

            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                MaterialAlertDialogBuilder(this@SplashActivity)
                    .setTitle("Camera toestemming")
                    .setMessage("Camera toestemming is nodig om kaartjes te scannen. Wil je die geven?")
                    .setPositiveButton("Oke") { dialog, which -> token.continuePermissionRequest() }
                    .setNegativeButton("Nah") { dialog, which ->
                        token.cancelPermissionRequest()
                    }
                    .show()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                MaterialAlertDialogBuilder(this@SplashActivity)
                    .setTitle("Camera toestemming")
                    .setMessage("Camera toestemming is nodig om kaartjes te scannen.")
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton("Ok") { dialog, which -> checkPermissions() }
                    .show()
            }
        }
    }

    private fun checkPermissions() {

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(permissionListener)
            .check()
    }

}
