import SwiftUI
import shared

struct ContentView: View {
	var body: some View {
		ComposeViewControllerToSwiftUI()
			.ignoresSafeArea(.all)
	}
}

struct ComposeViewControllerToSwiftUI: UIViewControllerRepresentable {
	func makeUIViewController(context: Context) -> UIViewController {
		return MainViewControllerKt.MainViewController()
	}
	func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}