package com.chat.listdemo.interfaces

import com.chat.listdemo.models.ChatDataModel

interface OnItemClickListener {

    fun onItemClick(item: ChatDataModel)

    fun onUnreadClick(item: ChatDataModel)

    fun onCallClick(item: ChatDataModel)

    fun onMoreClick(item: ChatDataModel)

}