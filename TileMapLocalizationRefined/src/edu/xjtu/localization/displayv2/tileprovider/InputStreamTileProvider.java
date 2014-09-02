package edu.xjtu.localization.displayv2.tileprovider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

/**
 * Transform generic inputstream into tileprovider
 * 
 * @author jiangzp
 * 
 */
public abstract class InputStreamTileProvider implements TileProvider {

	private int width, height;

	public void setTileSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Method getBytesFromImageInputStream will call this method to get the
	 * inputstream. All you need to do is tranform any data source (URL, file,
	 * or etc.) into an inputstream.
	 * 
	 * @return
	 */
	public abstract InputStream getSourceInputStream();

	/**
	 * Transform Any inputstream to tiles.
	 * 
	 * @return
	 */
	private byte[] getBytesFromImageInputStream() {

		InputStream fis = getSourceInputStream();
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			org.apache.commons.io.IOUtils.copy(fis, baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return baos == null ? null : baos.toByteArray();

	}

	@Override
	public Tile getTile(int x, int y, int zoom) {

		byte[] imageByte = getBytesFromImageInputStream();
		if (imageByte == null) {
			return NO_TILE;
		}
		return new Tile(width, height, imageByte);
	}

}
