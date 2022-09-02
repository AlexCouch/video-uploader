import csstype.*
import emotion.react.css
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import react.FC
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useState

val useUploadProgress: ()->(Triple<Int, HttpStatusCode, suspend (String, ByteArray)->Unit>) = {
    val (progress, setProgress) = useState<Int>(0)
    val (response, setResponse) = useState<HttpStatusCode>(HttpStatusCode.OK)

    val uploadVideo: suspend (String, ByteArray)->Unit = { name, videoContent ->
        jsonClient.post(endpoint + Routes.UPLOAD) {
            headers{
                contentType(ContentType.MultiPart.FormData)
                accept(ContentType.Video.MP4)
            }
            setBody(MultiPartFormDataContent(
                formData{
                    append("video", videoContent, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=${name}")
                    })
                }
            ))
            onUpload { bytesSentTotal, contentLength ->
                val progress = ((bytesSentTotal.toFloat() / contentLength.toFloat()) * 100).toInt()
                setProgress(progress)
            }
        }.let{ response ->
            setResponse(response.status)
        }
    }
    Triple(progress, response, uploadVideo)
}

external interface RenderUploadProgressProps: Props{
    var progress: Int
}

val RenderUploadProgress = FC<RenderUploadProgressProps> { props ->
    div{
        css{
            height = 5.px
            width = 100.pct
            backgroundColor = Color("#125E07ff")
            borderRadius = 40.px
            margin = 50.px
        }
        div{
            css{
                height = 100.pct
                width = props.progress.pct
                borderRadius = 40.px
                backgroundColor = Color("#39FF14ff")
            }
            span{
                css{
                    paddingLeft = (props.progress + 10).em
                    color = Color("#000000ff")
                    fontWeight = FontWeight.bolder
                }
                +"${props.progress}%"
            }
        }
    }
}