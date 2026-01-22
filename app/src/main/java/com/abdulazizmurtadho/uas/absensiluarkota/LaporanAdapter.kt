package com.abdulazizmurtadho.uas.absensiluarkota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.graphics.BitmapFactory
import java.io.File

class LaporanAdapter : ListAdapter<Absen, LaporanAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tv_nama)
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal)
        val tvKoordinat: TextView = view.findViewById(R.id.tv_koordinat)
        val ivFoto: ImageView = view.findViewById(R.id.iv_foto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_absen, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val absen = getItem(position)
        holder.tvNama.text = absen.nama
        holder.tvTanggal.text = absen.tanggal
        holder.tvKoordinat.text = "Lat:${absen.latitude}, Lng:${absen.longitude}"

        // Load foto dari path
        try {
            val file = File(absen.fotoPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.ivFoto.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            holder.ivFoto.setImageResource(android.R.drawable.ic_menu_camera)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Absen>() {
        override fun areItemsTheSame(oldItem: Absen, newItem: Absen): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Absen, newItem: Absen): Boolean = oldItem == newItem
    }
}
