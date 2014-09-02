/**
 * 
 */
package edu.xjtu.localization.displayv2.tileprovider;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.util.Log;

/**
 * Provide an easier way to use files in assets dir as tiles, extending our InputStreamTileProvider.
 * @author jiangzp
 *
 */
public class AssetsFileTileProvider extends InputStreamTileProvider {

	private String pathString;
	private AssetManager assetManager;
	
	/**
	 * implements the underline getSourceInputStream. You don't need to call this function.
	 */
	@Override
	public InputStream getSourceInputStream() {
		try {
			return assetManager.open(pathString);
		} catch (IOException e) {
			Log.e("tiles","pathString not found");
			return null;
		}
	}
	
	/**
	 * Set the filepath, getSourceInputStream method will access the file with this name and turn it into an inputstraem.
	 * @param filepathString
	 */
	public void setAssetsPathString(String filepathString){
		this.pathString = filepathString;
	}
	
	/**
	 * Requing AssetManager to access the assets DIR.
	 * @param assetManager
	 */
	public void setAssetManager(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

}
