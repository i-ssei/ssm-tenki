@Grab('org.jsoup:jsoup:1.7.3')
@Grab("net.gpedro.integrations.slack:slack-webhook:1.2.1")

import org.jsoup.Jsoup
import groovy.json.*
import net.gpedro.integrations.slack.*

def get_weather_forecast(String location) {

  def url = "http://weather.goo.ne.jp/weather/address/${location}/"
  def weather_report = ""
  
  def document = Jsoup.connect(url).get()

  def location_name = document.select("h2.h2-a")[0].text()
  weather_report += ":suke: ${location_name}\n"
  def weather_oneday = document.select("div.weather_oneday.cx")

  weather_report += get_oneday_forecast(weather_oneday, "today", 0)
  weather_report += get_oneday_forecast(weather_oneday, "tomorrow", 0)
  
  return weather_report
}

def get_oneday_forecast(weather_oneday, String day, index) {
  def report = ""

  def oneday = weather_oneday.select("div.${day}")
  def date = oneday.select("h2.mpb0")[index].text()

  def detail = oneday.select("div.detail")[index]
  def weather = detail.select("p.weather")[index]
  
  def temperature = detail.select("div.temperature")[index]
  def max = temperature.select("p.red")[index]
  def min = temperature.select("p.blue")[index]
  report += "${date} "
  report += "${weather.text()} "
  report += "${min.text()} :left_right_arrow: ${max.text()}\n"
  
  return report
}

def forecast = get_weather_forecast("13")

def api = new SlackApi("https://hooks.slack.com/services/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")
api.call(new SlackMessage(forecast))
