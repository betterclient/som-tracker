package io.github.betterclient.tracker

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.net.http.HttpResponse.BodyHandlers
import java.net.{CookieManager, URI}
import java.net.http.{HttpClient, HttpRequest}

def request(): Document =
    val client = HttpClient
        .newBuilder()
        .cookieHandler(CookieManager())
        .build()

    val request = HttpRequest
        .newBuilder()
        .uri(URI.create("https://summer.hackclub.com/shop"))
        .header("Cookie", readConfig().cookie)
        .GET()
        .build()

    val resp = client.send(request, BodyHandlers.ofString())
    Jsoup.parse(resp.body())
