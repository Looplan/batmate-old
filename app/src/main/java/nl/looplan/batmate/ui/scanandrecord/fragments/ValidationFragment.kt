package nl.looplan.batmate.ui.scanandrecord.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.facebook.imagepipeline.request.ImageRequest
import kotlinx.android.synthetic.main.fragment_validation.*
import nl.looplan.batmate.R
import nl.looplan.batmate.databinding.FragmentValidationBinding
import nl.looplan.batmate.ui.scanandrecord.MainViewModel


/**
 * Fragment for validation of the taken picture and the details scanned from it.
 *
 */
class ValidationFragment : Fragment() {

    companion object {
        const val TAG = "ValidationFragment"
        fun newInstance() = ValidationFragment()
    }

    /**
     * The view model which holds the data for this app.
     * */
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get the view model.
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        val binding : FragmentValidationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_validation, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Get the view model.
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        setupObservers()

        setupListeners()
    }

    private fun setupObservers() {
        viewModel.visionImage.observe(this, Observer {
            if(it != null) {
                showRecognitionProgress()

                // Set the image.
                validation_image.setImageRequest(ImageRequest.fromFile(viewModel.imageFile.value))
            }
        })

        viewModel.recognizedText.observe(this, Observer {
            if(it != null) {
                hideRecognitionProgress()
            }
        })
    }

    private fun setupListeners() {
        validation_fab.setOnClickListener {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Show the recognition progress.
     * */
    private fun showRecognitionProgress() {
        TransitionManager.beginDelayedTransition(validation_container)

        validation_progress_text.text = "Plaatje aan het scannen"

        ConstraintSet().apply {
            clone(validation_container)

            clear(R.id.validation_progress, ConstraintSet.BOTTOM)
            clear(R.id.validation_progress, ConstraintSet.TOP)
            clear(R.id.validation_name_text_layout, ConstraintSet.TOP)

            setMargin(R.id.validation_progress, ConstraintSet.TOP, 8)
            setMargin(R.id.validation_progress, ConstraintSet.BOTTOM, 16)

            connect(R.id.validation_progress, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            connect(R.id.validation_progress, ConstraintSet.BOTTOM, R.id.validation_name_text_layout, ConstraintSet.TOP)
            connect(R.id.validation_name_text_layout, ConstraintSet.TOP, R.id.validation_progress, ConstraintSet.BOTTOM)

            setVisibility(R.id.validation_progress, ConstraintSet.VISIBLE)

            applyTo(validation_container)
        }
    }

    /**
     * Hide the recognition progress.
     * */
    private fun hideRecognitionProgress() {

        TransitionManager.beginDelayedTransition(validation_container)

        ConstraintSet().apply {
            clone(validation_container)

            setVisibility(R.id.validation_progress, ConstraintSet.GONE)

            clear(R.id.validation_progress, ConstraintSet.BOTTOM)
            clear(R.id.validation_progress, ConstraintSet.TOP)
            clear(R.id.validation_name_text_layout, ConstraintSet.TOP)

            setMargin(R.id.validation_name_text_layout, ConstraintSet.TOP, 16)

            connect(R.id.validation_name_text_layout, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)

            applyTo(validation_container)
        }
    }


}
