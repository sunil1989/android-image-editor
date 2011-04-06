package android.image.editor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity {
	
	MyView myView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(myView = new MyView(this));
    }

    public static class MyView extends View {

        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Paint   mBitmapPaint;
        private CommandManager commandManager = new CommandManager(2);
        private Tool currentTool = new EraseTool();

        public MyView(Context c) {
            super(c);

            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
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
            currentTool.onDraw(this, canvas);
        }

        public CommandManager getCommandManager() {
			return commandManager;
		}
        
        public void drawBitmap(Canvas canvas) {
        	canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        }

		@Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    currentTool.touchStart(this, x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    currentTool.touchMove(this, x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    currentTool.touchUp(this);
                    break;
            }
            return true;
        }
		
        public Canvas getCanvas() {
			return mCanvas;
		}

		public Tool getCurrentTool() {
			return currentTool;
		}

		public MyViewMemento createMemento() {
    		return new MyViewMemento(mBitmap.copy(Bitmap.Config.ARGB_8888, false));
    	}
    	
    	public void setMemento(MyViewMemento memento) {
    		redrawBitmap(memento.state);
    	}
    	
    	public void redrawBitmap(Bitmap bitmap) {
    		mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    		mCanvas.drawBitmap(bitmap, 0, 0, null);
    	}
    	
    	private void undo() {
    		commandManager.undo();
    	}
    	
    	private void changeTool(Tool tool) {
    		currentTool = tool;
    	}
    	
    	private void crop() {
    		if (!(currentTool instanceof SelectionTool)) {
    			return;
    		}
    		SelectionTool selectionTool = (SelectionTool)currentTool;
    		if (!selectionTool.selectionExists()) {
    			return;
    		}
    		commandManager.executeCommand(new CropCommand(this));
    	}
        
        public Bitmap getBitmap() {
			return mBitmap;
		}

		public void setBitmap(Bitmap mBitmap) {
			this.mBitmap = mBitmap;
		}

		public static class MyViewMemento {
    		
    		private Bitmap state;

    		private MyViewMemento(Bitmap state) {
    			this.state = state;
    		}
    		
    		public void recycle() {
    			state.recycle();
    		}
    		
    	}
    }
    
    private static final int SAVE_MENU_ITEM_ID = 0;
    private static final int UNDO_MENU_ITEM_ID = 1;
    private static final int SELECTION_MENU_ITEM_ID = 2;
    private static final int CROP_MENU_ITEM_ID = 3;
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, SAVE_MENU_ITEM_ID, 0, "save");
		menu.add(0, UNDO_MENU_ITEM_ID, 1, "undo");
		menu.add(0, SELECTION_MENU_ITEM_ID, 2, "selection");
		menu.add(0, CROP_MENU_ITEM_ID, 3, "crop");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem undoMenuItem = menu.getItem(UNDO_MENU_ITEM_ID);
		if (myView.commandManager.hasMoreUndo()) {
			undoMenuItem.setEnabled(true);
		} else {
			undoMenuItem.setEnabled(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == SAVE_MENU_ITEM_ID) {
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
		} else if (item.getItemId() == UNDO_MENU_ITEM_ID) {
			myView.undo();
		} else if (item.getItemId() == SELECTION_MENU_ITEM_ID) {
			myView.changeTool(new SelectionTool());
		} else if (item.getItemId() == CROP_MENU_ITEM_ID) {
			myView.crop();
		}
		return super.onOptionsItemSelected(item);
	}

}
