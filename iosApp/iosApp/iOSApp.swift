import UIKit
import SwiftUI
import shared

@main
struct iOSApp: App {
   init() {
       KoinCommonKt.doInitKoin()
    }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
