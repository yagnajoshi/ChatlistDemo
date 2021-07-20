package com.chat.listdemo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.chat.listdemo.BR
import com.chat.listdemo.models.ChatDataModel
import com.chat.listdemo.R
import com.chat.listdemo.databinding.ItemChatListBinding
import com.chat.listdemo.interfaces.OnItemClickListener


class ChatListRecycleAdapter(private val list: ArrayList<ChatDataModel>) : RecyclerView.Adapter<ChatListRecycleAdapter.ViewHolder>() {

    lateinit var onItemClickListener: OnItemClickListener

    /*This method is returning the view for each item in the list*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemChatListBinding = DataBindingUtil.inflate( LayoutInflater.from(parent.context),R.layout.item_chat_list, parent, false)
        return ViewHolder(binding)
    }

    /*This method is giving the size of the list*/
    override fun getItemCount(): Int { return list.size }


    class ViewHolder(var itemRowBinding: ItemChatListBinding) : RecyclerView.ViewHolder(itemRowBinding.root) {
        /* bind() - This function will be bind the Item data to the variable @model of the view */
        fun bind(obj: Any?) {
            itemRowBinding.setVariable(BR.model, obj)
            itemRowBinding.executePendingBindings()
        }
    }

    /*Binding row item's data to the  view of the Item*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataDataModel: ChatDataModel = list[position]
        holder.bind(dataDataModel)
        holder.itemRowBinding.itemClickListener = onItemClickListener
    }


}
