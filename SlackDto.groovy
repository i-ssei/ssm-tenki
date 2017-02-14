import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
class SlackDto {
    String text

    SlackDto(text) {
        this.text = text
    }
}
