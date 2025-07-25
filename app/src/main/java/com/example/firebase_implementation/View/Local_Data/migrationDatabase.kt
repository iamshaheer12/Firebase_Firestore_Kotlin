import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Adding the new column "synced" with INTEGER type for Boolean values
        database.execSQL("ALTER TABLE note_table ADD COLUMN synced INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the deleted_notes table
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS 'deleted_notes' (" +
                    "'id' TEXT PRIMARY KEY NOT NULL, " +
                    "'userId' TEXT NOT NULL)"
        )
    }
}
