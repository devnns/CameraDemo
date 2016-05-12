package com.example.camerademo;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import com.example.util.BitmapUtil;
import com.example.util.CameraParamUtil;
import com.example.util.Config;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
public class CameraActivity extends Activity implements SurfaceHolder.Callback,OnClickListener{
	private String TAG=this.getClass().getSimpleName();
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;

	private Camera mCamera;
	private ImageView ivPreview;
	private TextView tvCancel;
	private TextView tvAlbum;
	private ImageView ivTakePhoto;
	private static final int REQUEST_CODE_ALBUM=100;
	private DisplayMetrics phyMetrics;//屏幕真实分辨率
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initWindow();//设置成全屏
		setContentView(R.layout.activity_camara);
		initViews();
		phyMetrics=getPhyMetrics();
	}
	private void initWindow(){
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}
	private void initViews(){
		this.surfaceView=(SurfaceView)findViewById(R.id.surfaceView);
		surfaceHolder=this.surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		this.surfaceView.setOnClickListener(this);
		ivPreview=(ImageView)this.findViewById(R.id.img_preview);
		ivPreview.setVisibility(View.GONE);
		tvCancel=(TextView)findViewById(R.id.cancel);
		tvCancel.setOnClickListener(this);
		tvAlbum=(TextView)findViewById(R.id.album);
		tvAlbum.setOnClickListener(this);
		ivTakePhoto=(ImageView)findViewById(R.id.take_phone);
		ivTakePhoto.setOnClickListener(this);
	}
	private ShutterCallback mShutterCallback = new ShutterCallback() {

		public void onShutter() {
			// just log ,do nothing
			//	   Log.v("ShutterCallback", "…onShutter…");
		}

	};
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.equals(this.surfaceView)){
			mCamera.autoFocus(mAutoFocusCallBack);
		}else if(arg0.equals(this.ivTakePhoto)){
			if(mCamera==null){
				return;
			}
			mCamera.takePicture(mShutterCallback, null, mPictureCallback);
		}else if(arg0.equals(this.tvCancel)){
			if(tvCancel.getText().equals(this.getResources().getText(R.string.cancel))){
				this.finish();
			}else if(tvCancel.getText().equals(this.getResources().getText(R.string.redo_take_phone))){
				ivPreview.setVisibility(View.GONE);
				if(preBitmap != null && !preBitmap.isRecycled()){
					preBitmap.recycle();
					preBitmap = null;
				}
				changeView();
			}
		}else if(arg0.equals(this.tvAlbum)){
			if(tvAlbum.getText().equals(this.getResources().getText(R.string.album))){
				Intent intent = new Intent();  
				/* 开启Pictures画面Type设定为image */  
				intent.setType("image/*");  
				/* 使用Intent.ACTION_GET_CONTENT这个Action */  
				intent.setAction(Intent.ACTION_PICK);//直接打开相册
				//				intent.setAction(Intent.ACTION_GET_CONTENT);  //打开图片软件选择器
				startActivityForResult(intent, REQUEST_CODE_ALBUM);  
			}else if(tvAlbum.getText().equals(this.getResources().getText(R.string.confirm))){
				if(preBitmap==null||preBitmap.isRecycled()){
					Toast.makeText(this, "请重新拍照", Toast.LENGTH_SHORT).show();
					return;
				}
				saveBitmapAndExit(preBitmap);
			}
		}
	}
	private void changeView(){
		if(this.ivPreview.getVisibility()==View.VISIBLE){
			tvCancel.setText(R.string.redo_take_phone);
			tvAlbum.setText(R.string.confirm);
			ivTakePhoto.setVisibility(View.INVISIBLE);
		}else{
			tvCancel.setText(R.string.cancel);
			tvAlbum.setText(R.string.album);
			ivTakePhoto.setVisibility(View.VISIBLE);
			surfaceView.setVisibility(View.VISIBLE);
		}
	}
	private DisplayMetrics getPhyMetrics()
	{  
		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics(); 
		@SuppressWarnings("rawtypes")
		Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
			Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
			method.invoke(display, dm);
			//            dpi=dm.widthPixels+"*"+dm.heightPixels;
			return dm;
		}catch(Exception e){
			e.printStackTrace();
		}  
		return null;
	}
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		System.out.println("surface created");
		mCamera=Camera.open();
		try {
			mCamera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
			mCamera.release();// release camera  
			mCamera = null;  

		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int format, int width, int height) {
		// TODO Auto-generated method stub
		System.out.println("手机型号:"+android.os.Build.MODEL);
		System.out.println("手机品牌:"+android.os.Build.BRAND);
		Parameters params = mCamera.getParameters();
		params.setPictureFormat(ImageFormat.JPEG);// 设置图片格式
		params.set("rotation", 90); //设置生成的图片顺时针旋转90度，即肖像模式,对有些设备无效,因此在生成图片时可能需要把Bitmap旋转一下(见下面PictureCallback回调的实现)

		Size previewSize=CameraParamUtil.getProperPreviewSize(params.getSupportedPreviewSizes());
		System.out.println("预览分辨率:"+previewSize.width+","+previewSize.height);
		System.out.println("预览比例:"+previewSize.width/(float)previewSize.height);
		params.setPreviewSize(previewSize.width, previewSize.height);

		Size pictureSize=CameraParamUtil.getProperPictureSize(params.getSupportedPictureSizes(), previewSize.width/(float)previewSize.height, phyMetrics);
		System.out.println("图片分辨率:"+pictureSize.width+","+pictureSize.height);
		System.out.println("图片比例:"+(float)pictureSize.width/pictureSize.height);
		params.setPictureSize(pictureSize.width,pictureSize.height);

		mCamera.setDisplayOrientation(90);//设置成竖拍
		mCamera.setParameters(params);
		mCamera.startPreview();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		System.out.println("surface destroyed");
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
	private AutoFocusCallback mAutoFocusCallBack = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
		}
	};
	/**
	 * 拍照的回调接口
	 */
	private Bitmap preBitmap;
	PictureCallback mPictureCallback = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if (data != null) {
				ByteArrayInputStream isBm = new ByteArrayInputStream(data);
				BitmapFactory.Options newOpts = new BitmapFactory.Options();
				//开始读入图片，此时把options.inJustDecodeBounds 设回true了
				newOpts.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(isBm, null, newOpts);
				newOpts.inJustDecodeBounds = false;
				int w =0;
				int h =0;
				System.out.println("测量图片尺寸:"+newOpts.outWidth+","+newOpts.outHeight);
				w = newOpts.outWidth;
				h = newOpts.outHeight;


				float hh = phyMetrics.heightPixels;
				float ww = phyMetrics.widthPixels;
				//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
				int be = 1;//be=1表示不缩放
				if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
					be = (int) (newOpts.outWidth / ww);
				} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
					be = (int) (newOpts.outHeight / hh);
				}
				if (be <= 1){
					be = 1;
				}
				newOpts.inSampleSize = be;//设置压缩比例
				//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
				isBm.reset();
				isBm = new ByteArrayInputStream(data);
				preBitmap =BitmapFactory.decodeStream(isBm, null, newOpts);
				if(w>h){//部分手机设置生成图像角度无效，导致图像逆时针旋转了90时，这里再转回去
					preBitmap=rotaingImageView(90,preBitmap);//顺时针旋转90度
				}
				ivPreview.setImageBitmap(preBitmap);
				ivPreview.setVisibility(View.VISIBLE);
				surfaceView.setVisibility(View.GONE);
				changeView();
			}
		}
	};
	/*
	 * 旋转图片
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public  Bitmap rotaingImageView(int angle , Bitmap bitmap) {
		//旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		bitmap.recycle();
		return resizedBitmap;
	}
	private void saveBitmapAndExit(Bitmap bitmap){
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "未找到SD卡", Toast.LENGTH_SHORT).show();
			return ;
		}
		String fname=this.getIntent().getStringExtra("imageName")+".temp";//原图片名称
		//目标图片路径 
		File picture = new File(Environment.getExternalStorageDirectory()+File.separator+ Config.APP_RESOURCE_ROOT_PATH+File.separator+ Config.APP_IMAGES_PATH+File.separator+fname);
		File file= picture.getParentFile();  
		if(!file.exists()){
			file.mkdirs();
		}
		FileOutputStream stream=null;
		try {
			stream = new FileOutputStream(picture);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bitmap.compress(CompressFormat.JPEG, 100, stream);//将bitmap保存到SD卡,因为下面的压缩只支持path传入
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		Bitmap newBitmap=BitmapUtil.compressSizeAndQuality(picture.getAbsolutePath());//缩小分辨率
		picture.delete();//删除临时文件
		fname=this.getIntent().getStringExtra("imageName")+".jpg";
		picture = new File(Environment.getExternalStorageDirectory()+File.separator+ Config.APP_RESOURCE_ROOT_PATH+File.separator+ Config.APP_IMAGES_PATH+File.separator+fname);
		try {
			stream = new FileOutputStream(picture);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap lastBitmap=null;
		if(newBitmap.getHeight()>=newBitmap.getWidth()){
			int start=(newBitmap.getHeight()-newBitmap.getWidth())/2;
			lastBitmap=Bitmap.createBitmap(newBitmap, 0, start, newBitmap.getWidth(),  newBitmap.getWidth());
		}else{
			int start=(newBitmap.getWidth()-newBitmap.getHeight())/2;
			lastBitmap=Bitmap.createBitmap(newBitmap, start, 0, newBitmap.getHeight(),  newBitmap.getHeight());
		}
		if(newBitmap != null && !newBitmap.isRecycled()){
			newBitmap.recycle();
			newBitmap = null;
		}
		lastBitmap.compress(CompressFormat.JPEG, 80, stream);//生成最终图片
		if(lastBitmap != null && !lastBitmap.isRecycled()){
			lastBitmap.recycle();
			lastBitmap = null;
		}
		try {
			stream.flush();
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent=new Intent();
		intent.putExtra("path", picture.getAbsolutePath());
		this.setResult(Activity.RESULT_OK, intent);
		finish();
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK&&requestCode==REQUEST_CODE_ALBUM){
			//选择图片
			Uri uri = data.getData(); 
			ContentResolver cr = this.getContentResolver(); 
			try {
				Bitmap bmp = BitmapFactory.decodeStream(cr.openInputStream(uri));
				saveBitmapAndExit(bmp);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		if(preBitmap != null && !preBitmap.isRecycled()){
			preBitmap.recycle();
			preBitmap = null;
			Log.i(TAG, "preBitmap has been recycled");
		}
	}



}
