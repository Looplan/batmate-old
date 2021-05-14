package nl.looplan.batmate.ui.intro.activities

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroFragment
import com.github.appintro.model.SliderPage
import nl.looplan.batmate.BatMatePreferences
import nl.looplan.batmate.R
import nl.looplan.batmate.ui.intro.IntroViewModel
import nl.looplan.batmate.ui.intro.fragments.GoogleSignInFragment
import nl.looplan.batmate.ui.scanandrecord.activities.ScanAndRecordActivity
import nl.looplan.batmate.ui.splash.activities.SplashActivity

class IntroActivity : AppIntro() {

    private lateinit var viewModel : IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val welcomePage = SliderPage().apply {
            title = "Welkom bij deze app"
            description = "Scan een inventarisatie kaartje met gemak in en sla hem gelijk op in de cloud"
            imageDrawable = R.drawable.looplan_logo_512x512
            backgroundColor = Color.BLACK
        }

        val cameraPage = SliderPage().apply {
            title = "Neem een foto"
            description = "Neem een foto van het kaartje"
            imageDrawable = R.drawable.ic_photo_camera_512dp
            backgroundColor = Color.BLACK
        }

        val validationPage = SliderPage().apply {
            title = "Scan & bevestig"
            description = "De foto word gescant, jij hoeft alleen te kijken of het klopt"
            imageDrawable = R.drawable.ic_scanner_512dp
            backgroundColor = Color.BLACK
        }

        val permissionsPage = SliderPage().apply {
            title = "Let's go? Wacht nog eventjes!"
            description = "Voordat je de app kan gebruiken, moeten we wel gemachtigd zijn om de camera kunnen gebruiken. Je "
            imageDrawable = R.drawable.ic_permissions
            backgroundColor = Color.BLACK
        }

        val loginPage = SliderPage().apply {
            title = "Het laatse, we beloven het"
            description = "Log in met je OneDrive account zodat we gelijk voor je kunnen uploaden, klik op de afbeelding"
            imageDrawable = R.drawable.ic_onedrive
            backgroundColor = Color.BLACK
        }

        val donePage = SliderPage().apply {
            title = "Ready, set and go!"
            description = "Top! Je kan de app nu gebruiken"
            imageDrawable = R.drawable.ic_cheers
            backgroundColor = Color.BLACK
        }

        askForPermissions(
            permissions = arrayOf(Manifest.permission.CAMERA),
            slideNumber = 4,
            required = true)

        addSlide(AppIntroFragment.newInstance(welcomePage))
        addSlide(AppIntroFragment.newInstance(cameraPage))
        addSlide(AppIntroFragment.newInstance(validationPage))
        addSlide(AppIntroFragment.newInstance(permissionsPage))
        //addSlide(GoogleSignInFragment())
        addSlide(AppIntroFragment.newInstance(donePage))

        isSkipButtonEnabled = false

    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        BatMatePreferences.setIntroFinishedByUserBefore(true)
        startMainActivityAndFinish()
    }

    private fun startMainActivityAndFinish() {
        startActivity(Intent(this, ScanAndRecordActivity::class.java))
        finish()
    }

    private fun startSplashActivityAndFinish() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }

}
