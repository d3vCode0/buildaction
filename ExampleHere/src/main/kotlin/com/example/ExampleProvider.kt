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

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val items = ArrayList<HomePageList>()
        val document = if (page == 1) {
            app.get(request.data.removeSuffix("page/")).document
        } else {
            app.get(request.data + page).document
        }
        val home = document.select("div.page-content div.row div").mapNotNull {
            it.toSearchResult()
        }

        items.add(HomePageList("title", home))
        return HomePageResponse(items)

        // return HomePageResponse(
        //     arrayListOf(HomePageList(request.name, home, isHorizontalImages = true)),
        //     hasNext = true
        // )
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("div.info h3")?.text()?.trim() ?: return null
        val href = fixUrlNull(this.selectFirst("a")?.attr("href")) ?: return null
        val posterUrl = fixUrlNull(this.selectFirst("a")?.attr("data-src"))
        val ep = this.selectFirst("a.episode")?.text()?.trim() ?: return null

        return AnimeSearchResponse(
            name = "$title $ep",
            url = href,
            apiName = this@ExampleAPi.name,
            type = TvType.Anime,
            posterUrl = posterUrl
        )   
        // return newAnimeSearchResponse("$title EP: $ep", href, TvType.Anime) { this.posterUrl = posterUrl }        
    }

}