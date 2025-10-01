package com.uitopic.restockmobile.core.cloudinary.repositories

import android.net.Uri
import java.io.File

interface ImageUploadRepository {
    suspend fun uploadImage(imageUri: Uri): Result<String>
    suspend fun uploadImage(imageFile: File): Result<String>
}