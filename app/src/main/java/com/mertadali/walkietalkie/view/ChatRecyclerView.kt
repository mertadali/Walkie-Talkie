package com.mertadali.walkietalkie.view

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.view.*

import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

import com.mertadali.walkietalkie.R
import com.mertadali.walkietalkie.model.Chat
import java.util.*


class ChatRecyclerView : RecyclerView.Adapter<ChatRecyclerView.ChatHolder>() {


    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2


    class ChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }

    private val diffUtil = object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem

        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem

        }

    }

    private val recyclerListDiffer = AsyncListDiffer(this@ChatRecyclerView, diffUtil)

    var chats: List<Chat>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun getItemViewType(position: Int): Int {

        val chat = chats.get(position)

        if (chat.user == FirebaseAuth.getInstance().currentUser?.email.toString()) {
            return VIEW_TYPE_MESSAGE_SENT

        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)

            return ChatHolder(view)

        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_row_right, parent, false)
            return ChatHolder(view)
        }


    }

    override fun getItemCount(): Int {
        return chats.size

    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatHolder, position: Int) {

        val textView = holder.itemView.findViewById<TextView>(R.id.chatRecyclerTextView)

        textView.text = "${chats.get(position).user} : ${chats.get(position).text}"


      holder.itemView.setOnClickListener {

            textView.text = "${chats.get(position).user} : ${chats.get(position).downloadUrl}"
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource("Audio")
            mediaPlayer.prepareAsync()
            mediaPlayer.start()

        }








































    }




}











