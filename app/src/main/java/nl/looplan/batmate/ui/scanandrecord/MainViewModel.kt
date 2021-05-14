package nl.looplan.batmate.ui.scanandrecord

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.coroutines.*
import nl.looplan.batmate.tools.SingleLiveEvent
import nl.looplan.batmate.tools.VisionImageSearcher
import java.io.File

class MainViewModel : ViewModel() {

    companion object {
        const val TAG = "MainViewModel"
    }

    val job = Job()

    val scope = CoroutineScope(Dispatchers.Main + job)

    val imageFile: MutableLiveData<File> = MutableLiveData()

    val visionImage : MutableLiveData<FirebaseVisionImage> = MutableLiveData()

    val recognizedText : MutableLiveData<FirebaseVisionText> = MutableLiveData()

    val recognizedTitle : MutableLiveData<String> = MutableLiveData()

    val uploadToggle : SingleLiveEvent<Void> = SingleLiveEvent()

    val againToggle : SingleLiveEvent<Void> = SingleLiveEvent()

    private val visionImageObserver = Observer<FirebaseVisionImage> { image ->
        // Get detector.
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

        // Run text recognition.s
        detector.processImage(image)
            .addOnSuccessListener { visionText ->
                // Set the view model text.
                recognizedText.value = visionText
            }
            .addOnFailureListener {
                // Log the error.
                Log.e("PlayerSelectionFragment", it.message!!)
            }
    }

    private val recognizedTextObserver = Observer<FirebaseVisionText> { text ->
        scope.launch(Dispatchers.Main) {
            try {
                val title = withContext(Dispatchers.IO) {
                    VisionImageSearcher.searchForPaperTitle(text, visionImage.value!!)
                }
                // Set the view model title value.
                recognizedTitle.value = title
            } catch(exception : VisionImageSearcher.NoMatchingTextBlocksFound) {
                Log.i(TAG, "No text block was found that was matching with the search params")
            }
        }

    }

    init {
        imageFile.observeForever {

        }

        visionImage.observeForever(visionImageObserver)
        recognizedText.observeForever(recognizedTextObserver)
    }

}