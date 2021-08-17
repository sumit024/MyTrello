package com.app_devs.mytrello.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult
import com.app_devs.mytrello.activities.MyProfile

object Constants {
    const val USERS:String="users"

    const val NAME:String="name"
    const val IMAGE:String="image"
    const val MOBILE:String="mobile"
    const val BOARDS:String="boards"
    const val ASSIGNED_TO:String="assignedTo"
    const val DOCUMENT_ID:String="documentId"
    const val TASK_LIST:String="taskList"

    const val READ_EXTERNAL_STORAGE_CODE=1
    const val IMAGE_PICK_REQUEST_CODE =2
    const val BOARD_DETAIL:String="boardDetail"
    const val ID:String="id"
    const val EMAIL:String="email"
    const val TASK_LIST_ITEM_POSITION:String="taskListItemPosition"
    const val CARD_LIST_ITEM_POSITION:String="cardListItemPosition"
    const val BOARD_MEMBERS_LIST:String="boardMembersList"
     fun showImageChooser(activity: Activity)
    {
        val intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
    }
    fun getFileExtension(activity: Activity,uri: Uri?):String?
    {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}