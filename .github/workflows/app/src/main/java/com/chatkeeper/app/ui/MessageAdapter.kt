package com.chatkeeper.app.ui
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chatkeeper.app.data.MessageEntity
import com.chatkeeper.app.databinding.ItemMessageBinding
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.VH>() {
    private var items: List<MessageEntity> = emptyList()
    private val fmt = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    fun submitList(list: List<MessageEntity>) { items = list; notifyDataSetChanged() }
    override fun getItemCount() = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    inner class VH(private val b: ItemMessageBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: MessageEntity) {
            b.tvSender.text = item.sender
            b.tvMessage.text = item.message
            b.tvTime.text = fmt.format(Date(item.timestamp))
        }
    }
}
