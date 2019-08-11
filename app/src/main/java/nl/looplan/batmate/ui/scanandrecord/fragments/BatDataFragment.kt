package nl.looplan.batmate.ui.scanandrecord.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mjdev.libaums.UsbMassStorageDevice
import com.github.mjdev.libaums.fs.UsbFile

import nl.looplan.batmate.R
import java.util.ArrayList

class BatDataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bat_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val devices = UsbMassStorageDevice.getMassStorageDevices(context!!)
        for(device in devices) {
            device.init()

            val defaultPartition = device.partitions[0]
            val fileSystem = defaultPartition.fileSystem
            val root = fileSystem.rootDirectory

            val recordingFolders = getRecordingFolders(root)

            when(recordingFolders.size) {
                0 -> {

                }
                1 -> {

                }
                else -> {

                }
            }

        }
    }


    private fun getRecordingFolders(folder: UsbFile): List<UsbFile> {
        val list: ArrayList<UsbFile> = ArrayList()
        for(file in folder.listFiles()) {
            if(file.isDirectory) {
                if(file.name.startsWith("BL")) {
                    list.add(file)
                }
            }
        }
        return list
    }
}
