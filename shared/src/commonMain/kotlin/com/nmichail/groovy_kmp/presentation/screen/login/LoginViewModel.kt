import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nmichail.groovy_kmp.domain.usecases.LoginUseCase
import com.nmichail.groovy_kmp.domain.models.AuthResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    private var lastAuthResponse: AuthResponse? = null

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        isLoading = true
        errorMessage = null
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = loginUseCase(email, password)
                lastAuthResponse = response
                if (response.token != null) {
                    onResult(true)
                } else {
                    errorMessage = response.error ?: "Unknown error"
                    onResult(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Network error"
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }

    fun getUser() = lastAuthResponse?.user
    fun getToken() = lastAuthResponse?.token
}