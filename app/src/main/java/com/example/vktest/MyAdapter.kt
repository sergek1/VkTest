package com.example.vktest

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_item.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MyAdapter(
    private val context: Context,
    var filesAndFolders: List<File>
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    lateinit var type: String

    init {
        filesAndFolders = filesAndFolders.sortedBy { it.name }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filesAndFolders.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        filesAndFolders.sortedBy { it.name }
        val selectedFile = filesAndFolders[position]
        holder.itemView.apply {
            tvFileName.text = selectedFile.name
            val fileTime = Date(selectedFile.lastModified())
            val format = SimpleDateFormat("dd.MM.yyyy HH:mm")
            tvCreationDate.text = format.format(fileTime).toString()
            if (selectedFile.isDirectory) {
                val size = selectedFile
                    .walkTopDown()
                    .map { it.length() }
                    .sum()
                    .toFloat() / 1024.0 / 1024.0
                val roundSize = (size * 10000.0).roundToInt() / 10000.0

                ("${roundSize}Mb").also { tvSize.text = it }
                ivIcon.setImageResource(R.drawable.baseline_folder_24)
            } else {
                val roundSize = ((selectedFile.length().toFloat() / 1024 / 1024) * 10000.0).roundToInt() / 10000.0
                ("$roundSize Mb").also { tvSize.text = it }

                when (selectedFile.extension) {
                    "jpeg" -> { ivIcon.setImageResource(R.drawable.jpeg)
                        type = "image/*" }
                    "jpg" -> { ivIcon.setImageResource(R.drawable.jpg)
                        type = "image/*" }
                    "mp3" -> { ivIcon.setImageResource(R.drawable.mp3)
                        type = "audio/*" }
                    "mp4" -> { ivIcon.setImageResource(R.drawable.mp4)
                        type = "video/*" }
                    "pdf" -> { ivIcon.setImageResource(R.drawable.pdf)
                        type = "application/pdf" }
                    "png" -> { ivIcon.setImageResource(R.drawable.png)
                        type = "image/*" }
                    "txt" -> { ivIcon.setImageResource(R.drawable.txt)
                        type = "text/*" }
                    else -> { ivIcon.setImageResource(R.drawable.baseline_file_24)
                        type = "*/*" }
                }
            }
        }

        holder.itemView.setOnClickListener {
            if (selectedFile.isDirectory) {
                val intent = Intent(context.applicationContext, FolderActivity::class.java)
                val path = selectedFile.absolutePath
                intent.putExtra("path", path)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                try {
                    var intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.setDataAndType(Uri.parse(selectedFile.absolutePath), type)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context.applicationContext, "Cannot open the file ${e.message}", Toast.LENGTH_LONG).show()
                }

            }
        }
    }
}