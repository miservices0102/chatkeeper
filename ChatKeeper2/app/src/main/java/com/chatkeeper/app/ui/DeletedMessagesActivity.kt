package com.chatkeeper.app.ui
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chatkeeper.app.data.AppDatabase
import com.chatkeeper.app.databinding.ActivityDeletedMessagesBinding

class DeletedMessagesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeletedMessagesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeletedMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        val adapter = MessageAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        AppDatabase.getInstance(applicationContext).messageDao().getAll().observe(this) { messages ->
            adapter.submitList(messages)
            binding.tvEmpty.visibility = if (messages.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
