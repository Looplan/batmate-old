package nl.looplan.batmate.ui.scanandrecord.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.coroutines.*
import nl.looplan.batmate.R
import nl.looplan.batmate.tools.CameraIntentHelper
import nl.looplan.batmate.ui.scanandrecord.MainViewModel

class CameraFragment : Fragment() {
    companion object {
        const val TAG = "CameraFragment"
    }

    private lateinit var viewModel: MainViewModel

    private val job = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_camera_layout.setOnClickListener {
            uiScope.launch {
                startCameraApp()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Get the view model.
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        uiScope.launch {
            deleteCacheDirectory()
        }
    }

    private suspend fun startCameraApp() {
        val file = CameraIntentHelper.createFileAndStartCamera(activity!!)
        viewModel.imageFile.value = file

        navigateToValidationFragment()
    }

    private fun navigateToValidationFragment() {
        findNavController().navigate(R.id.validationFragment)
    }

    private suspend fun deleteCacheDirectory() {

        Log.i(TAG, "deleteCacheDirectory: Deleting cache directory")

        // Remove cache files.
        withContext(Dispatchers.IO) {
            activity?.cacheDir?.listFiles()?.forEach { file ->
                file.delete()
            }
        }
    }

}