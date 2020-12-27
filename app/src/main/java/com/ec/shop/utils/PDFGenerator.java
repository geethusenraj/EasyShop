package com.ec.shop.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.collection.LruCache;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileOutputStream;
import java.io.IOException;

public class PDFGenerator {
    RecyclerView view;

    public PDFGenerator(RecyclerView view) {
        this.view = view;
    }


    @SuppressLint("ResourceAsColor")
    public Bitmap getScreenshotFromRecyclerView(RecyclerView view,
                                                LinearLayout view2,
                                                Bitmap amountBitmap,
                                                FragmentActivity context) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap billBitmap = null;
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }

                height += holder.itemView.getMeasuredHeight();
            }

            billBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas billCanvas = new Canvas(billBitmap);
            billCanvas.drawColor(Color.WHITE);

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                billCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
            Canvas amountCanvas = new Canvas(amountBitmap);
            amountCanvas.drawBitmap(amountBitmap, 0, view2.getHeight(), null);

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                    height + view2.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bigBitmap);
            canvas.drawBitmap(billBitmap, 0, view2.getHeight(), null);
            canvas.drawBitmap(amountBitmap, 0, 0, null);
            billBitmap.recycle();
            amountBitmap.recycle();

        }

        return bigBitmap;
    }

    public void createPdfFile(Bitmap rvBitmap,
                              FragmentActivity context) {
        try {
            String pdfFile = "/sdcard/pdffromlayout.pdf";
            FileOutputStream fOut = new FileOutputStream(String.valueOf(pdfFile));

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(rvBitmap.getWidth(), rvBitmap.getHeight(),
                    1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            rvBitmap.prepareToDraw();
            Canvas c;
            c = page.getCanvas();
            c.drawBitmap(rvBitmap, 0, 0, null);
            document.finishPage(page);
            document.writeTo(fOut);
            document.close();
            Toast.makeText(context, "PDF generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}