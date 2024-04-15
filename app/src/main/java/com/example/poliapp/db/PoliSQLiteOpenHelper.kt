import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.poliapp.db.contracts.PhotosContract
import com.example.poliapp.db.contracts.ProfileContract
import com.example.poliapp.db.contracts.VideosContract

class PoliSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "PoliDB.db"

        private const val SQL_CREATE_PROFILES_TABLE =
            "CREATE TABLE ${ProfileContract.ProfileEntry.TABLE_NAME} (" +
                    "${ProfileContract.ProfileEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${ProfileContract.ProfileEntry.COLUMN_NAMES} TEXT," +
                    "${ProfileContract.ProfileEntry.COLUMN_EMAIL} TEXT," +
                    "${ProfileContract.ProfileEntry.COLUMN_OCCUPATION} TEXT," +
                    "${ProfileContract.ProfileEntry.COLUMN_IMAGE} TEXT)"

        private const val SQL_DELETE_PERFILES_TABLE = "DROP TABLE IF EXISTS ${ProfileContract.ProfileEntry.TABLE_NAME}"

        private const val SQL_CREATE_PHOTOS_TABLE =
            "CREATE TABLE ${PhotosContract.PhotosEntry.TABLE_NAME} (" +
                    "${PhotosContract.PhotosEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${PhotosContract.PhotosEntry.COLUMN_NAME} TEXT," +
                    "${PhotosContract.PhotosEntry.COLUMN_DESCRIPTION} TEXT," +
                    "${PhotosContract.PhotosEntry.COLUMN_IMAGE} TEXT)"

        private const val SQL_DELETE_PHOTOS_TABLE = "DROP TABLE IF EXISTS ${PhotosContract.PhotosEntry.TABLE_NAME}"

        private const val SQL_CREATE_VIDEOS_TABLE =
            "CREATE TABLE ${VideosContract.VideosEntry.TABLE_NAME} (" +
                    "${VideosContract.VideosEntry._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${VideosContract.VideosEntry.COLUMN_VIDEO} TEXT)"

        private const val SQL_DELETE_VIDEOS_TABLE = "DROP TABLE IF EXISTS ${VideosContract.VideosEntry.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_PROFILES_TABLE)
        db.execSQL(SQL_CREATE_PHOTOS_TABLE)
        db.execSQL(SQL_CREATE_VIDEOS_TABLE)
        insertDefaultPerfil(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Aquí podrías agregar código para realizar actualizaciones de la base de datos si es necesario
    }

    private fun insertDefaultPerfil(db: SQLiteDatabase) {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${ProfileContract.ProfileEntry.TABLE_NAME}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) {
            val values = ContentValues().apply {
                put(ProfileContract.ProfileEntry.COLUMN_NAMES, "Carlos Gomez")
                put(ProfileContract.ProfileEntry.COLUMN_EMAIL, "calegomez@poligran.edu.co")
                put(ProfileContract.ProfileEntry.COLUMN_OCCUPATION, "Ingeniero de sistemas")
                put(ProfileContract.ProfileEntry.COLUMN_IMAGE, "")
            }
            db.insert(ProfileContract.ProfileEntry.TABLE_NAME, null, values)
        }
    }

    private fun insertPhoto(db:SQLiteDatabase,nombre:String,descripcion:String,imagen:String){

        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${PhotosContract.PhotosEntry.TABLE_NAME}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if(count== 0) {
            val db = writableDatabase
            val valores = ContentValues().apply {

                put(PhotosContract.PhotosEntry.COLUMN_NAME, nombre)
                put(PhotosContract.PhotosEntry.COLUMN_DESCRIPTION, descripcion)
                put(PhotosContract.PhotosEntry.COLUMN_IMAGE, imagen)

            }
            db.insert(PhotosContract.PhotosEntry.TABLE_NAME, null, valores)
            db.close()
        }
    }
}