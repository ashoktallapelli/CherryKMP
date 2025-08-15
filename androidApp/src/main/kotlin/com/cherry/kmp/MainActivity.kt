package com.cherry.kmp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cherry.kmp.ui.navigation.DeepLinkData

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle deep link from intent
        val deepLinkData = extractDeepLinkData(intent)
        
        setContent {
            MainView(deepLinkData = deepLinkData)
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        
        // Handle deep links when app is already running
        val deepLinkData = extractDeepLinkData(intent)
        if (deepLinkData != null) {
            Log.d("MainActivity", "Received new intent with deep link: $deepLinkData")
            // The navigation will be handled in the Compose UI
            setIntent(intent)
            recreate() // Recreate to pass new deep link data
        }
    }
    
    private fun extractDeepLinkData(intent: Intent): DeepLinkData? {
        val action = intent.action
        val data: Uri? = intent.data
        
        if (action == Intent.ACTION_VIEW && data != null) {
            Log.d("MainActivity", "Deep link received: $data")
            
            return when {
                // Handle article deep links: https://cherry.kmp/article/123
                data.pathSegments.size >= 2 && data.pathSegments[0] == "article" -> {
                    val articleId = data.pathSegments[1].toIntOrNull()
                    if (articleId != null) {
                        DeepLinkData.Article(articleId)
                    } else {
                        Log.w("MainActivity", "Invalid article ID in deep link: ${data.pathSegments[1]}")
                        null
                    }
                }
                
                // Handle category deep links: https://cherry.kmp/category/technology
                data.pathSegments.size >= 2 && data.pathSegments[0] == "category" -> {
                    val category = data.pathSegments[1]
                    DeepLinkData.Category(category)
                }
                
                // Handle source deep links: https://cherry.kmp/source/bbc-news
                data.pathSegments.size >= 2 && data.pathSegments[0] == "source" -> {
                    val sourceId = data.pathSegments[1]
                    DeepLinkData.Source(sourceId)
                }
                
                // Handle search deep links: https://cherry.kmp/search?query=kotlin
                data.path == "/search" && data.getQueryParameter("query") != null -> {
                    val query = data.getQueryParameter("query")!!
                    val category = data.getQueryParameter("category") ?: ""
                    DeepLinkData.Search(query, category)
                }
                
                // Handle main sections: https://cherry.kmp/headlines
                data.pathSegments.size == 1 -> {
                    when (data.pathSegments[0]) {
                        "headlines" -> DeepLinkData.Section("headlines")
                        "sources" -> DeepLinkData.Section("sources")
                        "everything" -> DeepLinkData.Section("everything")
                        else -> {
                            Log.w("MainActivity", "Unknown section in deep link: ${data.pathSegments[0]}")
                            null
                        }
                    }
                }
                
                // Handle custom scheme: cherrykmp://article/123
                data.scheme == "cherrykmp" -> {
                    when {
                        data.pathSegments.size >= 2 && data.pathSegments[0] == "article" -> {
                            val articleId = data.pathSegments[1].toIntOrNull()
                            if (articleId != null) {
                                DeepLinkData.Article(articleId)
                            } else null
                        }
                        else -> null
                    }
                }
                
                else -> {
                    Log.w("MainActivity", "Unhandled deep link: $data")
                    null
                }
            }
        }
        
        return null
    }
}