package com.chat.listdemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.chat.listdemo.adapters.ChatListRecycleAdapter
import com.chat.listdemo.databinding.ActivityChatlistBinding
import com.chat.listdemo.interfaces.OnItemClickListener
import com.chat.listdemo.models.ChatDataModel
import java.util.*


class ChatListActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatListRecycleAdapter
    private var chatDataArray: ArrayList<ChatDataModel> = ArrayList()
    private lateinit var binding: ActivityChatlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatlist)
        val chatListViewModel = ViewModelProvider(this).get(ChatDataViewModel::class.java)
        binding.viewModel = chatListViewModel

        chatListViewModel.getItems().observe(this, {
            chatDataArray.clear()
            chatDataArray.addAll(it)
            chatAdapter.notifyDataSetChanged()
            chatListViewModel.onReady()
        })

        setRecyclerView()

        /*Adding Dummy data using ChatViewModel*/
        chatListViewModel.setDummyData()
    }

   /*Function for displaying ListView*/
    private fun setRecyclerView() {
        chatAdapter = ChatListRecycleAdapter(chatDataArray)
        chatAdapter.onItemClickListener = itemClickListener
        binding.chatListAdapter = chatAdapter
    }

    /*Implementation of ItemClickListener interface*/
    private val itemClickListener = object : OnItemClickListener {
        override fun onItemClick(item: ChatDataModel) { displayMessage("${item.name} clicked") }
        override fun onUnreadClick(item: ChatDataModel) { displayMessage("Unread clicked") }
        override fun onCallClick(item: ChatDataModel) { displayMessage("Call clicked") }
        override fun onMoreClick(item: ChatDataModel) { displayMessage("More clicked") }
    }

    /*Function for Displaying Toast Message*/
    private fun displayMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

}