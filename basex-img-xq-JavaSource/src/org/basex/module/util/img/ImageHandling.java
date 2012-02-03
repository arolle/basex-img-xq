package org.basex.module.util.img;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.basex.query.item.B64;
import org.basex.query.item.Bln;
import org.basex.query.item.Empty;
import org.basex.query.item.Int;
import org.basex.query.item.Str;
import org.basex.query.item.Value;
import org.basex.util.Token;

/**
 * deal with images: load into memory,
 * save from memory and modify affine.
 * 
 */
public class ImageHandling {

	private File myImagePath;
	private String myImageFormat;

	// keep the original image to avoid any loss of quality
	private BufferedImage myImage;
	private BufferedImage myImageChanged;

	/**
	 * Constructor
 	 * tries to get the image data from the given path
	 * 
	 * @param path
	 *            absolute path where the image is located (as string)
	 */
	public ImageHandling(String path) throws IOException {
		myImagePath = new File(path);
		System.err.println("Path: " + path + " " + myImagePath.exists());
		myImage = ImageIO.read(myImagePath);
		// retrieve format from file extension
		myImageFormat = path.substring(path.lastIndexOf(".") + 1);
		// debug
		System.err.println("Format " + myImageFormat);
	}


	/**
	 * saves this.myImage to path/name.ext where ext is the extension
	 * 
	 * @param path
	 *            path
	 * @param name
	 *            name of the image (including filetype-extension)
	 * @return
	 * 			returns either canonicalPath of the saved image or an empty string 
	 */
	public Str saveImage(String path, String name) {
		File imagePath = new File(path + File.separator + name);
		try {
			ImageIO.write(myImageChanged, myImageFormat, imagePath);
			return Str.get(imagePath.exists() ? imagePath.getCanonicalPath() : Str.ZERO);
		} catch (IOException e) {
			System.err.println("IOExeption in saveImage " + e.toString());
			return Str.ZERO;
		}
	}

	/**
	 * saves this.myImage using saveImage(String path, String
	 * name)
	 * 
	 */
	public Str saveImage(String str) {
		int lastSlash = str.lastIndexOf(File.separator);
		String path = str.substring(0, (lastSlash > 0 ? lastSlash : 0));// anything before last slash is dirname, if any
		boolean isAbsolute = path.substring(0, (path.length() > 0 ? 1 : 0)).equals(File.separator);
		if (path.isEmpty() && !isAbsolute) {
			path = myImagePath.getParent();
		} else if(!isAbsolute) {// is no path from root? => set path relative to myImagePath
			path = myImagePath.getParent() + File.separator + path;
		}
		
		String name = str.substring(str.lastIndexOf(File.separator) + 1);// anything after last slash is filename, if any
		int lastDot = name.lastIndexOf(".");
		String format = name.substring(lastDot < 0 ? name.length(): lastDot + 1);// guess the format, if any
		name = name.substring(0, (lastDot < 1 ? name.length() : lastDot));// remove ext

		if (format.isEmpty()) {
			format = myImageFormat;
		}
		if (name.isEmpty()) {
			name = Token.md5(myImagePath.getAbsolutePath());
		}
		return saveImage(path, name + "." + format);
	}

	/**
	 * get base64 representation of the image
	 * 
	 * @return returns either myImage or myImageChanged as base64 data URI
	 * @throws IOException 
	 */
	public B64 convertToBase64() throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		/*
		 * Return original image, if no scaled version is available
		 */
		ImageIO.write((myImageChanged == null ? myImage : myImageChanged),
				myImageFormat, outStream);
		return new B64(outStream.toByteArray());
	}

	/**
	 * scale myImage to `width` pixels times `height` pixels store scaled
	 * version in myImageChanged
	 * 
	 * @param width
	 *            width of the scaled version
	 * @param height
	 *            height of the scaled version
	 * @param preserveRatio
	 *            true if ratio of image dimension should be kept; false if not
	 *            (image size will be exactly width x height)
	 * @return	void in BaseX
	 */
	public Value scaleImage(Int width, Int height, Bln preserveRatio) {
		// calculate scaling factors
		Double scaleFactorWidth = width.dbl(null) / ((double) myImage.getWidth());
		Double scaleFactorHeight = height.dbl(null) / ((double) myImage.getHeight());

		// if preserveRatio: scale equally
		if (preserveRatio.bool(null)) {
			// set scaleFactorHeigt and scaleFactorWidth to the minimum of both
			// then the scaled version really fits into the box width*height.
			scaleFactorWidth = scaleFactorHeight = Math.min(scaleFactorWidth, scaleFactorHeight);
		}

		// DEBUG
/*		System.err.println("Width " + myImage.getWidth()
				+ "\nHeight " + myImage.getHeight()
				+ "\nScaleFactorWidth "	+ scaleFactorWidth
				+ "\nScaleFactorHeight " + scaleFactorHeight
		);
*/

		affineTransform(AffineTransform.getScaleInstance(scaleFactorWidth,
				scaleFactorHeight), (int) (myImage.getWidth() * scaleFactorWidth), (int) (myImage.getHeight() * scaleFactorHeight));
		return Empty.SEQ;
	}

	/**
	 * scale myImage to fit into the box `width` pixels times `height` pixels
	 * store scaled version in myImageChanged
	 * 
	 * @param width
	 * @param height
	 */
	public Value scaleImage(Int width, Int height) {
		return scaleImage(width, height, Bln.TRUE);
	}

	
	/**
	 * Applies a given affine transformation to the image.
	 * To be used for affine manipulations on the image.
	 * 
	 * @param at
	 * 			an affine transformation (to be applied to the image)
	 * @param width
	 * 			width of image after transformation
	 * @param height
	 * 			height of image after transformation
	 **/
	private void affineTransform(AffineTransform at, Integer width, Integer height){
		myImageChanged = new BufferedImage(width, height, myImage.getType());
		Graphics2D imageGraphics = myImageChanged.createGraphics();
		imageGraphics.drawRenderedImage(myImage, at);
		imageGraphics.dispose();
	}
}