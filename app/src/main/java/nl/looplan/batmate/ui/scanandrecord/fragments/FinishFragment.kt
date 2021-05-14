package nl.looplan.batmate.ui.scanandrecord.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.TransitionManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.android.synthetic.main.fragment_finish.*
import kotlinx.coroutines.*
import nl.looplan.batmate.R
import nl.looplan.batmate.ui.scanandrecord.MainViewModel

class FinishFragment : Fragment() {

    companion object {
        const val DOLPHINS = 0
        const val FROG = 1
        const val MONSTER = 2
        const val DANCING = 3
        const val SWAG = 4
        const val OBAMA = 5
        const val STATE_KEY = "STATE"
        const val NONE = "NONE"
        const val PROGRESS = "PROGRESS"
        const val SUCCESS = "SUCCESS"
    }

    val job = Job()

    val scope = CoroutineScope(Dispatchers.Main + job)

    lateinit var state : String

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        finish_again_button.setOnClickListener {
            viewModel.againToggle.call()
        }

        state = savedInstanceState?.getString(STATE_KEY) ?: NONE

        when(state) {
            PROGRESS -> showProgress()
            SUCCESS -> showSuccess()
            NONE -> { }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(STATE_KEY, state)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Get the view model.
        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)

        viewModel.uploadToggle.observe(this, Observer {
            showProgress()

            scope.launch {
                upload()
            }
        })
    }

    private fun showProgress() {
        state = PROGRESS

        val randomNumber = (0..2).random()

        loadProgressImage(randomNumber)

        setLoadingText(randomNumber)

        animateToProgress()
    }

    private fun loadProgressImage(number: Number) {

        var imageRequest : ImageRequest? = null

        when(number) {
            DOLPHINS -> {
                // Load gif.
                imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.dolphins).build()
            }
            FROG -> {
                // Load gif.
                imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.frog).build()
            }
            MONSTER -> {
                // Load gif.
                imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.monster).build()
            }
        }

        val controller = Fresco.newDraweeControllerBuilder()
            .setAutoPlayAnimations(true)
            .setUri(imageRequest!!.sourceUri)
            .build()

        finish_card_image.controller = controller
    }

    private fun setLoadingText(number: Number) {

        val text : String? = when(number) {
            DOLPHINS -> "Catching dolphins"
            FROG -> "Breaking fingers"
            MONSTER -> "Crunching cookies"
            else -> "Wait who is working?"
        }

        finish_progress_text.text = text
    }

    private fun showSuccess() {
        state = SUCCESS

        val randomNumber = (3..5).random()

        loadDoneImage(randomNumber)
        setDoneText(randomNumber)

        animateToSuccess()
    }

    private fun loadDoneImage(number: Number) {

        var imageRequest: ImageRequest? = null

        when(number) {
             DANCING -> {
                // Load gif.
                imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.dancing).build()
            }
            SWAG -> {
                // Load gif.
                imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.swag).build()
            }
            OBAMA -> {
                // Load gif.
                imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.obama).build()
            }
        }

        val controller = Fresco.newDraweeControllerBuilder()
            .setAutoPlayAnimations(true)
            .setUri(imageRequest!!.sourceUri)
            .build()
        finish_card_image.controller = controller
    }

    private fun setDoneText(number: Number) {
        finish_progress_text.text = "Klaar!"
    }

    private fun showFailure() {

    }

    private fun animateToSuccess() {
        TransitionManager.beginDelayedTransition(fragment_finish_layout)

        ConstraintSet().apply {
            clone(fragment_finish_layout)

            clear(R.id.finish_progress, ConstraintSet.BOTTOM)

            setMargin(R.id.finish_progress, ConstraintSet.BOTTOM, 30)

            connect(R.id.finish_progress, ConstraintSet.BOTTOM, R.id.finish_again_button, ConstraintSet.TOP)

            setVisibility(R.id.finish_again_button, ConstraintSet.VISIBLE)

            applyTo(fragment_finish_layout)
        }

        ConstraintSet().apply {
            clone(finish_progress)

            clear(R.id.finish_progress_text, ConstraintSet.TOP)

            connect(R.id.finish_progress_text, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)

            setVisibility(R.id.finish_progress_bar, ConstraintSet.INVISIBLE)

            applyTo(finish_progress)
        }
    }

    private fun animateToProgress() {
        TransitionManager.beginDelayedTransition(fragment_finish_layout)

        ConstraintSet().apply {
            clone(fragment_finish_layout)

            clear(R.id.finish_progress, ConstraintSet.BOTTOM)

            setMargin(R.id.finish_progress, ConstraintSet.BOTTOM, 150)

            connect(R.id.finish_progress, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

            setVisibility(R.id.finish_again_button, ConstraintSet.INVISIBLE)

            applyTo(fragment_finish_layout)
        }

        ConstraintSet().apply {
            clone(finish_progress)

            clear(R.id.finish_progress_text, ConstraintSet.TOP)

            connect(R.id.finish_progress_text, ConstraintSet.TOP, R.id.finish_progress_bar, ConstraintSet.BOTTOM)

            setVisibility(R.id.finish_progress_bar, ConstraintSet.VISIBLE)

            applyTo(finish_progress)
        }
    }


    private suspend fun upload() {
        val name = viewModel.recognizedTitle.value

        val path = "$name.jpg"

        val file = viewModel.imageFile.value

        val fileUri = Uri.fromFile(file)

        val storage = FirebaseStorage.getInstance()

        val folderReference = storage.reference.child("reports")

        var fileReference = folderReference.child(path)

        var count = 1
        var uri : Uri? = null

        while(uri != null) {
            uri = withContext(Dispatchers.IO) {
                try {
                    fileReference.downloadUrl.result
                } catch (exception : StorageException) {
                    null
                }
            }
            if(uri != null) {
                fileReference = folderReference.child("$name$count")
            }
            count++
        }



        val task = fileReference.putFile(fileUri)

        task.addOnSuccessListener {
            showSuccess()
        }.addOnFailureListener {
            showFailure()
        }.addOnProgressListener {
            if(it.bytesTransferred != 0.toLong()) {
                val percent = (it.bytesTransferred / it.totalByteCount) * 100
                finish_progress_bar.progress = percent.toInt()
            }
        }
    }

}