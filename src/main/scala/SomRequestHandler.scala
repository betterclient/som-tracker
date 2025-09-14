package io.github.betterclient.tracker

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.net.http.HttpResponse.BodyHandlers
import java.net.{CookieManager, URI}
import java.net.http.{HttpClient, HttpRequest}

def request(region: String): Document =
    val client = HttpClient
        .newBuilder()
        .cookieHandler(CookieManager())
        .build()

    val request = HttpRequest
        .newBuilder()
        .uri(URI.create(s"https://summer.hackclub.com/shop?region=$region"))
        .header("Cookie", readConfig().cookie)
        .GET()
        .build()

    val resp = client.send(request, BodyHandlers.ofString())
    Jsoup.parse(resp.body())
