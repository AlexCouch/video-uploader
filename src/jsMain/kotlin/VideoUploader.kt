import csstype.*
import emotion.react.css
import kotlinext.js.asJsObject
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.File
import org.w3c.files.get
import react.*
import react.dom.events.ChangeEventHandler
import react.dom.findDOMNode
import react.dom.html.InputType
import react.dom.html.ReactHTML.br
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p

//This is triggered when a video file is selected, validated, and confirmed.
//It will also switch the state of the app so that it shows the metadata form for the video upload
external interface VideoUploaderProps : Props {
    var onVideoFileSelected: (File)->Unit
}

val VideoUploader = FC<VideoUploaderProps>{ prop ->
    val inputFile = useRef<HTMLInputElement>(null)
    val (video, setVideo) = useState<File>()

    val changeHandler: ChangeEventHandler<HTMLInputElement> = {
        it.preventDefault()
        it.target.files?.let{
            it.item(it.length - 1)?.let{ file ->
                setVideo(file)
                prop.onVideoFileSelected(file)
            }
        }
    }

    div{
        className = ClassName("flex-container")
        css{
            display = Display.flex
            alignItems = AlignItems.center
            justifyContent = JustifyContent.center
            position = Position.absolute

            width = 100.pct
            height = 100.pct

            top = 0.px
            left = 0.px
        }
        input{
            type = InputType.file
            accept = ".mp4"
            id = "upload-video-input-hidden"
            onChange = changeHandler
            ref = inputFile
            css{
                display = None.none
            }
        }
        p{
            +"Select File"
            br()
            +(video?.name ?: "No video selected")
            css{
                textAlign = TextAlign.center
                color = rgba(255,255,255,1.0)
            }
            onClick = {
                inputFile.current?.click()
            }
        }

    }
}