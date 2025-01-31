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
                // Display the selected audio file name
                val fileName = uri.lastPathSegment ?: "Unknown File"
                binding.audioFileName.text = fileName

                Toast.makeText(requireContext(), "Audio Selected: $fileName", Toast.LENGTH_SHORT).show()
                playAudio(uri)
            }
        }
    }


    private fun getFileName(uri: Uri): String {
        var name = "Unknown"
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndexOrThrow("_display_name")
                name = cursor.getString(nameIndex)
            }
        }
        return name
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
