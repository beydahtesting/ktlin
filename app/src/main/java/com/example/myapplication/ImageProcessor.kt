package com.example.myapplication

import android.graphics.Bitmap
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.hypot

object ImageProcessor {
    private val GREEN = Scalar(0.0, 255.0, 0.0)
    private val RED = Scalar(0.0, 0.0, 255.0)
    private const val MATCH_THRESHOLD = 30.0

    fun processImage(image: Mat): Mat {
        if (image.channels() == 4) {
            val bgr = Mat()
            Imgproc.cvtColor(image, bgr, Imgproc.COLOR_RGBA2BGR)
            return processImage(bgr)
        } else if (image.channels() == 1) {
            val bgr = Mat()
            Imgproc.cvtColor(image, bgr, Imgproc.COLOR_GRAY2BGR)
            return processImage(bgr)
        }
        val gray = Mat()
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY)
        val blurred = Mat()
        Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)
        val thresh = Mat()
        Imgproc.adaptiveThreshold(
            blurred, thresh, 255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2.0
        )
        val edges = Mat()
        Imgproc.Canny(thresh, edges, 50.0, 150.0)
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(edges, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        gray.release()
        blurred.release()
        thresh.release()
        edges.release()

        if (contours.isNotEmpty()) {
            var largestContour = contours[0]
            for (cnt in contours) {
                if (Imgproc.contourArea(cnt) > Imgproc.contourArea(largestContour)) {
                    largestContour = cnt
                }
            }
            val contour2f = MatOfPoint2f(*largestContour.toArray())
            val perimeter = Imgproc.arcLength(contour2f, true)
            val approx = MatOfPoint2f()
            Imgproc.approxPolyDP(contour2f, approx, 0.02 * perimeter, true)
            if (approx.total() == 4L) {
                val orderedPts = reorderPoints(approx)
                val dst = MatOfPoint2f(
                    Point(0.0, 0.0),
                    Point(699.0, 0.0),
                    Point(699.0, 799.0),
                    Point(0.0, 799.0)
                )
                val M = Imgproc.getPerspectiveTransform(orderedPts, dst)
                val warped = Mat()
                Imgproc.warpPerspective(image, warped, M, Size(700.0, 800.0))
                approx.release()
                orderedPts.release()
                M.release()
                dst.release()
                return warped
            }
        }
        return image.clone()
    }

    fun detectFilledCircles(image: Mat): List<Point> {
        val hsv = Mat()
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV)
        val lowerBlue = Scalar(90.0, 50.0, 50.0)
        val upperBlue = Scalar(130.0, 255.0, 255.0)
        val mask = Mat()
        Core.inRange(hsv, lowerBlue, upperBlue, mask)
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(mask, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        val filledCircles = mutableListOf<Point>()
        for (cnt in contours) {
            val area = Imgproc.contourArea(cnt)
            if (area in 200.0..5000.0) {
                val center = Point()
                val radius = FloatArray(1)
                Imgproc.minEnclosingCircle(MatOfPoint2f(*cnt.toArray()), center, radius)
                filledCircles.add(center)
            }
        }
        hsv.release()
        mask.release()
        return filledCircles
    }

    fun compareCircles(teacherCircles: List<Point>, studentCircles: List<Point>, image: Mat): Mat {
        val correctMatches = mutableListOf<Point>()
        val unmatchedStudent = studentCircles.toMutableList()
        for (t in teacherCircles) {
            var matchFound = false
            val iterator = unmatchedStudent.iterator()
            while (iterator.hasNext()) {
                val s = iterator.next()
                if (hypot(t.x - s.x, t.y - s.y) < MATCH_THRESHOLD) {
                    correctMatches.add(s)
                    matchFound = true
                    iterator.remove()
                    break
                }
            }
            if (!matchFound) {
                Imgproc.circle(image, t, 20, GREEN, 2)
            }
        }
        teacherCircles.forEach { Imgproc.circle(image, it, 20, GREEN, 3) }
        correctMatches.forEach { Imgproc.circle(image, it, 20, GREEN, -1) }
        unmatchedStudent.forEach { Imgproc.circle(image, it, 20, RED, -1) }
        return image
    }

    fun drawDetectedCircles(image: Mat): Mat {
        val circles = detectFilledCircles(image)
        val output = image.clone()
        for (p in circles) {
            Imgproc.circle(output, p, 20, GREEN, 2)
        }
        return output
    }

    private fun reorderPoints(points: MatOfPoint2f): MatOfPoint2f {
        val pts = points.toArray()
        if (pts.size != 4) return points
        val ordered = arrayOfNulls<Point>(4)
        val sums = DoubleArray(4)
        val diffs = DoubleArray(4)
        for (i in pts.indices) {
            sums[i] = pts[i].x + pts[i].y
            diffs[i] = pts[i].y - pts[i].x
        }
        var tl = 0
        var br = 0
        var tr = 0
        var bl = 0
        for (i in 1 until 4) {
            if (sums[i] < sums[tl]) tl = i
            if (sums[i] > sums[br]) br = i
            if (diffs[i] < diffs[tr]) tr = i
            if (diffs[i] > diffs[bl]) bl = i
        }
        ordered[0] = pts[tl]
        ordered[1] = pts[tr]
        ordered[2] = pts[br]
        ordered[3] = pts[bl]
        return MatOfPoint2f(*ordered.requireNoNulls())
    }

    fun extractField(text: String, label: String): String {
        val modifiedText = text.replace("\\n", "\n")
        val regex = Regex(Regex.escape(label) + "\\s*(.*?)(\\n|\"|$)")
        return regex.find(modifiedText)?.groups?.get(1)?.value?.trim() ?: ""
    }

    fun extractStudentInfo(bitmap: Bitmap): JSONObject {
        return try {
            val imageBytes = ImageUtils.compressToJPEG(bitmap, 30)
            val encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP)
            val inlineData = JSONObject().apply {
                put("mime_type", "image/jpeg")
                put("data", encodedImage)
            }
            val partsArray = JSONArray().apply {
                put(JSONObject().put("inline_data", inlineData))
                put(JSONObject().put("text", "Extract only the student's name and roll number from this exam sheet image."))
            }
            val contentObject = JSONObject().apply { put("parts", partsArray) }
            val contentsArray = JSONArray().apply { put(contentObject) }
            val payload = JSONObject().apply { put("contents", contentsArray) }
            android.util.Log.d("GeminiAPI", "Payload: $payload")
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyDGalTcZxd_xWk1ZU6SQqgHl3KR5ZvKpoc")
            val conn = url.openConnection() as HttpURLConnection
            conn.doOutput = true
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.outputStream.use { os ->
                os.write(payload.toString().toByteArray(Charsets.UTF_8))
            }
            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                val errorResponse = conn.errorStream.bufferedReader().readText()
                android.util.Log.e("GeminiAPI", "HTTP Error Code: ${conn.responseCode} Response: $errorResponse")
                throw Exception("HTTP Error Code ${conn.responseCode}")
            }
            val response = conn.inputStream.bufferedReader().readText()
            android.util.Log.d("GeminiAPI", "Response: $response")
            JSONObject(response)
        } catch (e: Exception) {
            android.util.Log.e("GeminiAPI", "Error extracting student info", e)
            JSONObject().apply {
                put("name", "John Doe")
                put("rollNumber", "12345")
            }
        }
    }
}
