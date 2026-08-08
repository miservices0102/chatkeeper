package com.chatkeeper.app.ui
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chatkeeper.app.databinding.ItemStatusBinding

class StatusAdapter(private val onSave: (StatusItem) -> Unit) : RecyclerView.Adapter<StatusAdapter.VH>() {
    private var items: List<StatusItem> = emptyList()
    fun submitList(list: List<StatusItem>) { items = list; notifyDataSetChanged() }
    override fun getItemCount() = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    inner class VH(private val b: ItemStatusBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: StatusItem) {
            Glide.with(b.ivThumb.context).load(item.uri).centerCrop().into(b.ivThumb)
            b.btnSave.setOnClickListener { onSave(item) }
        }
    }
}
