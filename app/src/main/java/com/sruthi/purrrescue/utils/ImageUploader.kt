import android.content.Context
import android.net.Uri
import android.util.Base64
import com.sruthi.purrrescue.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object ImgUploader {

    private const val API_KEY = Constants.IMGBB_API_KEY
    private val client = OkHttpClient()

    suspend fun uploadImage(context: Context, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: throw Exception("Couldn't read image")
            val imageBytes = inputStream.readBytes()
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            val requestBody = FormBody.Builder()
                .add("key", API_KEY)
                .add("image", base64Image)
                .build()

            val request = Request.Builder()
                .url("https://api.imgbb.com/1/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: throw Exception("Empty response from ImgBB")

            if (!response.isSuccessful) {
                throw Exception("Image upload failed: $responseBody")
            }

            val json = JSONObject(responseBody)
            json.getJSONObject("data").getString("url")
        }
    }
}