package com.android.khamdan.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.khamdan.R
import com.android.khamdan.data.user.User

class UserAdapter(
    private val userList: List<User>,
    private val onClickEdit: (User) -> Unit,
    private val onClickDelete: (User) -> Unit,
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textId: TextView = itemView.findViewById(R.id.textId)
        val textUsername: TextView = itemView.findViewById(R.id.textUsername)
        val textEmail: TextView = itemView.findViewById(R.id.textEmail)
        val textRole: TextView = itemView.findViewById(R.id.textRole)
        val iconEdit: AppCompatImageView = itemView.findViewById(R.id.iconEdit)
        val iconDelete: AppCompatImageView = itemView.findViewById(R.id.iconDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textId.text = "ID: ${currentUser.id}"
        holder.textUsername.text = "Username: ${currentUser.username}"
        holder.textEmail.text = "Email: ${currentUser.email}"
        holder.textRole.text = "Role: ${currentUser.role}"
        holder.iconEdit.setOnClickListener {
            onClickEdit(currentUser)
        }
        holder.iconDelete.setOnClickListener {
            onClickDelete(currentUser)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
