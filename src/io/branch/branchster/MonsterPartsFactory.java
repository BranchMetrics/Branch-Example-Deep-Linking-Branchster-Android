package io.branch.branchster;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class MonsterPartsFactory { 
	private int[] colorArray;
	private Bitmap[] bodyArray;
	private Bitmap[] faceArray;
	private String[] descriptionArray;

	private static MonsterPartsFactory factory_;
	
	private MonsterPartsFactory(Context context) {
		colorArray = new int[]{ Color.argb(1, 36, 164, 221), 
								Color.argb(1, 236, 98, 121),
								Color.argb(1, 41, 180, 113),
								Color.argb(1, 246, 153, 56),
								Color.argb(1, 132, 38, 139),
								Color.argb(1, 36, 202, 218),
								Color.argb(1, 254, 213, 33),
								Color.argb(1, 158, 22, 35) };
		
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
//        options.inSampleSize = 10;
        
		InputStream bis0 = context.getResources().openRawResource(R.drawable.body0);
		InputStream bis1 = context.getResources().openRawResource(R.drawable.body1);
		InputStream bis2 = context.getResources().openRawResource(R.drawable.body2);
		InputStream bis3 = context.getResources().openRawResource(R.drawable.body3);
		InputStream bis4 = context.getResources().openRawResource(R.drawable.body4);
        
		InputStream fis0 = context.getResources().openRawResource(R.drawable.face0);
		InputStream fis1 = context.getResources().openRawResource(R.drawable.face1);
		InputStream fis2 = context.getResources().openRawResource(R.drawable.face2);
		InputStream fis3 = context.getResources().openRawResource(R.drawable.face3);
		InputStream fis4 = context.getResources().openRawResource(R.drawable.face4);
		
        bodyArray = new Bitmap[]{
        	BitmapFactory.decodeStream(bis0, null, options),
        	BitmapFactory.decodeStream(bis1, null, options),
        	BitmapFactory.decodeStream(bis2, null, options),
        	BitmapFactory.decodeStream(bis3, null, options),
        	BitmapFactory.decodeStream(bis4, null, options)
        };
        
        faceArray = new Bitmap[]{
            	BitmapFactory.decodeStream(fis0, null, options),
            	BitmapFactory.decodeStream(fis1, null, options),
            	BitmapFactory.decodeStream(fis2, null, options),
            	BitmapFactory.decodeStream(fis3, null, options),
            	BitmapFactory.decodeStream(fis4, null, options)
            };
        
//		bodyArray = new Bitmap[]{	context.getResources().getDrawable(R.drawable.body0), 
//									context.getResources().getDrawable(R.drawable.body1), 
//									context.getResources().getDrawable(R.drawable.body2), 
//									context.getResources().getDrawable(R.drawable.body3), 
//									context.getResources().getDrawable(R.drawable.body4) };
		
//		faceArray = new Bitmap[]{	context.getResources().getDrawable(R.drawable.face0), 
//									context.getResources().getDrawable(R.drawable.face1), 
//									context.getResources().getDrawable(R.drawable.face2), 
//									context.getResources().getDrawable(R.drawable.face3), 
//									context.getResources().getDrawable(R.drawable.face4) };
		
		descriptionArray = new String[]
		{
	         "%@ is a social butterfly. She’s a loyal friend, ready to give you a piggyback ride at a moments notice or greet you with a face lick and wiggle.",
	         "Creative and contagiously happy, %@ has boundless energy and an appetite for learning about new things. He is vivacious and popular, and is always ready for the next adventure.",
	         "%@ prefers to work alone and is impatient with hierarchies and politics.  Although he’s not particularly social, he has a razor sharp wit (and claws), and is actually very fun to be around.",
	         "Independent and ferocious, %@ experiences life at 100 mph. Not interested in maintaining order, he is a fierce individual who is highly effective, successful, and incredibly powerful.",
	         "Peaceful, shy, and easygoing, %@ takes things at his own pace and lives moment to moment. She is considerate, pleasant, caring, and introspective. She’s a bit nerdy and quiet -- but that’s why everyone loves him."
		};
	}
	
	public static MonsterPartsFactory getInstance(Context context) {
		if (factory_ == null) {
			factory_ = new MonsterPartsFactory(context);
		}
		return factory_;
	}
	
	public int colorForIndex(int index) {
	    return -colorArray[index];
	}

	public String descriptionForIndex(int index) {
	    return descriptionArray[index];
	}

	public Bitmap imageForBody(int index) {
	    return bodyArray[index];
	}

	public int sizeOfBodyArray() {
	    return bodyArray.length;
	}

	public Bitmap imageForFace(int index) {
		return faceArray[index];
	}

	public int sizeOfFaceArray() {
	    return faceArray.length;
	}

}
