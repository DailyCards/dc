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

class DayilyCardsScratchView extends SurfaceView implements SurfaceHolder.Callback {
	//tova e za debugvane
	 private static final String TAG = "DayilyCardsScratchView";
	 
	 private Context context;
	 private Bitmap bitmap;
	 private DayilyCardsScratchViewThread workingThread;
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
	 
	 
	 //private int width;
	 //private int height;
	 //private int sizeOfQuadsX;
	// private int sizeOfQuadsY;
	 //private int quadsMustBeScracthed;
	 
	 //private HashMap<String, Boolean> coords = new HashMap<String, Boolean>();
	 
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
		 /*
		 width = getWidth();
		 height = getHeight();
		 sizeOfQuadsX  = (int)(width/(scratchWidth + .5));
		 sizeOfQuadsY  = (int)(height/(scratchWidth + .5));
		 quadsMustBeScracthed = (int)(sizeOfQuadsX*sizeOfQuadsY - 2*sizeOfQuadsX -
				  2*sizeOfQuadsY- 4*scratchWidth);	 
				 
		 Log.d("Width and height", width + " " + height);
		 Log.d("sizeOfQuadsX", sizeOfQuadsX + " ");
		 Log.d("sizeOfQuadsY", sizeOfQuadsY + " ");
		 Log.d("quadsMustBeScracthed", quadsMustBeScracthed + " ");
		 */
	 }
	 /*
	 public void drawC(Canvas canvas, DayilyCardsScratchView view) {
		 bitmap = Bitmap.createBitmap(canvas.getWidth(), 
					canvas.getHeight(), Bitmap.Config.RGB_565);
		 Canvas newCanvas = new Canvas(bitmap);
		 view.draw(newCanvas);
		 canvas.drawBitmap(bitmap, 0, 0, null);
		// for (Path path: pathList) {
		//	 canvas.drawPath(path, overlayPaint);
		// }
	 }
	 */
	 @Override
	 public void draw(Canvas canvas) {
		 //buildDrawingCache(true);
		 //Log.d("bitmap size", bitmap.getWidth() + " ");
		 bitmap = Bitmap.createBitmap(canvas.getWidth(), 
					canvas.getHeight(), Bitmap.Config.RGB_565);
		 Canvas newCanvas = new Canvas(bitmap);
		 canvas.drawColor(overlayColor);
		 //buildDrawingCache(true);
		 //bitmap = getBitmap();
		 //if (bitmap != null) {
		//	 Log.d("bitmap != null", "bitmap != null");
		 //}
		// Log.d("onDrawwwwwwwwwwww", "DRAWWWWWWWWWWWWWWW");
		 //izchertavame putistata
		 for (Path path: pathList) {
			 canvas.drawPath(path, overlayPaint);
			 newCanvas.drawPath(path, overlayPaint2);
		 }
		
		// setDrawingCacheEnabled(false);
		//destroyDrawingCache();
	 }
	 
	 @Override
	 public boolean onTouchEvent (MotionEvent event) {
		 // moje da ne e svrurhsilo predichnoto scratchvave, sinchronizirame
		 synchronized (workingThread.getSurfaceHolder()) {
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
			 				//theXYQuad(event.getX(), event.getY());
			 			//}
			 			//theXYQuad(event.getX(), event.getY());
			 			//Log.d("movvvvvveeeeee", event.getX() + " "+ event.getY());
			 		}
			 	break;
			 	case MotionEvent.ACTION_UP:
			 		scratching = false;
			 		 //bitmap = getBitmap();
					 //if (bitmap != null) {
						 if (finishScratch()) {
							 //Log.d("Finish Scrapch", "Finish Scratch");
							 workingThread.setRunning(false);
						 }
					 //}
			 		/*
			 		 if (finishScratch()) {
						 Log.d("Finish Scrapch", "Finish Scratch");
						 workingThread.setRunning(false);
					 } else {
						 Log.d("Not finished", "Not finished");
					 }
					 */
			 		//getBitmap().setPixel((int)event.getX(), (int)event.getY(), Color.BLACK);
			 	break;
			 }
			 return true;
		 }
	 }
	 
	 
	 @Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		workingThread = new DayilyCardsScratchViewThread(getHolder(), this); 
		workingThread.setRunning(true);
		workingThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		workingThread.setRunning(false);
        while (retry) {
            try {
            	workingThread.join();
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
	 /*
	 private void theXYQuad (float x, float y) {
		 Log.d("calling theXYQuad", "calling theXYQuad");
		 //if (x > scratchWidth && x < width - scratchWidth &&
			//	 y > scratchWidth && y < height - scratchWidth) {
			
			 final int XQuad = (int)x/scratchWidth;
			 final int YQuad = (int)y/scratchWidth;
			 Log.d("number of quad", XQuad + " " + YQuad);
			 if (!coords.containsKey(XQuad+"_"+YQuad)) {
				 coords.put(XQuad+"_"+YQuad, new Boolean(true));
				 Log.d("putting", "putting");
			 } 
			 
		// }
	 }
	 */
	 
	 private boolean finishScratch () {
		 
		 bitmap = getBitmap();
		 Log.d("on", "DRAWWWWWWWWWWWWWWW");
		 if (bitmap != null) {
			 final int width = bitmap.getWidth();
		     final int height = bitmap.getHeight();
		     final int scale = (int)(scratchWidth/2 + .5);
		     
		     final int xStep = width/scale;
		     final int yStep = height/scale;
		     
		     final int xInit = xStep/2;
		     final int yInit = yStep/2;
		     
		     final int xEnd = width - xStep/2;
		     final int yEnd = height - yStep/2;
		     
		     int totalTransparent = 0;
		     
		     for(int x = xInit; x <= xEnd; x += xStep) {
		            for(int y = yInit; y <= yEnd; y += yStep) {
		                if (bitmap.getPixel(x, y) == Color.WHITE) {
		            	Log.d("Color", Integer.toString(bitmap.getPixel(x, y)));
		                    totalTransparent++;
		                }
		            }
		        }
		     	
		     	Log.i("number transperant", Integer.toString(totalTransparent));
		     	Log.i("all quads", Integer.toString(scale*scale));
		     	//return false;
		        return ((float)totalTransparent)/(scale * scale) > 0.8;
		       //Log.d("transperant",Integer.toString(totalTransparent));
		 }
		 return false;
		 /*
		 Log.d("HashpMap size", Integer.toString(coords.size()));
		 return ((float)coords.size()/(float)quadsMustBeScracthed) > .85;
		 */
	 }
	 
	 private Bitmap getBitmap() {
		// return this.getDrawingCache();
		 return bitmap;
		 
	 }
	 
	 private void setBitmap(Bitmap bitmap) {
		 this.bitmap = bitmap;
	 }
	 
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
							 /*
							 if (bitmap == null)
								bitmap = Bitmap.createBitmap(canvas.getWidth(), 
										canvas.getHeight(), Bitmap.Config.ARGB_8888);
							 canvas.setBitmap(bitmap);
							 */
							// drawC(canvas, view);
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
	 
	 
	private int dipToPixels(Context context, float dipValue) {
		    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}
	 
}
