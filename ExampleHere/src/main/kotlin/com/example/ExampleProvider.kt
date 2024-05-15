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

    private fun Element.toSearchRespone(): SearchRespone {
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
