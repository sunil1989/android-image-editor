package android.image.editor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {

	Panel panel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		panel = new Panel(this);
		setContentView(panel);

	}

	class Panel extends View {

		//private Bitmap mBitmap;
		//private Canvas mCanvas;
		//private Path mPath;
		private Paint mPaint;
		Bitmap bitmap;
		Canvas pcanvas;
		int x = 0;
		int y = 0;
		int r = 0;

		public Panel(Context context) {
			super(context);
			//setFocusable(true);
			setBackgroundColor(Color.GREEN);
			mPaint = new Paint();
			mPaint.setAlpha(0);
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
			mPaint.setAntiAlias(true);
			Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/dress.jpg");
			// converting image bitmap into mutable bitmap
			bitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.ARGB_8888);
			pcanvas = new Canvas();
			pcanvas.setBitmap(bitmap); // drawXY will result on that Bitmap
			pcanvas.drawBitmap(bm, 0, 0, null);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// draw a circle that is erasing bitmap
			pcanvas.drawCircle(x, y, r, mPaint);
			canvas.drawBitmap(bitmap, 0, 0, null);
			super.onDraw(canvas);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// set paramete to draw circle on touch event
			x = (int) event.getX();
			y = (int) event.getY();
			r = 20;
			// Atlast invalidate canvas
			invalidate();
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("save");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("save")) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/dress1.jpg");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			panel.bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}