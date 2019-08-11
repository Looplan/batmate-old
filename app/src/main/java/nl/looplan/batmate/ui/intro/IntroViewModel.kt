package nl.looplan.batmate.ui.intro

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class IntroViewModel : ViewModel() {
    val account : MutableLiveData<GoogleSignInAccount> = MutableLiveData()
}