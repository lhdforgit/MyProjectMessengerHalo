/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.CursorLoader
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.DocumentsContract.getDocumentId
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.File
import java.util.*


object FileUtils {

    private fun loadBitmap(context: Context, imageUri: Uri): Bitmap? {
        try {
            if (Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    imageUri
                )
                return bitmap
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                return bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getFileWithUri(context: Context, uri: Uri?): File? {
        if (uri == null) return null
        val pathFile = uri.toString()
        var file: File? = null
        try {
            file = getFileLocal(context, uri)
            if (file != null && file.exists()) return file
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (file == null) {
            val path = getPath(context, uri)
            if (!TextUtils.isEmpty(path)) {
                file = File(path)
                if (file.exists()) return file
            }
            if (uri.path != null) {
                file = File(uri.path)
                if (file.exists()) return file
            }
            file = File(uri.toString())
            if (file.exists()) return file
            file = File(pathFile)
            if (file.exists()) return file
            val s = pathFile.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (s.size > 0) {
                file = File(s[s.size - 1])
                if (file.exists()) return file
            }
            if (file.exists()) return file
        }
        return file
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        val mimeType: String?
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr = context.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase(Locale.ROOT)
            )
        }
        return mimeType
    }

    fun getImageSize(context: Context, uri: Uri?): Point {
        var imageWidth = 0
        var imageHeight = 0
        try {
            uri?.run {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(
                    context.getContentResolver().openInputStream(this),
                    null,
                    options
                )
                imageHeight = options.outHeight
                imageWidth = options.outWidth
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return Point(imageWidth, imageHeight)
    }


    private fun getFileLocal(context: Context, uri: Uri?): File? {
        if (uri != null) {
            val path: String? =
                getPath(context, uri)
            if (path != null && isLocal(path)) {
                return File(path)
            }
        }
        return null
    }

    private fun isLocal(url: String?): Boolean {
        return url != null && !url.startsWith("http://") && !url.startsWith("https://")
    }

    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        var result: String? = null
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) { // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                result = getPathExternalStorageDocumentFromURI(context, uri)
            } else if (isDownloadsDocument(uri)) {
                result = getPathDownloadDocumentFromURI(context, uri)
            } else if (isMediaDocument(uri)) {
                result =  getPathMediaDocumentFromURI(context, uri)
            }
            if(result ==null ) result  = getRealPathFromURI(context, uri)
            if(result ==null ) result  = getPathFromURI(context, uri)
            if(result ==null ) result  = getVideoPathFromURI(context, uri)
            if(result ==null ) result  = getImagePathFromURI(context, uri)
            if(result ==null ) result  = getAudioPathFromURI(context, uri)
            if(result ==null ) result  = getFilePathFromURI(context, uri)
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            result =  getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            result =  uri.path
        }
        return result
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


    private fun getPathExternalStorageDocumentFromURI(context: Context, uri: Uri): String? {
        try {
            val docId = getDocumentId(uri);
            if (docId.startsWith("raw:")) {
                return docId.replaceFirst("raw:", "");
            }
            val split = docId.split(":");
            val type = split[0];

            if ("primary".contentEquals(type)) {
                return Environment.getExternalStorageDirectory().path + "/" + split[1];
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getPathDownloadDocumentFromURI(context: Context, uri: Uri): String? {
        try {
            val id = getDocumentId(uri)
            if (id.startsWith("raw:")) {
                return id.replaceFirst("raw:", "");
            }
            if (id.startsWith("msf:")) {
                val type = getMimeType(context, uri)
                when {
                    type?.contains("image") == true -> {
                        return getImagePathFromURI(context, uri)
                    }
                    type?.contains("video") == true -> {
                        return getVideoPathFromURI(context, uri)
                    }
                    type?.contains("audio") == true -> {
                        return getAudioPathFromURI(context, uri)
                    }
                    else -> {
                        return getFilePathFromURI(context, uri) ?: getPathFromURI(context, uri)
                    }
                }
            }
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )
            return getDataColumn(
                context,
                contentUri,
                null,
                null
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getPathMediaDocumentFromURI(context: Context, uri: Uri): String? {
        try {
            val docId = getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(
                context,
                contentUri,
                selection,
                selectionArgs
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getPathFromURI(context: Context, uri: Uri): String? {
        var realPath: String? = null
        // SDK < API11
        try {
            if (Build.VERSION.SDK_INT < 11) {
                val proj =
                    arrayOf(MediaStore.Images.Media.DATA)
                @SuppressLint("Recycle") val cursor: Cursor? =
                    context.getContentResolver().query(uri, proj, null, null, null)
                var column_index = 0
                val result = ""
                if (cursor != null) {
                    column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    realPath = cursor.getString(column_index)
                }
            } else if (Build.VERSION.SDK_INT < 19) {
                val proj =
                    arrayOf(MediaStore.Images.Media.DATA)
                val cursorLoader = CursorLoader(context, uri, proj, null, null, null)
                val cursor: Cursor = cursorLoader.loadInBackground()
                if (cursor != null) {
                    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    realPath = cursor.getString(column_index)
                }
            } else {
                val wholeID = getDocumentId(uri)
                // Split at colon, use second item in the array
                val id = wholeID.split(":".toRegex()).toTypedArray()[1]
                val column =
                    arrayOf(MediaStore.Images.Media.DATA)
                // where id is equal to
                val sel = MediaStore.Images.Media._ID + "=?"
                val cursor: Cursor? = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column,
                    sel,
                    arrayOf(id),
                    null
                )
                var columnIndex = 0
                if (cursor != null) {
                    columnIndex = cursor.getColumnIndex(column[0])
                    if (cursor.moveToFirst()) {
                        realPath = cursor.getString(columnIndex)
                    }
                    cursor.close()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return realPath
    }

    private fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        try {
            var cursor: Cursor? = null
            return try {
                val proj =
                    arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                cursor.getString(column_index)
            } finally {
                cursor?.close()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getImagePathFromURI(context: Context, uri: Uri?): String? {
        var path: String? = null
        try {
            var cursor =
                context.contentResolver.query(uri!!, null, null, null, null)

            if (cursor != null) {
                cursor.moveToFirst()
                var document_id = cursor.getString(0)
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
                cursor.close()
                cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Images.Media._ID + " = ? ",
                    arrayOf(document_id),
                    null
                )
                if (cursor != null) {
                    cursor.moveToFirst()
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    cursor.close()
                }
            }
        } catch (e: Exception) {
        }
        return path
    }

    private fun getVideoPathFromURI(context: Context, uri: Uri?): String? {
        var path: String? = null
        try {
            var cursor = context.contentResolver.query(
                uri!!,
                null,
                null,
                null,
                null
            )

            if (cursor != null) {
                cursor.moveToFirst()
                var document_id = cursor.getString(0)
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
                cursor.close()
                cursor = context.contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Video.Media._ID + " = ? ",
                    arrayOf(document_id),
                    null
                )
                if (cursor != null) {
                    cursor.moveToFirst()
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    cursor.close()
                }
            }
        } catch (e: Exception) {
        }
        return path
    }

    private fun getFilePathFromURI(context: Context, uri: Uri): String? {
        var path: String? = null
        try {
            var cursor = context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )

            if (cursor != null) {
                cursor.moveToFirst()
                var document_id = cursor.getString(0)
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
                cursor.close()
                cursor = context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    MediaStore.Audio.Media._ID + " = ? ",
                    arrayOf(document_id),
                    null
                )
                if (cursor != null) {
                    cursor.moveToFirst()
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    cursor.close()
                }
            }
        } catch (e: Exception) {
        }
        return path

    }

    private fun getAudioPathFromURI(context: Context, uri: Uri): String? {
        var path: String? = null
        try {
            var cursor = context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )

            if (cursor != null) {
                cursor.moveToFirst()
                var document_id = cursor.getString(0)
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
                cursor.close()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    cursor = context.contentResolver.query(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        null,
                        MediaStore.Downloads._ID + " = ? ",
                        arrayOf(document_id),
                        null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()
                        path = cursor.getString(cursor.getColumnIndex(MediaStore.Downloads.DATA))
                        cursor.close()
                    }
                }
            }
        } catch (e: Exception) {
        }
        return path

    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        try {
            val column = "_data"
            val projection = arrayOf(
                column
            )
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        try {
            if (uri.getScheme().equals("content")) {
                val cursor = context.getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor?.close();
                }
            }
            if (result == null) {
                result = uri.getPath();
                val cut: Int = result?.lastIndexOf('/') ?: -1
                if (cut != -1) {
                    result = result?.substring(cut + 1)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result;

    }

    fun getFileSize(context: Context, uri: Uri): Int {
        try {
            val returnCursor =
                context.contentResolver.query(uri, null, null, null, null)
            returnCursor?.run {
                val sizeIndex = this.getColumnIndex(OpenableColumns.SIZE)
                this.moveToFirst()
                val size = this.getLong(sizeIndex)
                this.close()
                return size.toInt()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return 1
    }
}