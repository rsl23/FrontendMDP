import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.projectmdp.data.model.auth.RegisterDto
import com.example.projectmdp.data.repository.AuthRepository
import com.example.projectmdp.ui.module.login.LoginScreen
import com.example.projectmdp.ui.module.login.LoginViewModel

class DummyAuthRepository : AuthRepository() {
    override suspend fun login(email: String, password: String): String {
        return "dummy_token_for_preview"
    }

    override suspend fun register(registerDto: RegisterDto): String {
        return "dummy_register_response"
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val previewViewModel = object : LoginViewModel() {
        override fun login() {
            // no-op for preview
        }
    }

    MaterialTheme {
        LoginScreen(viewModel = previewViewModel, navController = rememberNavController())
    }
}
