package com.ec.shop.ui.cart

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.view.View
import android.widget.LinearLayout
import androidx.collection.LruCache
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.ec.shop.constants.Constants
import com.ec.shop.data.db.entities.CartEntity
import com.ec.shop.data.repositories.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CartViewModel(
    private val application: Application,
    cartRepository: CartRepository
) : ViewModel() {

    var productData: LiveData<List<CartEntity>> = MutableLiveData()

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean>
        get() = _updateStatus

    init {
        productData = cartRepository.getData()
    }

    fun getScreenshotFromRecyclerView(
        recyclerView: RecyclerView,
        layoutTotal: LinearLayout,
        amountBitmap: Bitmap
    ): Bitmap? {
        val adapter = recyclerView.adapter
        val billBitmap: Bitmap?
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size = adapter.itemCount
            var height = 0
            val paint = Paint()
            var iHeight = 0
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmapCache = LruCache<String, Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                holder.itemView.layout(
                    0,
                    0,
                    holder.itemView.measuredWidth,
                    holder.itemView.measuredHeight
                )
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmapCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.measuredHeight
            }
            billBitmap =
                Bitmap.createBitmap(recyclerView.measuredWidth, height, Bitmap.Config.ARGB_8888)
            val billCanvas = Canvas(billBitmap)
            billCanvas.drawColor(Color.WHITE)
            for (i in 0 until size) {
                val bitmap = bitmapCache[i.toString()]
                billCanvas.drawBitmap(bitmap!!, 0f, iHeight.toFloat(), paint)
                iHeight += bitmap.height
                bitmap.recycle()
            }
            val amountCanvas = Canvas(amountBitmap)
            amountCanvas.drawBitmap(amountBitmap, 0f, layoutTotal.height.toFloat(), null)
            bigBitmap = Bitmap.createBitmap(
                recyclerView.measuredWidth,
                height + layoutTotal.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bigBitmap)
            canvas.drawBitmap(billBitmap, 0f, layoutTotal.height.toFloat(), null)
            canvas.drawBitmap(amountBitmap, 0f, 0f, null)
            billBitmap.recycle()
            amountBitmap.recycle()
        }
        return bigBitmap
    }

    fun createPdfFile(rvBitmap: Bitmap) {
        val filePath = createPdfDirectory()
        try {
            val pdfFile = "${filePath?.absolutePath}/${Constants.PDF_BILL}"
            val fOut = FileOutputStream(pdfFile)
            val document = PdfDocument()
            val pageInfo = PageInfo.Builder(
                rvBitmap.width, rvBitmap.height,
                1
            ).create()
            val page = document.startPage(pageInfo)
            rvBitmap.prepareToDraw()
            val c: Canvas
            c = page.canvas
            c.drawBitmap(rvBitmap, 0f, 0f, null)
            document.finishPage(page)
            document.writeTo(fOut)
            document.close()
            _updateStatus.value = true
        } catch (e: IOException) {
            _updateStatus.value = false
            e.printStackTrace()
        }
    }

    fun createPdfDirectory(): File? {
        val pdfDirectory = File(
            (application.applicationContext.getExternalFilesDir(null)).toString() +
                    File.separator + Constants.PDF_DIRECTORY
        )
        if (!pdfDirectory.exists()) {
            pdfDirectory.mkdir()
        }

        return pdfDirectory
    }

    fun deleteCartItem(cartEntity: CartEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            CartRepository(application)
                .deleteCartItem(cartEntity)
        }

    }


}