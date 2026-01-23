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

class LaporanAdapter : RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {
    private var listAbsen: List<Absen> = emptyList()

    fun updateData(newList: List<Absen>) {
        listAbsen = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFoto: ImageView = itemView.findViewById(R.id.imgFoto)
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvKoordinat: TextView = itemView.findViewById(R.id.tvKoordinat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_absen, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val absen = listAbsen[position]
        holder.tvNama.text = "Pegawai ${absen.nama ?: "Unknown"}"
        holder.tvTanggal.text = absen.tanggal
        holder.tvKoordinat.text = "Lat: ${absen.latitude}, Lng: ${absen.longitude}"

        val bitmap = BitmapFactory.decodeFile(absen.fotoPath)
        holder.imgFoto.setImageBitmap(bitmap)
    }

    override fun getItemCount() = listAbsen.size
}

