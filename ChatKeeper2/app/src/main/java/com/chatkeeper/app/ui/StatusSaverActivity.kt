package com.chatkeeper.app.ui
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import com.chatkeeper.app.databinding.ActivityStatusSaverBinding

class StatusSaverActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatusSaverBinding
    private lateinit var adapter: StatusAdapter
    private val prefs by lazy { getSharedPreferences("ck_prefs", MODE_PRIVATE) }

    private val folderPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data ?: return@registerForActivityResult
        contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        prefs.edit().putString("tree_uri", uri.toString()).apply()
        loadStatuses(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusSaverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        adapter = StatusAdapter { saveStatus(it) }
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = adapter
        binding.btnPickFolder.setOnClickListener {
            folderPicker.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(
                    "content://com.android.externalstorage.documents/document/primary:Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
                ))
            })
        }
        prefs.getString("tree_uri", null)?.let { loadStatuses(Uri.parse(it)) }
    }

    private fun loadStatuses(treeUri: Uri) {
        val files = DocumentFile.fromTreeUri(this, treeUri)?.listFiles()?.filter {
            val n = it.name ?: return@filter false
            n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png") || n.endsWith(".mp4")
        } ?: emptyList()
        val items = files.map { StatusItem(it.uri, it.name ?: "status", it.name?.endsWith(".mp4") == true) }
        adapter.submitList(items)
        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun saveStatus(item: StatusItem) {
        try {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, item.name)
                put(MediaStore.MediaColumns.RELATIVE_PATH, if (item.isVideo) "Movies/ChatKeeper" else "Pictures/ChatKeeper")
            }
            val collection = if (item.isVideo) MediaStore.Video.Media.EXTERNAL_CONTENT_URI else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val dest = contentResolver.insert(collection, values) ?: return
            contentResolver.openInputStream(item.uri)?.use { i -> contentResolver.openOutputStream(dest)?.use { o -> i.copyTo(o) } }
            Toast.makeText(this, getString(com.chatkeeper.app.R.string.saved), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
