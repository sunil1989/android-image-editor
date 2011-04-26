package com.android.image.edit.command;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.android.image.edit.BitmapWrapper;
import com.android.image.edit.ImageEditorView;

public class EraseCommand extends AbstractMultiTargetCommand<BitmapWrapper> {

	public EraseCommand(BitmapWrapper target) {
		super(target);
	}

	@Override
	public void execute(BitmapWrapper target, Object... params) {
		if (!(params[0] instanceof Path)) {
			throw new IllegalArgumentException("android.graphics.Path expected but was " + params[0].getClass());
		}
		if (!(params[1] instanceof Float)) {
			throw new IllegalArgumentException("java.lang.Float expected but was " + params[1].getClass());
		}
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(ImageEditorView.BACKGROUND_COLOR);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		//if (!(target instanceof MemoryBitmap)) {
			paint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.CLEAR));
		//}
		paint.setStrokeWidth((Float)params[1]);
		target.drawPath((Path)params[0], paint);
	}

}
