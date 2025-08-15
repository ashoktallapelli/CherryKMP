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
                .onOpenURL { url in
                    handleDeepLink(url: url)
                }
                .onContinueUserActivity(NSUserActivityTypeBrowsingWeb) { userActivity in
                    if let url = userActivity.webpageURL {
                        handleDeepLink(url: url)
                    }
                }
        }
    }
    
    private func handleDeepLink(url: URL) {
        print("iOS Deep link received: \(url)")
        // For now, just log - we'll handle navigation in the shared code
    }
}
