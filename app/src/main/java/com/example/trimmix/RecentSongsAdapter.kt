package com.example.trimmix

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecentSongsAdapter(
    private val songs: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<RecentSongsAdapter.SongViewHolder>() {

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songTitle: TextView = itemView.findViewById(R.id.songTitle)

        fun bind(songData: String) {
            val parts = songData.split("|")
            val fileName = parts[0]
            val uri = parts[1]

            songTitle.text = fileName
            itemView.setOnClickListener { onClick(uri) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songs[position])
    }

    override fun getItemCount() = songs.size
}
