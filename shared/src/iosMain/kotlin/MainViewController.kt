import androidx.compose.ui.window.ComposeUIViewController
import com.nmichail.groovy_kmp.presentation.App
import org.koin.mp.KoinPlatform.getKoin

fun MainViewController() = ComposeUIViewController {
    App()
} 