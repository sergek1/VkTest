package com.example.vktest

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_folder.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class FolderActivity : AppCompatActivity() {
    private lateinit var filesAndFolders: List<File>
    private lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        val path = intent.getStringExtra("path")
        val root = path?.let { File(it) }
        filesAndFolders = root?.listFiles()?.toList() as List<File>
        if (filesAndFolders.isEmpty()) {
            tvNoFilesInFolder.visibility = View.VISIBLE
            return
        }
        tvNoFilesInFolder.visibility = View.INVISIBLE
        adapter = MyAdapter(this, filesAndFolders)
        rvFolder.adapter = adapter
        rvFolder.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_sort, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.AscSize -> filesAndFolders = filesAndFolders.sortedBy { it ->
                it.walkTopDown()
                    .map { it.length() }
                    .sum() }
            R.id.AscDate -> filesAndFolders = filesAndFolders.sortedBy { it.lastModified() }
            R.id.AscExtension -> filesAndFolders = filesAndFolders.sortedBy { it.extension }
            R.id.DescSize -> filesAndFolders = filesAndFolders.sortedBy { it ->
                it.walkTopDown()
                    .map { it.length() }
                    .sum() }.reversed()
            R.id.DescDate -> filesAndFolders = filesAndFolders.sortedBy { it.lastModified() }.reversed()
            R.id.DescExtension -> filesAndFolders = filesAndFolders.sortedBy { it.extension }.reversed()
        }
        updateAdapter()
        return super.onOptionsItemSelected(item)
    }

    private fun updateAdapter() {
        adapter.filesAndFolders =  filesAndFolders
        rvFolder.adapter = adapter
        rvFolder.layoutManager = LinearLayoutManager(this)
        adapter.notifyDataSetChanged()
    }
}