package nl.looplan.batmate.ui.scanandrecord.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_scanandrecord.*
import kotlinx.coroutines.*
import nl.looplan.batmate.R
import nl.looplan.batmate.tools.CameraIntentHelper
import nl.looplan.batmate.tools.pager.DepthPageTransformer
import nl.looplan.batmate.tools.pager.FragmentViewPagerAdapter
import nl.looplan.batmate.tools.pager.SwipeConfigurableViewPager
import nl.looplan.batmate.ui.scanandrecord.MainViewModel

class ScanAndRecordActivity : AppCompatActivity() {

    private val job = Job()

    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var viewModel: MainViewModel

    private val onPageChangeListener = onPageChangeListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanandrecord)

        // Check if it is first time this activity is started
        if(savedInstanceState == null) {

            // Start the camera application
            startCameraApp()
        }

        // Get the view model.
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        setupObservers()

        setupViewPager()
    }

    private fun setupObservers() {
        viewModel.uploadToggle.observe(this, Observer {
            fragment_view_pager.setCurrentItem(FragmentViewPagerAdapter.Pages.FINISH.position, true)
        })

        viewModel.againToggle.observe(this, Observer {
            fragment_view_pager.setCurrentItem(FragmentViewPagerAdapter.Pages.CAMERA.position, true)
        })
    }

    private fun setupViewPager() {
        fragment_view_pager.apply {
            setAllowedSwipeDirection(SwipeConfigurableViewPager.SwipeDirection.none)
            offscreenPageLimit = 2
            adapter = FragmentViewPagerAdapter(supportFragmentManager)
            setPageTransformer(true, DepthPageTransformer())
        }
        fragment_view_pager.addOnPageChangeListener(onPageChangeListener)
    }

    private fun onPageChangeListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    FragmentViewPagerAdapter.Pages.CAMERA.position -> {
                        startCameraApp()

                        if (viewModel.imageFile.value != null) {
                            fragment_view_pager.apply {
                                setAllowedSwipeDirection(SwipeConfigurableViewPager.SwipeDirection.right)
                            }
                        } else {
                            fragment_view_pager.apply {
                                setAllowedSwipeDirection(SwipeConfigurableViewPager.SwipeDirection.none)
                            }
                        }
                    }

                    FragmentViewPagerAdapter.Pages.VALIDATION.position -> {
                        fragment_view_pager.apply {
                            setAllowedSwipeDirection(SwipeConfigurableViewPager.SwipeDirection.left)
                        }
                    }

                    FragmentViewPagerAdapter.Pages.FINISH.position -> {
                        fragment_view_pager.apply {
                            setAllowedSwipeDirection(SwipeConfigurableViewPager.SwipeDirection.none)
                        }
                    }
                }
            }
        }
    }

    private fun startCameraApp() {
        scope.launch(Dispatchers.Main) {
            val file = CameraIntentHelper.createFileAndStartCamera(this@ScanAndRecordActivity)
            viewModel.imageFile.value = file
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == CameraIntentHelper.CAMERA_INTENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            onImageTaken()
        }
    }

    private fun onImageTaken() {
        scope.launch(Dispatchers.Main) {

            // Load the FirebaseVisionImage.
            loadVisionImage()

            // Move to the validation page.
            fragment_view_pager.setCurrentItem(FragmentViewPagerAdapter.Pages.VALIDATION.position, true)
        }
    }

    private suspend fun loadVisionImage() {
        // Create the firebase image from the picture resultHQ.
        val image = withContext(Dispatchers.IO) {
            FirebaseVisionImage.fromFilePath(
                this@ScanAndRecordActivity,
                FileProvider.getUriForFile(
                    applicationContext,
                    "com.example.android.fileprovider",
                    viewModel.imageFile.value!!
                )
            )
        }
        // Set view model value.
        viewModel.visionImage.value = image
    }


    override fun onLowMemory() {
        super.onLowMemory()

        Fresco.getImagePipeline().apply {
            clearMemoryCaches()
        }

    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)

        Fresco.getImagePipeline().apply {
            clearMemoryCaches()
        }
    }

}
