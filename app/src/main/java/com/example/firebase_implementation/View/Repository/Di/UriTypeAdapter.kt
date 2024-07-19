import android.net.Uri
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class UriTypeAdapter : TypeAdapter<Uri>() {

    override fun write(out: JsonWriter, value: Uri?) {
        out.value(value?.toString())
    }

    override fun read(reader: JsonReader): Uri {
        var uriString: String? = null

        // Handle different JSON token types
        when (reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                return Uri.EMPTY // Handle null case
            }
            JsonToken.STRING -> {
                uriString = reader.nextString()
            }
            JsonToken.BEGIN_OBJECT -> {
                // Handle object case, if necessary
                reader.skipValue()
            }
            else -> {
                // Handle other cases, if necessary
                reader.skipValue()
            }
        }

        return Uri.parse(uriString ?: "")
    }
}
