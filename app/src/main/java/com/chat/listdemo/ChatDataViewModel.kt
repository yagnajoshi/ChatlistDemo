package com.chat.listdemo

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chat.listdemo.models.ChatDataModel
import java.util.ArrayList

/*
* This ViewModel class is playing the role as Data-Provider to the ChatListActivity
* */
open class ChatDataViewModel : ViewModel() {
    /* isLoading - Needs to be public for DataBinding! */
    val isLoading = ObservableBoolean()

    /* onRefresh() - Needs to be public for DataBinding! */
    fun onRefresh() {
        isLoading.set(true)
        setDummyData()
    }

    /*
    * onReady() - It will called on the end for updating the RecyclerView.
    * It will close the refresh loader.
    * */
    fun onReady() = isLoading.set(false)

    private val items: MutableLiveData<List<ChatDataModel>> = MutableLiveData<List<ChatDataModel>>()

    /*
    * setDummyData() - It will called from ChatListActivity for adding dummy data.
    * */
    fun setDummyData() {
        val list: ArrayList<ChatDataModel> = getArrayList()
        val itemsViewDataModels: List<ChatDataModel> = list
        items.value = itemsViewDataModels
    }

    fun getItems(): LiveData<List<ChatDataModel>> {
        return items
    }


    /*Creating Dummy Data */
    private fun getArrayList() :  ArrayList<ChatDataModel>  {
        val chatDataArray: ArrayList<ChatDataModel> = ArrayList()
        chatDataArray.add(ChatDataModel("Shubh","Sure!" + getEmoji(0x1F44D),"2/7/2021",R.mipmap.avatar_4))
        chatDataArray.add(ChatDataModel("Eva", "Bye", "3/7/2021", R.mipmap.avatar_3))
        chatDataArray.add(ChatDataModel("KT", "Ok", "2/7/2021", R.mipmap.avatar_5))
        chatDataArray.add(ChatDataModel("Rachel","Great! Here you go!" + getEmoji(0x1F44C) + getEmoji(0x1F44C),"4/7/2021",R.mipmap.avatar_2))
        chatDataArray.add(ChatDataModel("Graeme", "Thanks", "1/7/2021", R.mipmap.avatar_6))
        chatDataArray.add(ChatDataModel("Shruti", "No problem", "14/6/2021", R.mipmap.avatar_7))
        chatDataArray.add(ChatDataModel("Maya", "Hi" + getEmoji(0x1F60A), "8/7/2021",R.mipmap.avatar_1))
        chatDataArray.add(ChatDataModel("Shubh","Sure!" + getEmoji(0x1F44D),"2/7/2021",R.mipmap.avatar_4))
        chatDataArray.add(ChatDataModel("Eva", "Bye", "3/7/2021", R.mipmap.avatar_3))
        chatDataArray.add(ChatDataModel("KT", "Ok", "2/7/2021", R.mipmap.avatar_5))
        chatDataArray.add(ChatDataModel("Rachel","Great! Here you go!" + getEmoji(0x1F44C) + getEmoji(0x1F44C),"4/7/2021",R.mipmap.avatar_2))
        chatDataArray.add(ChatDataModel("Graeme", "Thanks", "1/7/2021", R.mipmap.avatar_6))
        chatDataArray.add(ChatDataModel("Shruti", "No problem", "14/6/2021", R.mipmap.avatar_7))
        chatDataArray.add(ChatDataModel("Maya", "Hi" + getEmoji(0x1F60A), "8/7/2021",R.mipmap.avatar_1))
        chatDataArray.add(ChatDataModel("Shubh","Sure!" + getEmoji(0x1F44D),"2/7/2021",R.mipmap.avatar_4))
        chatDataArray.add(ChatDataModel("Eva", "Bye", "3/7/2021", R.mipmap.avatar_3))
        chatDataArray.add(ChatDataModel("KT", "Ok", "2/7/2021", R.mipmap.avatar_5))
        chatDataArray.add(ChatDataModel("Rachel","Great! Here you go!" + getEmoji(0x1F44C) + getEmoji(0x1F44C),"4/7/2021",R.mipmap.avatar_2))
        chatDataArray.add(ChatDataModel("Graeme", "Thanks", "1/7/2021", R.mipmap.avatar_6))
        chatDataArray.add(ChatDataModel("Shruti", "No problem", "14/6/2021", R.mipmap.avatar_7))
        chatDataArray.add(ChatDataModel("Maya", "Hi" + getEmoji(0x1F60A), "8/7/2021",R.mipmap.avatar_1))

        return chatDataArray
    }

    /*Method for converting Hex of Emojis to Emoji itself*/
    private fun getEmoji(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

}