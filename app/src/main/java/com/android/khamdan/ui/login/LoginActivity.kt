package com.android.khamdan.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.khamdan.R
import com.android.khamdan.databinding.ActivityLoginBinding
import com.android.khamdan.ui.home.HomeActivity
import com.android.khamdan.ui.register.RegisterActivity
import com.android.khamdan.util.FlowViewExt.safeCollectEvent
import com.android.khamdan.util.FlowViewExt.safeCollectUnique
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

    private var webSocketClient: WebSocketClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()
        observeCurrentUser()
        observeErrorMessageEvent()
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
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

    private fun observeCurrentUser() = state
        .map { it.currentUser }
        .safeCollectUnique(this) { currentUser ->
            if (currentUser != null) {
                openHomeActivity()
                try {
                    webSocketClient?.send(getString(R.string.user_logged))
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }

    private fun openHomeActivity() {
        startActivity(
            Intent(this, HomeActivity::class.java)
        )
        finish()
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
        }.subscribeBy { state ->
            viewModel.updateForm(state.email, state.password)
        }.addTo(disposables)
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
                        getString(R.string.websocket_message)
                            .plus(message.orEmpty()),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }

        webSocketClient?.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        webSocketClient?.close()
    }
}
