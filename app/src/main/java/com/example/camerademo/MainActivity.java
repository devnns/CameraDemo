package com.example.camerademo;
import com.example.imagezoom.SimpleSampleActivity;
import com.example.util.BitmapUtil;
import com.example.util.DensityUtil;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	private LinearLayout images_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		images_layout=(LinearLayout) this.findViewById(R.id.images_layout);
	}
	public void camera(View veiw){
		Intent intent=new Intent();
		intent.setClass(this, CameraActivity.class);
		String imageName="picture"+String.valueOf((images_layout.getChildCount()+1));
		intent.putExtra("imageName",imageName);
		this.startActivityForResult(intent, 100);
	}
	private ImageView imageView; 
	private void addImage(String path){
		System.out.println("添加图片到布局:"+path);
		try {
			//			Bitmap bitmap=	BitmapFactory.decodeFile(pathName);
			Bitmap bitmap=	BitmapUtil.createImageThumbnail(path);
			//			Bitmap bitmap=	BitmapFactory.decodeFile(path, options);
			imageView=new ImageView(this);
			imageView.setTag(path);
			LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(DensityUtil.dip2px(this,80f), DensityUtil.dip2px(this,80f));
			p.leftMargin=DensityUtil.dip2px(this,10f);
			imageView.setLayoutParams(p);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setImageBitmap(bitmap);
			imageView.setOnClickListener(new ImageOnClickListener());
			images_layout.addView(imageView);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private class ImageOnClickListener implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SimpleSampleActivity.class);
			intent.putExtra("path",(String)v.getTag());
			startActivity(intent);
		}

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==100&&resultCode==Activity.RESULT_OK){
			addImage(data.getStringExtra("path"));
		}
	}
	

}
