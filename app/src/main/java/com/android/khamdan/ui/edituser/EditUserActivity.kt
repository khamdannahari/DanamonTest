package com.android.khamdan.ui.edituser

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.khamdan.R
import com.android.khamdan.data.user.User
import com.android.khamdan.databinding.ActivityEditUserBinding
import com.android.khamdan.util.FlowViewExt.safeCollectEvent
import com.android.khamdan.util.ParcelableExtraExt.parcelable
import com.jakewharton.rxbinding4.widget.itemSelections
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class EditUserActivity : AppCompatActivity() {

    private val binding: ActivityEditUserBinding by lazy {
        ActivityEditUserBinding.inflate(layoutInflater)
    }

    private val viewModel: EditUserViewModel by viewModels()

    private val state: StateFlow<EditUserState> by lazy { viewModel.editUserState }
    private val disposables = CompositeDisposable()

    private val user by lazy { intent?.parcelable<User>(USER_ARG) ?: User() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()
        setInitialData()
        setupRoleSpinner()
        observeErrorMessageEvent()
        observeSuccessUpdateEvent()
        setupFormValidation()
        setupUpdateButton()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = getString(R.string.edit_user)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setInitialData() {
        viewModel.setUser(user)
    }

    private fun setupRoleSpinner() {
        binding.spinnerRole.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.role_options)
        )
    }

    private fun observeErrorMessageEvent() = state
        .map { it.errorMessageEvent }
        .safeCollectEvent(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

    private fun setupFormValidation() {
        val user = viewModel.editUserState.value
        binding.editTextUsername.setText(user.username)
        binding.editTextEmail.setText(user.email)
        binding.editTextPassword.setText(user.password)
        binding.spinnerRole.setSelection(
            if (user.role.lowercase() == "normal") 1 else 0
        )
        val usernameObservable = binding.editTextUsername.textChanges()
            .map { it.toString() }
        val emailObservable = binding.editTextEmail.textChanges()
            .map { it.toString() }
        val passwordObservable = binding.editTextPassword.textChanges()
            .map { it.toString() }
        val roleObservable = binding.spinnerRole.itemSelections()
            .map { position -> binding.spinnerRole.getItemAtPosition(position).toString() }

        Observable.combineLatest(
            usernameObservable,
            emailObservable,
            passwordObservable,
            roleObservable
        ) { username, email, password, role ->
            EditUserState(
                username = username,
                email = email,
                password = password,
                role = role,
            )
        }.subscribeBy { state ->
            viewModel.setUser(
                User(
                    username = state.username,
                    email = state.email,
                    password = state.password,
                    role = state.role,
                ),
            )
        }
            .addTo(disposables)
    }

    private fun setupUpdateButton() {
        binding.buttonUpdate.setOnClickListener {
            viewModel.update()
        }
    }

    private fun observeSuccessUpdateEvent() = state
        .map { it.successUpdateEvent }
        .safeCollectEvent(this) {
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show()
            finish()
        }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    companion object {
        const val USER_ARG = "USER_ARG"
    }
}
