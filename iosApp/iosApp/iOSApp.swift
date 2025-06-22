import SwiftUI
import shared

@main
struct iOSApp: App {
	init() {
		KoinInitializer().doInit()
	}

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}