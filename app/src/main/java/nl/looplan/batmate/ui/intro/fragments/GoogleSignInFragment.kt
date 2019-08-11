package nl.looplan.batmate.ui.intro.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_google_sign_in.*
import nl.looplan.batmate.R
import nl.looplan.batmate.ui.intro.IntroViewModel

class GoogleSignInFragment : Fragment() {

    companion object {
        const val TAG = "GoogleSignInFragment"
        const val RC_SIGN_IN = 5632
    }

    private lateinit var viewModel : IntroViewModel

    private lateinit var auth : FirebaseAuth

    private lateinit var client : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the view model.
        viewModel = ViewModelProviders.of(activity!!).get(IntroViewModel::class.java)

        auth = FirebaseAuth.getInstance()

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        client = GoogleSignIn.getClient(activity!!, signInOptions)

        val account = GoogleSignIn.getLastSignedInAccount(activity!!)

        if(account != null) {
            viewModel.account.value = account
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = client.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task : Task<GoogleSignInAccount>)  {
        try {
            val account = task.getResult(ApiException::class.java)

            if(account != null) {
                viewModel.account.value = account
                firebaseAuthWithGoogleSignIn(account)
            }

        } catch (exception : ApiException) {
            Log.w(TAG, "signInResult:failed code=" + exception.statusCode)
        }
    }

    private fun firebaseAuthWithGoogleSignIn(account : GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

}
