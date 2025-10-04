package com.uitopic.restockmobile.core.cloudinary.repositories

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.uitopic.restockmobile.BuildConfig
import com.uitopic.restockmobile.core.cloudinary.remote.CloudinaryApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CloudinaryImageUploadRepositoryImpl @Inject constructor(
    private val apiService: CloudinaryApiService,
    @ApplicationContext private val context: Context
) : ImageUploadRepository {

    companion object {
        private const val CLOUDINARY_FOLDER = "restock/profiles/avatars"
    }

    override suspend fun uploadImage(imageUri: Uri): Result<String> {
        return try {
            // Convertir Uri a File
            val file = uriToFile(imageUri)

            // Subir imagen
            val result = uploadImage(file)

            // Limpiar archivo temporal
            file.delete()

            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(imageFile: File): Result<String> {
        return try {
            val cloudName = BuildConfig.CLOUDINARY_CLOUD_NAME
            val uploadPreset = BuildConfig.CLOUDINARY_UPLOAD_PRESET

            if (cloudName.isEmpty() || uploadPreset.isEmpty()) {
                return Result.failure(Exception("Cloudinary credentials not configured"))
            }

            // Crear URL de upload
            val uploadUrl = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

            // Preparar el archivo para upload
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            // Upload preset
            val presetBody = uploadPreset.toRequestBody("text/plain".toMediaTypeOrNull())

            // Folder (opcional)
            val folderBody = CLOUDINARY_FOLDER.toRequestBody("text/plain".toMediaTypeOrNull())

            // Realizar upload
            val response = apiService.uploadImage(
                url = uploadUrl,
                file = body,
                uploadPreset = presetBody,
                folder = folderBody
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.secureUrl)
            } else {
                Result.failure(Exception("Upload failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open URI")

        val fileName = getFileName(uri)
        val tempFile = File(context.cacheDir, fileName)

        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }

        inputStream.close()
        return tempFile
    }

    private fun getFileName(uri: Uri): String {
        var name = "temp_image_${System.currentTimeMillis()}.jpg"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }
}