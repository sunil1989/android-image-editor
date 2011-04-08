package com.android.imgedit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import com.android.imgedit.tool.EraseTool;
import com.android.imgedit.tool.SelectionTool;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;

public class ImageEditorActivity extends Activity {
	
	ImageEditorView imageEditorView;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(imageEditorView = new ImageEditorView(this));
    }
    
    private static final int SAVE_MENU_ITEM_ID = 0;
    private static final int UNDO_MENU_ITEM_ID = 1;
    private static final int SELECTION_MENU_ITEM_ID = 2;
    private static final int CROP_MENU_ITEM_ID = 3;
    private static final int ERASE_MENU_ITEM_ID = 4;
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, SAVE_MENU_ITEM_ID, 0, "save");
		menu.add(0, UNDO_MENU_ITEM_ID, 1, "undo");
		menu.add(0, SELECTION_MENU_ITEM_ID, 2, "selection");
		menu.add(0, CROP_MENU_ITEM_ID, 3, "crop");
		menu.add(0, ERASE_MENU_ITEM_ID, 3, "erase");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem undoMenuItem = menu.getItem(UNDO_MENU_ITEM_ID);
		if (imageEditorView.getCommandManager().hasMoreUndo()) {
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
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			imageEditorView.getBitmap().compress(Bitmap.CompressFormat.PNG, 90, out);
			return true;
		} else if (item.getItemId() == UNDO_MENU_ITEM_ID) {
			imageEditorView.undo();
		} else if (item.getItemId() == SELECTION_MENU_ITEM_ID) {
			imageEditorView.changeTool(new SelectionTool());
		} else if (item.getItemId() == CROP_MENU_ITEM_ID) {
			imageEditorView.crop();
		} else if (item.getItemId() == ERASE_MENU_ITEM_ID) {
			imageEditorView.changeTool(new EraseTool());
		}
		return super.onOptionsItemSelected(item);
	}

}
