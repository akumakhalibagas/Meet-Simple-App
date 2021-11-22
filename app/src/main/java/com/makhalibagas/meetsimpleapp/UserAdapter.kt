package com.makhalibagas.meetsimpleapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makhalibagas.meetsimpleapp.databinding.ItemUserBinding

class UserAdapter(
    private val list: ArrayList<User>,
    private val listener: Listerners
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                user.apply {
                    tvEmail.text = email
                    tvName.text = name
                    tvChar.text = name!!.substring(0, 1)
                }

                audiocall.setOnClickListener { listener.audioMeet(user) }
                videocall.setOnClickListener { listener.videoMeet(user) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount(): Int = list.size
}

interface Listerners {
    fun audioMeet(user: User)
    fun videoMeet(user: User)
}