package com.example.trimmix

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SearchResultsAdapter(
    private val songs: List<Track>,
    private val onSongClick: (Track) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songTitle: TextView = view.findViewById(R.id.songTitle)
        val artistName: TextView = view.findViewById(R.id.artistName)
        val albumImage: ImageView = view.findViewById(R.id.albumImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songTitle.text = song.name
        holder.artistName.text = song.artists.joinToString { it.name }

        Log.d("API Response", "Song Data: $song")




        // Load album image using Glide
        Glide.with(holder.itemView.context)
            .load(song.album.images.firstOrNull()?.url)
            .into(holder.albumImage)

        holder.itemView.setOnClickListener {
            Log.d("SearchResultsAdapter", "Song clicked: ${song.name}, Preview URL: ${song.preview_url}")

            if (song.preview_url != null) {
                // Proceed with playing the audio
                onSongClick(song)
            } else {
                // Handle the case where there is no preview URL (e.g., show a message)
                Toast.makeText(holder.itemView.context, "Preview URL not available for ${song.name}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemCount(): Int = songs.size
}
