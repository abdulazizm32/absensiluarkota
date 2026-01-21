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
class LaporanAdapter :
    ListAdapter<Absen, LaporanAdapter.AbsenViewHolder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Absen>() {
            override fun areItemsTheSame(oldItem: Absen, newItem: Absen) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Absen, newItem: Absen) =
                oldItem == newItem
        }
    }

    inner class AbsenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvKoordinat: TextView = view.findViewById(R.id.tvKoordinat)
        val ivFoto: ImageView = view.findViewById(R.id.ivFoto)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsenViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_absen, parent, false)
        return AbsenViewHolder(v)
    }

    override fun onBindViewHolder(holder: AbsenViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvNama.text = item.nama
        holder.tvTanggal.text = item.tanggal
        holder.tvKoordinat.text =
            "${item.latitude}, ${item.longitude}"

        holder.ivFoto.setImageResource(R.drawable.imge_not_found) // default icon

// Load thumbnail (path dari camera)
        if (!item.fotoPath.isNullOrEmpty()) {
            val imgFile = File(item.fotoPath)
            if (imgFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.ivFoto.setImageBitmap(bitmap)
            }
        }
    }
}
