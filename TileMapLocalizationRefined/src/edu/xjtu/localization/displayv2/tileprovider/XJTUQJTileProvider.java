package edu.xjtu.localization.displayv2.tileprovider;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;

public class XJTUQJTileProvider extends AssetsFileTileProvider {

	private static String FILE_PATH = "xjtuTiles/";
	private static String FILE_PATH_PATTERN = FILE_PATH+"xjtu_%d_%d_%d.jpg";
	private static String BLANK_PATH = FILE_PATH+"blank.jpg";
	/**
	 * XJTUQU Tile Provider, extending the assetsFileTileProvider, provides the
	 * most specific function for our project.
	 * 
	 * @param context
	 *            require ApplicationContext to access the local data.
	 */
	public XJTUQJTileProvider(Context context) {
		// TODO Auto-generated constructor stub
		super();
		this.setTileSize(256, 256);
		setAssetManager(context.getResources().getAssets());

	}

	@Override
	public Tile getTile(int x, int y, int zoom) {

		int row = y - (int) Math.pow(2, zoom - 1);
		int col = x - (int) Math.pow(2, zoom - 1);
		int level = zoom - 3;
		Log.i("provider",level+" "+row+" "+col);
		String filePath;
		if (row >= 0 && col >= 0 && row < Math.pow(2, level)
				&& col < Math.pow(2, level)) {
			filePath = String.format(FILE_PATH_PATTERN, level, row, col);
			
		} else {
			filePath = BLANK_PATH;
		}
		
		
		setAssetsPathString(filePath);
		return super.getTile(x, y, zoom);

	}
}
