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
import androidx.recyclerview.widget.GridLayoutManager
import com.arthenica.ffmpegkit.FFmpegKit


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        setupRecyclerView()
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


    private fun setupRecyclerView() {
        // Set up the RecyclerView
        val recyclerView = binding.recyclerView // Assuming your RecyclerView ID is `recyclerView`

        // Use GridLayoutManager with 2 columns
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Set up the adapter with sample data
        val sampleData = List(20) { "Item ${it + 1}" } // Generates a list of 20 items
        recyclerView.adapter = MyAdapter(sampleData)
    }

    private val PICK_AUDIO_REQUEST_CODE = 1

    private fun uploadAudio() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, PICK_AUDIO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AUDIO_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                // Handle the selected audio URI
                Toast.makeText(requireContext(), "Audio Selected: $uri", Toast.LENGTH_SHORT).show()
                playAudio(uri)
            }
        }
    }

    private fun playAudio(audioUri: Uri) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(requireContext(), audioUri)
        mediaPlayer.prepare()
        mediaPlayer.start()
        Toast.makeText(requireContext(), "Playing audio...", Toast.LENGTH_SHORT).show()
    }
    private fun trimAudio(inputPath: String, outputPath: String, startTime: String, duration: String) {
        val command = "-i $inputPath -ss $startTime -t $duration -c copy $outputPath"
        FFmpegKit.execute(command).apply {
            if (returnCode.isValueSuccess) {
                Toast.makeText(requireContext(), "Audio trimmed successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Audio trimming failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
