import SwiftUI
import shared

struct ContentView: View {
	var body: some View {
		ComposeView { App() }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}