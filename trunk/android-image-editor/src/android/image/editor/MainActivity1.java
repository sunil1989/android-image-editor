package android.image.editor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity1 extends Activity {
	
	MyView myView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(myView = new MyView(this));
    }

    public static class MyView extends View {

        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        private Paint       mPaint;
        private Command lastCommand;

        public MyView(Context c) {
            super(c);

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(0xFFFF0000);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(12);
            mPaint.setXfermode(new PorterDuffXfermode(
                    PorterDuff.Mode.CLEAR));
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/dress.jpg");
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mCanvas.drawBitmap(bm, 0, 0, null);
            bm.recycle();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFAAAAAA);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            canvas.drawPath(mPath, mPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            lastCommand = new DrawPathCommand(this);
            lastCommand.execute();
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
        
        public void drawPath() {
        	mCanvas.drawPath(mPath, mPaint);
        }
        
        public MyViewMemento createMemento() {
    		return new MyViewMemento(mBitmap.copy(Bitmap.Config.ARGB_8888, false));
    	}
    	
    	public void setMemento(MyViewMemento memento) {
    		mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    		mCanvas.drawBitmap(memento.state, 0, 0, null);
    	}
    	
    	private void undo() {
    		lastCommand.unexecute();
    	}
        
        public static class MyViewMemento {
    		
    		private Bitmap state;

    		private MyViewMemento(Bitmap state) {
    			this.state = state;
    		}
    		
    	}
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("save");
		menu.add("undo");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("save")) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/dress1.png");
		        /*Bitmap picture = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/dress.jpg").
		        	copy(Bitmap.Config.ARGB_8888, true);
		        Canvas c = new Canvas(picture);
		        
		        Bitmap logo = myView.mBitmap;
		        c.drawBitmap(logo, 0, 0, null);
		        setContentView(R.layout.main);
		        ((ImageView)findViewById(R.id.dressImageView)).setImageBitmap(picture);*/
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			myView.mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			return true;
		} else if (item.getTitle().equals("undo")) {
			myView.undo();
		}
		return super.onOptionsItemSelected(item);
	}

}
