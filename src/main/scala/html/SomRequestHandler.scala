package io.github.betterclient.tracker
package html

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import java.net.{CookieManager, URI}

def request(region: String): Document =
    val client = HttpClient
        .newBuilder()
        .cookieHandler(CookieManager())
        .build()

    val request = HttpRequest
        .newBuilder()
        .uri(URI.create(s"https://summer.hackclub.com/shop?region=$region"))
        .header("Cookie", config.cookie)
        .GET()
        .build()

    val resp = client.send(request, BodyHandlers.ofString())
    Jsoup.parse(resp.body())
