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
        "${mainUrl}/episodes/" to "Episodes",
    )


    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val document = if (page == 1) {
            app.get(request.data).document
        } else {
            app.get(request.data + "page/$page").document
        }

        val home = document.select("div.container div.row div").mapNotNull {
            it.toSearchResponse()
        }
        return newHomePageResponse(
            HomePageList(
                name = request.name,
                home,
                isHorizontalImages = true
            )
        )
    }

    private fun Element.toSearchResponse(): SearchResponse {
        val title = this.selectFirst("div.info h3")?.text()?.trim()
        val href = fixUrlNull(this.selectFirst("a")?.attr("href"))
        val posterUrl = fixUrlNull(this.selectFirst("a")?.attr("data-src"))
        val eps = this.selectFirst("a.episode")?.text()?.trim().replace("الحلقة ", "")?.toIntOrNull()
        // val extra = this.selectFirst("div.info h4")?.text()?.trim().replace("الموسم ", "")

        return newAnimeSearchResponse(
            title.toString(),
            href.toString(),
            TvType.Anime,
        ) {
            this.posterUrl = posterUrl
            addDubStatus( false, eps )
        }
    }


}
