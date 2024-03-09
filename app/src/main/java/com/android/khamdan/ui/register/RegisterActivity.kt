package com.android.khamdan.ui.register

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.khamdan.R
import com.android.khamdan.databinding.ActivityRegisterBinding
import com.android.khamdan.util.FlowViewExt.safeCollectEvent
import com.google.android.material.snackbar.Snackbar
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
class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val viewModel: RegisterViewModel by viewModels()

    private val state: StateFlow<RegisterState> by lazy { viewModel.registerState }
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()
        setupRoleSpinner()
        observeErrorMessageEvent()
        observeOpenReviewEvent()
        setupFormValidationAndRegister()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = getString(R.string.register)
            setDisplayHomeAsUpEnabled(true)
        }
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
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }

    private fun setupFormValidationAndRegister() {
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
            RegisterState(
                username = username,
                email = email,
                password = password,
                role = role,
            )
        }.subscribeBy { state -> viewModel.updateState(state) }
            .addTo(disposables)

        binding.buttonRegister.setOnClickListener {
            viewModel.register()
        }
    }

    private fun observeOpenReviewEvent() = state
        .map { it.successRegisterEvent }
        .safeCollectEvent(this) {
            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show()
            finish()
        }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}
