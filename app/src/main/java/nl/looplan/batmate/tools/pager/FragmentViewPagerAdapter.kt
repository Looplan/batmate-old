package nl.looplan.batmate.tools.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import nl.looplan.batmate.ui.scanandrecord.fragments.CameraFragment
import nl.looplan.batmate.ui.scanandrecord.fragments.FinishFragment
import nl.looplan.batmate.ui.scanandrecord.fragments.ValidationFragment

class FragmentViewPagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm) {

    enum class Pages(val position : Int) {
        CAMERA(0),
        VALIDATION(1),
        FINISH(2)
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            Pages.CAMERA.position -> {
                return CameraFragment()
            }
            Pages.VALIDATION.position -> {
                return ValidationFragment()
            }
            Pages.FINISH.position -> {
                return FinishFragment()
            }
        }
        return CameraFragment()
    }

    override fun getCount(): Int {
        return 3
    }

}