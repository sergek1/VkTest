package com.example.vktest

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 111
    private lateinit var filesAndFolders: List<File>
    private lateinit var adapter: MyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkPermission()) {
            val path = Environment.getExternalStorageDirectory().path
            val root = File(path)
            filesAndFolders = root.listFiles()?.toList() as List<File>
            if (filesAndFolders.isEmpty()) {
                tvNoFiles.visibility = View.VISIBLE
                return
            }
            tvNoFiles.visibility = View.INVISIBLE
            adapter = MyAdapter(this, filesAndFolders)
            recycler_view.adapter = adapter
            recycler_view.layoutManager = LinearLayoutManager(this)
        } else {
            requestPermission()
        }
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
        tvNoFiles.visibility = View.INVISIBLE
        adapter.filesAndFolders =  filesAndFolders
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter.notifyDataSetChanged()
    }

    private fun checkPermission() : Boolean {
        val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return (result == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        val permissionsList = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) { //
            Toast.makeText(this, "Storage permission is required. Please allow from settings", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(this, permissionsList, REQUEST_CODE)
        }
    }
}


