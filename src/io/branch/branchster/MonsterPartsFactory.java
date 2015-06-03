package io.branch.branchster;

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

        colorArray = context.getResources().getIntArray(R.array.colors);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;

        bodyArray = new Bitmap[]{
                BitmapFactory.decodeResource(context.getResources(), R.drawable.body0),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.body1),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.body2),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.body3),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.body4)
        };
        
        faceArray = new Bitmap[]{
                BitmapFactory.decodeResource(context.getResources(), R.drawable.face0),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.face1),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.face2),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.face3),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.face4),
            };

        descriptionArray = context.getResources().getStringArray(R.array.description_array);

	}
	
	public static MonsterPartsFactory getInstance(Context context) {
		if (factory_ == null) {
			factory_ = new MonsterPartsFactory(context);
		}
		return factory_;
	}
	
	public int colorForIndex(int index) {
        return colorArray[index];
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
