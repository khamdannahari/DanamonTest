package com.android.khamdan.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.khamdan.R
import com.android.khamdan.data.user.User
import com.android.khamdan.databinding.ActivityHomeBinding
import com.android.khamdan.ui.edituser.EditUserActivity
import com.android.khamdan.ui.edituser.EditUserActivity.Companion.USER_ARG
import com.android.khamdan.ui.login.LoginActivity
import com.android.khamdan.util.FlowViewExt.safeCollectEvent
import com.android.khamdan.util.FlowViewExt.safeCollectUnique
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private val viewModel: HomeViewModel by viewModels()

    private val state: StateFlow<HomeState> by lazy { viewModel.homeState }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()
        observeErrorMessageEvent()
        observePhotos()
        observeUsers()
        setupPhotosRecyclerView()
        setupUsersRecyclerView()
    }

    private fun setupActionBar() {
        supportActionBar?.title = getString(R.string.home)
    }

    private fun observeErrorMessageEvent() = state
        .map { it.errorMessageEvent }
        .safeCollectEvent(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

    private fun observePhotos() = state
        .map { it.photos }
        .safeCollectUnique(this) {
            binding.recyclerViewPhotos.isVisible = it.isNotEmpty()
            (binding.recyclerViewPhotos.adapter as PhotoAdapter).addItems(it)
        }

    private fun observeUsers() = state
        .map { it.users }
        .safeCollectUnique(this) {
            binding.recyclerViewUsers.isVisible = it.isNotEmpty()
            setupUsersRecyclerView()
        }

    private fun setupPhotosRecyclerView() = with(binding.recyclerViewPhotos) {
        adapter = PhotoAdapter(state.value.photos.toMutableList())
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!state.value.isLoading &&
                    visibleItemCount.plus(firstVisibleItemPosition) >= totalItemCount &&
                    firstVisibleItemPosition >= 0
                ) {
                    viewModel.getPhotos(viewModel.homeState.value.currentPage.inc())
                }
            }
        })
    }

    private fun setupUsersRecyclerView() {
        binding.recyclerViewUsers.adapter =
            UserAdapter(
                userList = viewModel.homeState.value.users,
                onClickEdit = ::openEditUserActivity,
                onClickDelete = ::showDeleteConfirmationDialog,
            )
    }

    private fun openEditUserActivity(user: User) {
        startActivity(
            Intent(this, EditUserActivity::class.java).apply {
                putExtra(USER_ARG, user)
            }
        )
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmationDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.logout_confirmation_title))
            .setMessage(getString(R.string.logout_confirmation_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteCurrentUser()
                openLoginActivity()
            }.setNegativeButton(getString(R.string.cancel), null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun openLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showDeleteConfirmationDialog(user: User) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_delete, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.editTextPassword)

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirmation_title))
            .setMessage(getString(R.string.delete_confirmation_message))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                val password = passwordEditText.text.toString()
                viewModel.deleteUser(user.id, password)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        dialog.show()
    }

}
