package com.android.image.edit;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.image.edit.tool.EditRectSelectionTool;
import com.android.image.edit.tool.EraseTool;
import com.android.image.edit.tool.ScrollTool;
import com.android.image.edit.transform.ImageTransformStrategy;
import com.android.image.edit.R;


import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ImageEditorActivity extends Activity {
	
	private static final int SAVE_MENU_ITEM_ID = 1;
    private static final int UNDO_MENU_ITEM_ID = 2;
    private static final int SELECTION_MENU_ITEM_ID = 3;
    private static final int CROP_MENU_ITEM_ID = 4;
    private static final int ERASE_MENU_ITEM_ID = 5;
    private static final int OPEN_MENU_ITEM_ID = 6;
    private static final int SCROLL_MENU_ITEM_ID = 7;
    private static final int FIT_TO_SCREEN_ITEM_ID = 8;
    private static final int ORIGINAL_SIZE_ITEM_ID = 9;
    
    private static final int SELECT_PICTURE_REQUEST_CODE = 0;
	
	String currentImagePath;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.image_editor);
        final ImageEditorView imageEditorView = getImageEditorView();
        final View toolsFooter = findViewById(R.id.tools_footer);
        final View cropFooter = findViewById(R.id.crop_footer);
        findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageEditorView.changeTool(new ScrollTool());
			}
		});
        findViewById(R.id.erase).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageEditorView.changeTool(new EraseTool(imageEditorView));
			}
		});
        findViewById(R.id.crop).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageEditorView.changeTool(new EditRectSelectionTool(imageEditorView));
				toolsFooter.setVisibility(View.GONE);
				cropFooter.setVisibility(View.VISIBLE);
			}
		});
        findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageEditorView.undo();
			}
		});
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageEditorView.crop();
				imageEditorView.changeTool(new ScrollTool());
				toolsFooter.setVisibility(View.VISIBLE);
				cropFooter.setVisibility(View.GONE);
			}
		});
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageEditorView.changeTool(new ScrollTool());
				imageEditorView.invalidate();
				toolsFooter.setVisibility(View.VISIBLE);
				cropFooter.setVisibility(View.GONE);
			}
		});
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, OPEN_MENU_ITEM_ID, Menu.NONE, "open");
		menu.add(0, SAVE_MENU_ITEM_ID, Menu.NONE, "save");
		menu.add(0, UNDO_MENU_ITEM_ID, Menu.NONE, "undo");
		menu.add(0, SELECTION_MENU_ITEM_ID, Menu.NONE, "selection");
		menu.add(0, CROP_MENU_ITEM_ID, Menu.NONE, "crop");
		menu.add(0, ERASE_MENU_ITEM_ID, Menu.NONE, "erase");
		menu.add(0, SCROLL_MENU_ITEM_ID, Menu.NONE, "scroll");
		menu.add(0, FIT_TO_SCREEN_ITEM_ID, Menu.NONE, "fit to screen");
		menu.add(0, ORIGINAL_SIZE_ITEM_ID, Menu.NONE, "originalSize");
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
				out = new FileOutputStream(currentImagePath.substring(0, currentImagePath.length()-4) + "_changed.png");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			imageEditorView.getBitmap().compress(Bitmap.CompressFormat.PNG, 90, out);
			return true;
		} else if (item.getItemId() == UNDO_MENU_ITEM_ID) {
			imageEditorView.undo();
		} else if (item.getItemId() == SELECTION_MENU_ITEM_ID) {
			imageEditorView.changeTool(new EditRectSelectionTool(imageEditorView));
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
		} else if (item.getItemId() == SCROLL_MENU_ITEM_ID) {
			imageEditorView.changeTool(new ScrollTool());
		} else if (item.getItemId() == FIT_TO_SCREEN_ITEM_ID) {
			imageEditorView.changeImageTransformStrategy(ImageTransformStrategy.FIT_TO_SCREEN_SIZE);
		} else if (item.getItemId() == ORIGINAL_SIZE_ITEM_ID) {
			imageEditorView.changeImageTransformStrategy(ImageTransformStrategy.ORIGINAL_SIZE);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
            	String filemanagerstring, selectedImagePath;
                Uri selectedImageUri = data.getData();

                //OI FILE Manager
                filemanagerstring = selectedImageUri.getPath();

                //MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);

                //NOW WE HAVE OUR WANTED STRING
                if (selectedImagePath != null) {
                    currentImagePath = selectedImagePath;
                } else if (filemanagerstring != null) {
                    currentImagePath = filemanagerstring;
                } else {
                	return;
                }
                Bitmap bm = BitmapFactory.decodeFile(currentImagePath);
                getImageEditorView().setBitmap(bm);
                bm.recycle();
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
