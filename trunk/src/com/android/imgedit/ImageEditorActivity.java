package com.android.imgedit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.android.imgedit.R;

import com.android.imgedit.tool.EraseTool;
import com.android.imgedit.tool.SelectionTool;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

public class ImageEditorActivity extends Activity {
	
	private static final int SAVE_MENU_ITEM_ID = 0;
    private static final int UNDO_MENU_ITEM_ID = 1;
    private static final int SELECTION_MENU_ITEM_ID = 2;
    private static final int CROP_MENU_ITEM_ID = 3;
    private static final int ERASE_MENU_ITEM_ID = 4;
    private static final int OPEN_MENU_ITEM_ID = 5;
    
    private static final int SELECT_PICTURE_REQUEST_CODE = 0;
	
	String selectedImagePath;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_editor);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, OPEN_MENU_ITEM_ID, 0, "open");
		menu.add(0, SAVE_MENU_ITEM_ID, 1, "save");
		menu.add(0, UNDO_MENU_ITEM_ID, 2, "undo");
		menu.add(0, SELECTION_MENU_ITEM_ID, 3, "selection");
		menu.add(0, CROP_MENU_ITEM_ID, 4, "crop");
		menu.add(0, ERASE_MENU_ITEM_ID, 5, "erase");
		return super.onCreateOptionsMenu(menu);
	}
    
    private ImageEditorView getImageEditorView() {
    	return (ImageEditorView)findViewById(R.id.image_editor_view);
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem undoMenuItem = menu.getItem(UNDO_MENU_ITEM_ID);
		if (getImageEditorView().getCommandManager().hasMoreUndo()) {
			undoMenuItem.setEnabled(true);
		} else {
			undoMenuItem.setEnabled(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ImageEditorView imageEditorView = getImageEditorView();
		if (item.getItemId() == SAVE_MENU_ITEM_ID) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(selectedImagePath.substring(0, selectedImagePath.length()-4) + "_changed.png");
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
			imageEditorView.changeTool(new EraseTool(imageEditorView));
		} else if (item.getItemId() == OPEN_MENU_ITEM_ID) {
			Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), SELECT_PICTURE_REQUEST_CODE);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
            	String filemanagerstring;
                Uri selectedImageUri = data.getData();

                //OI FILE Manager
                filemanagerstring = selectedImageUri.getPath();

                //MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);

                //DEBUG PURPOSE - you can delete this if you want
                if(selectedImagePath!=null)
                    System.out.println(selectedImagePath);
                else System.out.println("selectedImagePath is null");
                if(filemanagerstring!=null)
                    System.out.println(filemanagerstring);
                else System.out.println("filemanagerstring is null");

                //NOW WE HAVE OUR WANTED STRING
                if(selectedImagePath!=null) {
                    System.out.println("selectedImagePath is the right one for you!");
                    Bitmap bm = BitmapFactory.decodeFile(selectedImagePath);
                    getImageEditorView().setBitmap(bm);
                    bm.recycle();
                } else
                    System.out.println("filemanagerstring is the right one for you!");
            }
        }
    }

    //UPDATED!
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null)
        {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
