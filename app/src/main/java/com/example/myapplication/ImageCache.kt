package com.example.myapplication

import android.graphics.Bitmap

class ImageCache private constructor() {
    var teacherImage: Bitmap? = null
        private set
    var studentImage: Bitmap? = null
        private set

    private val teacherImages = mutableListOf<Bitmap>()
    private val studentImages = mutableListOf<Bitmap>()

    fun addTeacherImage(image: Bitmap) {
        teacherImages.add(image)
        teacherImage = image
    }

    fun getTeacherImages(): List<Bitmap> = teacherImages

    fun addStudentImage(image: Bitmap) {
        studentImages.add(image)
        studentImage = image
    }

    fun getStudentImages(): List<Bitmap> = studentImages

    companion object {
        private var instance: ImageCache? = null
        fun getInstance(): ImageCache {
            if (instance == null) {
                instance = ImageCache()
            }
            return instance!!
        }
    }
}
