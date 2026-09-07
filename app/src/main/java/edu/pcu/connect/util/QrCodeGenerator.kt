package edu.pcu.connect.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Renders [content] as a square QR bitmap. Used by the Digital ID screen to
 * stand in for the "secure QR-based Digital ID" described in the brief -
 * in a real deployment this would encode a server-issued, rotating token
 * instead of a plain string.
 */
fun generateQrImageBitmap(content: String, sizePx: Int = 512): ImageBitmap {
    val writer = QRCodeWriter()
    val matrix = writer.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    for (x in 0 until sizePx) {
        for (y in 0 until sizePx) {
            bitmap.setPixel(x, y, if (matrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
        }
    }
    return bitmap.asImageBitmap()
}
