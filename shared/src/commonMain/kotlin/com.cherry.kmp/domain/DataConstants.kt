package com.cherry.kmp.domain

import com.cherry.kmp.domain.model.Source

object DataConstants {
    val SOURCE_LIST = listOf(
        Source("techcrunch", "TechCrunch"),
        Source("the-wall-street-journal", "The Wall Street Journal"),
        Source("google-news", "Google News"),
        Source("the-washington-post", "The Washington Post"),
        Source("newsweek", "Newsweek"),
        Source("bleacher-report", "Bleacher Report"),
        Source("financial-post", "Financial Post"),
        Source("nbc-news", "NBC News"),
        Source("cnn", "CNN"),
        Source("the-globe-and-mail", "The Globe And Mail"),
        Source("business-insider", "Business Insider"),
        Source("fortune", "Fortune")
    )

    const val COUNTRY_US = "us"
    const val COUNTRY_INDIA = "in"
    const val CATEGORY_BUSINESS = "business"
    const val PUBLISHED_AT = "publishedAt"
    const val QUERY_TELSA = "tesla"
}