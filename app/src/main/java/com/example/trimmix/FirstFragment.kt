package com.example.trimmix

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.trimmix.databinding.FragmentFirstBinding
import androidx.appcompat.widget.SearchView
import com.arthenica.ffmpegkit.FFmpegKit
import android.provider.OpenableColumns



/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val PICK_AUDIO_REQUEST_CODE = 1
    private val audioList = mutableListOf<String>() // Store uploaded audio files

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.uploadAudioButton.setOnClickListener {
            uploadAudio()
        }

        setupSearchView()
//        setupRecyclerView()
    }

    private fun setupSearchView() {
        val searchView = binding.searchBar // Assuming the search bar is bound in your layout XML

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission
                Toast.makeText(requireContext(), "Searching for: $query", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle real-time query text change
                if (!newText.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Searching for: $newText", Toast.LENGTH_SHORT)
                        .show()
                }
                return true
            }
        })
    }




    private fun uploadAudio() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, PICK_AUDIO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AUDIO_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                // Use the getFileName method to ensure consistency
                val fileName = getFileName(uri)
                binding.audioFileName.text = fileName

                Toast.makeText(requireContext(), "Audio Selected: $fileName", Toast.LENGTH_SHORT).show()
                playAudio(uri)
            }
        }
    }



    fun getFileName(uri: Uri): String {
        var fileName = "Unknown"  // Default name if nothing is found

        // Check if the URI has a content scheme (for content providers)
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.let {
                if (it.moveToFirst()) {
                    // Try to get the display name (file name)
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = it.getString(nameIndex)
                    }
                }
                it.close()
            }
        }
        // Check if the URI has a file scheme (for file system paths)
        else if (uri.scheme == "file") {
            fileName = uri.lastPathSegment ?: "Unknown"
        }

        // Return the file name
        return fileName
    }

    private fun playAudio(audioUri: Uri) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(requireContext(), audioUri)
        mediaPlayer.prepare()
        mediaPlayer.start()
        Toast.makeText(requireContext(), "Playing: ${getFileName(audioUri)}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
