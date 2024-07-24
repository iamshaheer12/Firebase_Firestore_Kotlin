import com.example.firebase_implementation.View.Local_Data.NoteEntity
import com.example.firebase_implementation.View.Model.Note

sealed class NoteData {
    data class NoteType(val note: Note) : NoteData()
    data class NoteEntityType(val noteEntity: NoteEntity) : NoteData()
}
