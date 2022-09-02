import csstype.*
import emotion.react.css
import io.ktor.client.call.*
import io.ktor.client.fetch.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.Headers
import io.ktor.http.content.*
import io.ktor.util.Identity.decode
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.js.timers.setInterval
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.files.File
import org.w3c.files.FileReader
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import kotlin.js.iterator
import kotlin.time.Duration

val endpoint = window.location.origin
val mainScope = MainScope()

val App = FC<Props>{
    div{
        className = ClassName("flex-container")
        css{
            display = Display.flex
            alignContent = AlignContent.center
            justifyContent = JustifyContent.center
            position = Position.absolute

            height = 100.pct
            width = 100.pct

            top = 0.px
            left = 0.px

            background = Color("#353b36")
        }
        val (progress, status, upload) = useUploadProgress()

        VideoUploader{
            onVideoFileSelected = { file ->
                val reader = FileReader()
                reader.onload = {
                    val videoContent = (reader.result as String).toByteArray()
                    mainScope.launch{
                        upload(file.name, videoContent)
                    }
                }
                reader.readAsBinaryString(file)
            }
        }

        if(status == HttpStatusCode.OK){
            RenderUploadProgress{
                this.progress = progress
            }
        }else{
            p{
                +"Error: ${status.description}"
            }
        }
    }
}