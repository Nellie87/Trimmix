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
//import com.arthenica.ffmpegkit.FFmpegKit
import android.provider.OpenableColumns
import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.trimmix.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
//import com.example.trimmix.network.RetrofitClient
//import com.example.trimmix.network.SpotifySearchResponse




/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!


    private val networkService = NetworkService() // NetworkService instance


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
            networkService.getAccessToken { accessToken ->
                if (accessToken != null) {
                    Toast.makeText(requireContext(), "Access Token: $accessToken", Toast.LENGTH_LONG).show()
                    searchSpotifyMusic(accessToken)
                } else {
                    Toast.makeText(requireContext(), "Error fetching token", Toast.LENGTH_SHORT).show()
                }
            }
            uploadAudio()
        }


        setupSearchView()
        loadRecentSongs() // Load recent songs when fragment starts
    }


    private fun setupSearchView() {
        val searchView = binding.searchBar // Assuming the search bar is bound in your layout XML

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission
                if (!query.isNullOrEmpty()) {
                    searchSpotify(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Optionally handle real-time query text change
                return true
            }
        })
    }

    private fun searchSpotifyMusic(accessToken: String) {
        // Create the search query
        val query = "Beatles" // Example: Search for music by Beatles

        // Make the API call
        RetrofitClient.spotifyApiService.searchMusic("Bearer $accessToken", query)
            .enqueue(object : retrofit2.Callback<SpotifySearchResponse> {
                override fun onResponse(
                    call: Call<SpotifySearchResponse>,
                    response: retrofit2.Response<SpotifySearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val searchResults = response.body()
                        // Process the search results (display them, etc.)
                        Toast.makeText(requireContext(), "Search Results: ${searchResults?.tracks?.items?.size}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SpotifySearchResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "API Call Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun searchSpotify(query: String) {
        val authorizationToken = "Bearer BQCgti1Kjw-fDTX1o2yp5g3wZJHKoZDwOW-_brlrcWAzkc2Mly0TwvnaY4anBK6zL0aPchjPF6ssZYiLARtL40Z_txJkdFBukKldDSSAiNo7DlLwTrXs1KiJMJURoKeABwA6dok0lVI"

        RetrofitClient.spotifyApiService.searchMusic(authorizationToken, query)
            .enqueue(object : Callback<SpotifySearchResponse> {
                override fun onResponse(call: Call<SpotifySearchResponse>, response: Response<SpotifySearchResponse>) {
                    if (response.isSuccessful) {
                        val tracks = response.body()?.tracks?.items ?: emptyList()
                        updateSearchResults(tracks)
                    } else {
                        Toast.makeText(requireContext(), "Error fetching results", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SpotifySearchResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateSearchResults(tracks: List<Track>) {
        val adapter = SearchResultsAdapter(tracks) { track ->
            Toast.makeText(requireContext(), "Selected: ${track.name}", Toast.LENGTH_SHORT).show()
            // Handle song selection (e.g., play a preview)
        }
        binding.searchResultsRecyclerView.adapter = adapter
    }




    private fun displayTracks(tracks: List<Track>?) {
        // Display the tracks in your RecyclerView or any other UI component
        if (tracks != null) {
            // Update your adapter to display these tracks
        }
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

    private var mediaPlayer: MediaPlayer? = null

    private fun playAudio(audioUri: Uri) {
        try {
            val fileName = getFileName(audioUri)

            // Save the recently played song
            saveRecentSong(fileName, audioUri.toString())

            mediaPlayer?.release() // Release previous MediaPlayer instance
            mediaPlayer = MediaPlayer().apply {
                setDataSource(requireContext(), audioUri)
                prepare()
                start()
                setOnCompletionListener {
                    release()
                    mediaPlayer = null
                }
            }

            Toast.makeText(requireContext(), "Playing: $fileName", Toast.LENGTH_SHORT).show()
            loadRecentSongs()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error playing audio", Toast.LENGTH_SHORT).show()
        }
    }



    private fun saveRecentSong(fileName: String, uri: String) {
        val sharedPreferences = requireContext().getSharedPreferences("RecentSongs", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val songsList = getRecentSongs().toMutableList() // Get current list
        songsList.add(0, "$fileName|$uri") // Add new song at the top

        if (songsList.size > 5) songsList.removeAt(songsList.size - 1) // Keep max 5 songs

        editor.putStringSet("recent_songs", songsList.toSet()) // Save updated list
        editor.apply()
    }


    private fun getRecentSongs(): List<String> {
        val sharedPreferences = requireContext().getSharedPreferences("RecentSongs", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getStringSet("recent_songs", emptySet())?.toList() ?: emptyList()
    }



    private fun loadRecentSongs() {
        val recentSongs = getRecentSongs()

        binding.recentSongsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = RecentSongsAdapter(recentSongs) { songUri ->
            playAudio(Uri.parse(songUri)) // Play song when clicked
        }

        binding.recentSongsRecyclerView.adapter = adapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
