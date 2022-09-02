import csstype.url
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.browser.window
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.p

val jsonClient = HttpClient{
    install(ContentNegotiation){
        json()
    }
    install(Logging)
}

fun main() {
    val container = document.createElement("div")
    document.body!!.appendChild(container)

    createRoot(container).render(App.create())
}