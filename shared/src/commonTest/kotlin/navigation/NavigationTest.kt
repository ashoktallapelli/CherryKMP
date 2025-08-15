package navigation

import com.cherry.kmp.ui.navigation.DeepLinkData
import com.cherry.kmp.ui.navigation.NavigationRoutes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NavigationTest {
    
    @Test
    fun testArticleRouteCreation() {
        val articleId = 123
        val route = NavigationRoutes.ArticleDetail.createRoute(articleId)
        assertEquals("article/$articleId", route)
    }
    
    @Test
    fun testDeepLinkParsingArticle() {
        val url = "https://cherry.kmp/article/123"
        val deepLinkData = DeepLinkData.fromUrl(url)
        
        assertNotNull(deepLinkData)
        assert(deepLinkData is DeepLinkData.Article)
        assertEquals(123, (deepLinkData as DeepLinkData.Article).articleId)
    }
    
    @Test
    fun testDeepLinkParsingCategory() {
        val url = "https://cherry.kmp/category/technology"
        val deepLinkData = DeepLinkData.fromUrl(url)
        
        assertNotNull(deepLinkData)
        assert(deepLinkData is DeepLinkData.Category)
        assertEquals("technology", (deepLinkData as DeepLinkData.Category).category)
    }
    
    @Test
    fun testDeepLinkParsingSearch() {
        val url = "https://cherry.kmp/search?query=kotlin&category=tech"
        val deepLinkData = DeepLinkData.fromUrl(url)
        
        assertNotNull(deepLinkData)
        assert(deepLinkData is DeepLinkData.Search)
        assertEquals("kotlin", (deepLinkData as DeepLinkData.Search).query)
        assertEquals("tech", deepLinkData.category)
    }
    
    @Test
    fun testDeepLinkParsingSection() {
        val url = "https://cherry.kmp/headlines"
        val deepLinkData = DeepLinkData.fromUrl(url)
        
        assertNotNull(deepLinkData)
        assert(deepLinkData is DeepLinkData.Section)
        assertEquals("headlines", (deepLinkData as DeepLinkData.Section).section)
    }
    
    @Test
    fun testInvalidDeepLink() {
        val url = "https://cherry.kmp/invalid/path"
        val deepLinkData = DeepLinkData.fromUrl(url)
        
        assertNull(deepLinkData)
    }
    
    @Test
    fun testCustomSchemeDeepLink() {
        val url = "cherrykmp://article/456"
        val deepLinkData = DeepLinkData.fromUrl(url)
        
        assertNotNull(deepLinkData)
        assert(deepLinkData is DeepLinkData.Article)
        assertEquals(456, (deepLinkData as DeepLinkData.Article).articleId)
    }
    
    @Test
    fun testDeepLinkToNavigationRoute() {
        val deepLinkData = DeepLinkData.Article(123)
        val navigationRoute = deepLinkData.toNavigationRoute()
        
        assertEquals("article/123", navigationRoute)
    }
    
    @Test
    fun testSectionDeepLinkToNavigationRoute() {
        val deepLinkData = DeepLinkData.Section("everything")
        val navigationRoute = deepLinkData.toNavigationRoute()
        
        assertEquals("everything", navigationRoute)
    }
}