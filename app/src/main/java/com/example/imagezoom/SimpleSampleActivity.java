/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.example.imagezoom;
import com.example.camerademo.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;


public class SimpleSampleActivity extends Activity {


	private ImageView mImageView;
	private PhotoViewAttacher mAttacher;

	private Bitmap mBitmap;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_zoom_image);

		mImageView = (ImageView) findViewById(R.id.iv_photo);
		String path=getIntent().getStringExtra("path");
		System.out.println("图片详情:"+path);
		if(path.startsWith("http")){
		}else{
			mBitmap=	BitmapFactory.decodeFile(path);
			if(mBitmap==null){
				finish();
			}
			//			mBitmap=BitmapUtil.createImageThumbnail(path);
			mImageView.setImageBitmap(mBitmap);
			mAttacher = new PhotoViewAttacher(mImageView,this);
		}
		// The MAGIC happens here!

		//		mAttacher.setScaleType(ScaleType.CENTER_INSIDE);
		// Lets attach some listeners, not required though!
		//		mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
		//		mAttacher.setOnPhotoTapListener(new PhotoTapListener());
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//		mAttacher.setScaleType(ScaleType.CENTER_INSIDE);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();

		// Need to call clean-up
		if(mAttacher!=null){
			mAttacher.cleanup();
		}
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mBitmap != null && !mBitmap.isRecycled()){
			mBitmap.recycle();
			mBitmap = null;
		}
	}


}
