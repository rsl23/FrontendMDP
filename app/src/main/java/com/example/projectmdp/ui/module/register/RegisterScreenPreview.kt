import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.projectmdp.data.model.auth.RegisterDto
import com.example.projectmdp.data.repository.AuthRepository
import com.example.projectmdp.ui.module.register.RegisterScreen
import com.example.projectmdp.ui.module.register.RegisterViewModel

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val previewViewModel = object : RegisterViewModel(DummyAuthRepository()) {
    }

    MaterialTheme {
        RegisterScreen(viewModel = previewViewModel, navController = rememberNavController())
    }
}
