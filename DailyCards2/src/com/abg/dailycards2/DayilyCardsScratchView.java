package com.abg.dailycards2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

class DayilyCardsScratchView extends SurfaceView implements SurfaceHolder.Callback {
	//tova e za debugvane
	 private static final String TAG = "DayilyCardsScratchView";
	 
	 private Context context;
	 private Bitmap bitmap;
	 private DayilyCardsScratchViewThread workingThreadDraw;
	 //list sus vsichki "scratchnati" pathove
	 List<Path> pathList = new ArrayList<Path>();
	 //cveta na zonata, koito ste se scratchva
	 private int overlayColor = 0xff444444;
	 //definicia na Paint-a za putia, koito se scratchva
	 private Paint overlayPaint;
	 
	 private Paint overlayPaint2;
	 
	 // shirinata na iztritata chertichka
	 private int scratchWidth = 50;
	 private Path path;
	 private float startX = 0f;
	 private float startY = 0f;
	 
	 private int importantWidth = 0;
	 private int importantHeight = 0;
	 
	 //ukazane dali pochva novo scratchvane za da se suzdade v List nov Path
	 private boolean scratching = false;
	 
	 public DayilyCardsScratchView (Context context) {
		 super(context);
		 init(context);
	 }
	 
	 public DayilyCardsScratchView(Context context, AttributeSet attr) {
		 super(context, attr);
		 init(context);
	 }
	 
	 private void init(Context context) {
		 this.context = context;
		 
		// this.setDrawingCacheEnabled(true);
		 
		 //postaviame dve viewto edno vurhu drugo, tova koeto ste se scratchva tribva da e otgore
		 setZOrderOnTop(true);
		 SurfaceHolder holder = getHolder();
		 holder.addCallback(this);
		 // tova e vajno za da moje da se vijda dolnoto view
		 holder.setFormat(PixelFormat.RGBA_8888);
		 
		 //definirame Paint obekta, koito ste se izpolzva za scratchvane
		 
		 overlayPaint = new Paint();
		 overlayPaint.setAntiAlias(true);
		 overlayPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		 overlayPaint.setStyle(Paint.Style.STROKE);
		 overlayPaint.setStrokeWidth(scratchWidth);
		 overlayPaint.setStrokeCap(Paint.Cap.ROUND);
		 overlayPaint.setStrokeJoin(Paint.Join.ROUND);
		 
		 
		 overlayPaint2 = new Paint();
		 overlayPaint2.setAntiAlias(true);
		 overlayPaint2.setColor(Color.WHITE);
		 overlayPaint2.setStyle(Paint.Style.STROKE);
		 overlayPaint2.setStrokeWidth(scratchWidth);
		 overlayPaint2.setStrokeCap(Paint.Cap.ROUND);
		 overlayPaint2.setStrokeJoin(Paint.Join.ROUND);
		
	 }
	
	 @Override
	 public void draw(Canvas canvas) {
		
		 bitmap = Bitmap.createBitmap(canvas.getWidth(), 
					canvas.getHeight(), Bitmap.Config.RGB_565);
		 Canvas newCanvas = new Canvas(bitmap);
		 canvas.drawColor(overlayColor);
		
		 for (Path path: pathList) {
			 canvas.drawPath(path, overlayPaint);
			 newCanvas.drawPath(path, overlayPaint2);
		 }
		
	 }
	 
	 @Override
	 public boolean onTouchEvent (MotionEvent event) {
		 // moje da ne e svrurhsilo predichnoto scratchvave, sinchronizirame
		 synchronized (workingThreadDraw.getSurfaceHolder()) {
			 switch (event.getAction()) {
			 	case MotionEvent.ACTION_DOWN:
			 		path = new Path();
			 		startX = event.getX();
			 		startY = event.getY();	
			 		path.moveTo(startX, startY);
			 		pathList.add(path);
			 	break;
			 	case MotionEvent.ACTION_MOVE:
			 		if (scratching) {
			 			path.lineTo(event.getX(), event.getY());
			 		} else {
			 			// moje kato zapochva da se trie, putia da ne e dostatuchen
			 			// za da iztrivame
			 			//if (toStartScratch(startX, event.getX(), startY, event.getY())) {
			 				scratching = true;
			 				path.lineTo(event.getX(), event.getY());
			 				
			 			//}
			 			
			 		}
			 	break;
			 	case MotionEvent.ACTION_UP:
			 		scratching = false;
			 	
					 if (finishScratch()) {
						 //Log.d("Finish Scrapch", "Finish Scratch");
						 workingThreadDraw.setRunning(false);
					 }
					
			 	break;
			 }
			 return true;
		 }
	 }
	 
	 
	 @Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		workingThreadDraw = new DayilyCardsScratchViewThread(getHolder(), this); 
		workingThreadDraw.setRunning(true);
		workingThreadDraw.start();
		
		View parent = (View)this.getParent();
		if (parent != null) {
			TextView textView = (TextView)parent.findViewById(R.id.scratch_text);
			importantHeight = textView.getHeight();
			importantWidth = textView.getWidth();
			Log.i("TExtView width", importantWidth + " ");
		} else {
			Log.i("No parent", "No parent");
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		workingThreadDraw.setRunning(false);
        while (retry) {
            try {
            	workingThreadDraw.join();
                retry = false;
            } catch (InterruptedException e) {
            	//one retry to close the thread
            }
        
        }
	}

	 
	 // kogato pochva novo triene izmervame kolko razstonia e izminato s prust
	 // i ako e po malko shirinata na liniata za iztrivane ne
	 // trieme, moje i bez tova TODO
	 private boolean toStartScratch(float oldX, float newX, float oldY, float newY) {
		 float distance = (float)Math.sqrt(Math.pow(Math.abs(newX - newY), 2) + 
				 							Math.pow(Math.abs(newY - oldY), 2));
		 if (distance > scratchWidth)
			 return true;
		 else
			 return false;
	 }
	 
	 
	 private boolean finishScratch () {
		 
		 if (bitmap != null) {
			 
			 int width;
			 int height;
			 
			 int bitmapW = bitmap.getWidth();
			 int bitmapH = bitmap.getHeight();
			 
			 Log.i("ImporatnWidth and height", importantWidth + " " + importantHeight);
			 
			 if (importantWidth == 0 || importantHeight == 0) {
				 width = bitmapW;
				 height = bitmapH;
			 } else {
				 width = importantWidth;
				 height = importantHeight;
			 }
			 
		     int scale = (int)scratchWidth/5;
		     if (importantWidth != 0 && importantHeight != 0) { 
		    	 scale = (int)scratchWidth/7;
		     }
		     
		     int xStep = width/scale;
		     int yStep = height/scale;
		     
		     Log.i("xStep and YStep", xStep + " " + yStep);
		     
		     int xInit = scale/2;
		     int yInit = scale/2;
		     if (importantWidth != 0 && importantHeight != 0) {
		    	 xInit += bitmapW/2 - width/2;
			     yInit += bitmapH/2 - height/2;
		     }
		     
		     Log.i("xInit and yInit", xInit + " " + yInit);
		     
		     int xEnd = width - scale/2;
		     int yEnd = height - scale/2;
		     if (importantWidth != 0 && importantHeight != 0) {
		    	 xEnd = bitmapW/2 + width/2 - scale/2;
		    	 yEnd = bitmapH/2 + height/2 - scale/2;
		     }
		     
		     Log.i("xEnd and yEnd", xEnd + " " + yEnd);
		     
		     int totalTransparent = 0;
		     
		     for(int x = xInit; x <= xEnd; x += scale) {
		            for(int y = yInit; y <= yEnd; y += scale) {
		                if (bitmap.getPixel(x, y) == Color.WHITE) {
		                    totalTransparent++;
		                }
		            }
		        }
		     	
		     	Log.i("number transperant", Integer.toString(totalTransparent));
		     	Log.i("all quads", Integer.toString(xStep * yStep- 2*xStep - 2*yStep + 4));
		     	
		     	if (importantWidth == 0 || importantHeight == 0) {
		     		Log.i("Procent", Float.toString(((float)totalTransparent)/(xStep * yStep - 2*xStep - 2*yStep + 4 )));
		     		return ((float)totalTransparent)/(xStep * yStep - 2*xStep - 2*yStep + 4) > 0.9f;
		     	}else{
		     		Log.i("Procent", Float.toString(((float)totalTransparent)/(xStep * yStep)));
		     		return ((float)totalTransparent)/(xStep * yStep) > 0.9f;
		     	}
		 }
		 return false;
	
	 }
	 /*
	 private Bitmap getBitmap() {
		 return bitmap;
		 
	 }
	 
	 private void setBitmap(Bitmap bitmap) {
		 this.bitmap = bitmap;
	 }
	 
	 private int dipToPixels(Context context, float dipValue) {
		    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}
	 */
	 
	 
	 //nishkata, koitato ste se izvika kogato Surface is created i ste vika onDraw methoda
	 class DayilyCardsScratchViewThread extends Thread {
		 private SurfaceHolder surfaceHolder;
		 private DayilyCardsScratchView view;
		 private boolean running = false;
		 
		 public DayilyCardsScratchViewThread (SurfaceHolder surfaceHolder, DayilyCardsScratchView view) {
			 this.surfaceHolder = surfaceHolder;
			 this.view = view;
		 }
		 
		 public void setRunning (boolean running) {
			 this.running = running;
		 }
		 
		 public SurfaceHolder getSurfaceHolder () {
			 return surfaceHolder;
		 }
		 
		 @Override
		 public void run() {
			 Canvas canvas = null;
			 while (running) {
				 try {
					 //moje da izpolzvam drugia method, not tozi pozvoliava da se specificira zona
					 // koito da se zapazi
					 canvas = surfaceHolder.lockCanvas(null);
					 synchronized(surfaceHolder) {
						 if (canvas != null) {
							 view.draw(canvas);
						 }
						 
					 }
				 } finally {
					 if (canvas != null) {
						 surfaceHolder.unlockCanvasAndPost(canvas);
					 }
				 }
			 }
		 }
	 }
	 
	 
	
}
