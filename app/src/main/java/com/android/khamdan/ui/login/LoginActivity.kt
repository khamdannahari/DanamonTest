package com.android.khamdan.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.khamdan.R
import com.android.khamdan.databinding.ActivityLoginBinding
import com.android.khamdan.ui.register.RegisterActivity
import com.android.khamdan.util.FlowViewExt.safeCollectEvent
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by viewModels()

    private val state: StateFlow<LoginState> by lazy { viewModel.loginState }
    private val disposables = CompositeDisposable()

    private lateinit var webSocketClient: WebSocketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()
        observeErrorMessageEvent()
        observeSuccessLoginEvent()
        setupFormValidation()
        setupLoginButton()
        setupRegisterText()
        setupWebSocket()
    }

    private fun setupActionBar() {
        supportActionBar?.title = getString(R.string.login)
    }

    private fun observeErrorMessageEvent() = state
        .map { it.errorMessageEvent }
        .safeCollectEvent(this) {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        }

    private fun setupFormValidation() {
        val emailObservable = binding.editTextEmail.textChanges()
            .map { it.toString() }
        val passwordObservable = binding.editTextPassword.textChanges()
            .map { it.toString() }

        Observable.combineLatest(
            emailObservable,
            passwordObservable,
        ) { email, password ->
            LoginState(
                email = email,
                password = password,
            )
        }.subscribeBy { state -> viewModel.updateState(state) }
            .addTo(disposables)
    }

    private fun setupLoginButton() {
        binding.buttonLogin.setOnClickListener {
            viewModel.login()
        }
    }

    private fun setupRegisterText() {
        binding.textRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }

    private fun setupWebSocket() {
        val uri = URI("wss://echo.websocket.org")
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) = Unit
            override fun onClose(code: Int, reason: String?, remote: Boolean) = Unit
            override fun onError(ex: Exception?) = Unit

            override fun onMessage(message: String?) {
                runOnUiThread {
                    Toast.makeText(
                        this@LoginActivity,
                        message.orEmpty(), Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        webSocketClient.connect()
    }

    private fun observeSuccessLoginEvent() = state
        .map { it.successLoginEvent }
        .safeCollectEvent(this) {
            webSocketClient.send(getString(R.string.login_success))
        }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        webSocketClient.close()
    }
}
