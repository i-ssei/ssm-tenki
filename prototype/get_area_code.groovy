@Grab('org.jsoup:jsoup:1.7.3')
@Grab("net.gpedro.integrations.slack:slack-webhook:1.2.1")

import org.jsoup.Jsoup
import groovy.json.*
import net.gpedro.integrations.slack.*


def url = "http://weather.goo.ne.jp/"

def document = Jsoup.connect(url).get()

def area_all = document.select("dl.area_all.cx")[0]

def area = area_all.select("li")[0]

println area.text()
println area.getElementsByTag("a").attr("href")
