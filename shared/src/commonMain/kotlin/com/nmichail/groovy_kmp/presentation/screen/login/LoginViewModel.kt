import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nmichail.groovy_kmp.domain.usecases.LoginUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        Log.i("LoginViewModel", "=== НАЧАЛО ЛОГИНА ===")
        Log.i("LoginViewModel", "Email: $email, Password: $password")
        
        isLoading = true
        errorMessage = null
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.i("LoginViewModel", "Вызываем loginUseCase...")
                val response = loginUseCase(email, password)
                Log.i("LoginViewModel", "Получили ответ: $response")
                Log.i("LoginViewModel", "Token: ${response.token}")
                Log.i("LoginViewModel", "User: ${response.user}")
                
                if (response.token != null) {
                    Log.i("LoginViewModel", "✅ ЛОГИН УСПЕШЕН!")
                    onResult(true)
                } else {
                    Log.e("LoginViewModel", "❌ ЛОГИН НЕУСПЕШЕН - НЕТ ТОКЕНА")
                    errorMessage = response.error ?: "Unknown error"
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "❌ ОШИБКА ПРИ ЛОГИНЕ: ${e.message}")
                e.printStackTrace()
                errorMessage = e.message ?: "Network error"
                onResult(false)
            } finally {
                isLoading = false
                Log.i("LoginViewModel", "=== КОНЕЦ ЛОГИНА ===")
            }
        }
    }
}