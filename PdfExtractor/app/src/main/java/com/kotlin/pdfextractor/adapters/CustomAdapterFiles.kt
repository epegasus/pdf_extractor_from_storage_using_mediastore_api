package com.kotlin.pdfextractor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.pdfextractor.R
import com.kotlin.pdfextractor.databinding.ListItemFileBinding
import com.kotlin.pdfextractor.models.FileItem

class CustomAdapterFiles : ListAdapter<FileItem, CustomAdapterFiles.CustomViewHolderEmployee>(DiffUtilsEmployees) {

    class CustomViewHolderEmployee(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListItemFileBinding.bind(itemView)
    }

    object DiffUtilsEmployees : DiffUtil.ItemCallback<FileItem>() {

        override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem.pdfFilePath == newItem.pdfFilePath
        }

        override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolderEmployee {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_file, parent, false)
        return CustomViewHolderEmployee(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolderEmployee, position: Int) {
        val item = getItem(position)
        with(holder) {
            val body = "${item.dateModified} - ${item.size}"
            binding.tvTitleListItemFile.text = item.fileName
            binding.tvBodyListItemFile.text = body
        }
    }
}