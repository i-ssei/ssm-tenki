import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

import groovy.json.*
import net.gpedro.integrations.slack.*

import org.jsoup.Jsoup

@Path("/tenki")
class WeatherResource {

  @POST
  @Path("/yohou")
  @Produces(MediaType.APPLICATION_JSON)
  public SlackDto responseWeatherForecast(String text) {
    def post = parseOutgoingText(text)
    def location = (post["text"].split("\\+"))[1]
    def forecast = getWeatherForecast(location)

    return new SlackDto(forecast)
  }

  def convertWeatherIcon(weather) {
    def icon
    weather = java.net.URLDecoder.decode(weather)
    
    weather = weather.
                 replace(/雨/, ":umbrella_with_rain_drops:").
                 replace(/時々/, "／").
                 replace(/曇り/, "曇").
                 replace(/曇/, ":cloud:").
                 replace(/晴れ/, "晴").
                 replace(/晴/, ":sunny:").
                 replace(/のち/, ":arrow_right:").
                 replace(/雪/, ":snowman:").
                 replace(/暴風/, ":cyclone:")

    return weather
  }

  def getWeatherForecast(String location) {
    def cityUrl

    cityUrl = getCityUrl(location)

    if(!cityUrl) {
      return "sorry! no such point :suke: "
    }
  
    def url = "http://weather.goo.ne.jp/${cityUrl}"
    def weather_report = ""
    
    def document = Jsoup.connect(url).get()
  
    def location_name = ""
    if( document.select("h2.h2-a") ) {
      location_name = document.select("h2.h2-a")[0].text()
    } else {
      location_name = document.select("h1.fl")[0].text()
    }

    weather_report += ":suke: ${location_name}\n"
    def weather_oneday = document.select("div.weather_oneday.cx")
  
    weather_report += getOnedayForecast(weather_oneday, "today", 0)
    weather_report += getOnedayForecast(weather_oneday, "tomorrow", 0)
    
    return weather_report
  }

  def getOnedayForecast(weather_oneday, String day, index) {
    def report = ""
  
    def oneday = weather_oneday.select("div.${day}")
    def date = oneday.select("h2.mpb0")[index].text()
  
    def detail = oneday.select("div.detail")[index]
    def weather = detail.select("p.weather")[index]
    
    def temperature = detail.select("div.temperature")[index]
    def max = temperature.select("p.red")[index]
    def min = temperature.select("p.blue")[index]
    report += "${date} "
    report += "${convertWeatherIcon(weather.text())} "
    report += "${min.text()} :left_right_arrow: ${max.text()}\n"
    
    return report
  }


  def parseOutgoingText(text) {
    def hash = [:]
    println text
    text.split("&").each { line ->
      def contents = line.split("=")
      hash[contents[0]] = contents[1]
    }

    return hash
  }
  
  def getCityUrl(location) {
    def city = [:]
 
    def url = "http://weather.goo.ne.jp/"
    def document = Jsoup.connect(url).get()

    def area_all = document.select("dl.area_all.cx")[0]

    area_all.select("li").each{ area ->
      city[area.text()] = area.getElementsByTag("a").attr("href")   
    }

    return city[java.net.URLDecoder.decode(location)]
  }

}
