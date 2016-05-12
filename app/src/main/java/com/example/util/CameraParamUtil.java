package com.example.util;

import java.util.List;

import android.hardware.Camera.Size;
import android.util.DisplayMetrics;

/**
 * @author devnn
 */
public class CameraParamUtil {
	/**
	 * @param supportedSize 手机支持的预览分辨率
	 * @return 像素最大的预览分辨率(取像素最大就是为了预览更清晰) Size中width大于height
	 */
	public static Size getProperPreviewSize(List<Size> supportedSize){
		int maxPreviewIndex=0;
		float pixel=0;
		for(Size size:supportedSize){
			if(pixel<size.width*size.height){
				pixel=size.width*size.height;
				maxPreviewIndex=supportedSize.indexOf(size);
			}
		}
		return supportedSize.get(maxPreviewIndex);
	}
	/**
	 * @param supportedSize 手机支持的拍照后生成的图片分辨率
	 * @param rate 目标比例(这个就是预览的分辨率比例,按照这个比例，拍照后图片不会变形)
	 * @param phyMetrics 屏幕分辨率，优先取等于屏幕分辨率的分辨率
	 * @return 等于phyMetrics或接近rate的分辨率
	 */
	public static Size getProperPictureSize(List<Size> supportedSize,float rate,DisplayMetrics phyMetrics){
		float min=3;
		int index=0;
		for(Size size:supportedSize){
			if(size.width==phyMetrics.heightPixels&&size.height==phyMetrics.widthPixels){
				index=supportedSize.indexOf(size);
				break;
			}
			float tw=(float)size.width/size.height;
			if(Math.abs(tw-rate)<min){
				min=Math.abs(tw-rate);
				index=supportedSize.indexOf(size);
			}
		}
		return supportedSize.get(index);
	}
}
