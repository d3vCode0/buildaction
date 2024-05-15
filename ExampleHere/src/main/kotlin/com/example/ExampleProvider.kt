package com.example

import com.lagradost.cloudstream3.*
import org.jsoup.nodes.Element


class ExampleAPi : MainAPI() {
    override var mainUrl = "https://ww3.animerco.org"
    override var name = "Animerco"
    override val hasMainPage = true
    override val hasDownloadSupport = true
    override var lang = "ar"
    override val supportedTypes = setOf(
        TvType.Anime,
        TvType.AnimeMovie
    )

    override val mainPage = mainPageOf(
        "${mainUrl}/episodes/page/" to "Episodes",
    )


    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val doc = app.get("$mainUrl/").document
        val homeList = doc.select("div.page-content")
            .mapNotNull {
                val title = "Episodes"
                val list =
                    it.select("div.row div").map {
                            anime -> anime.toSearchResponse()
                    }
                HomePageList(title, list, isHorizontalImages = true)
            }
        return newHomePageResponse(homeList, hasNext = false)
    }

    private fun Element.toSearchResponse(): SearchResponse {
        val title = select("div.info h3").text()
        val href = select("a").attr("href")
        val posterUrl = select("a").attr("data-src")
        return newAnimeSearchResponse(
            title,
            href,
            TvType.Anime,
        ) {
            this.posterUrl = posterUrl
        }
    }


}
