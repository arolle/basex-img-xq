package org.basex.module.util.img;

import java.io.IOException;

import org.basex.query.item.Bln;
import org.basex.query.item.Int;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ImageHandling ih = new ImageHandling("/home/user/Desktop/image.jpg");
		ih.scaleImage(Int.get(200), Int.get(200), Bln.get(true));
		
		System.out.println(ih.saveImage("/Users/mag/Desktop/"));
		System.out.println(ih.saveImage("newName"));// goes to same Folder
		System.out.println(ih.saveImage("./newNames")); // same folder - relative
		
		ih.scaleImage(Int.get(200), Int.get(200), Bln.get(false));
		System.out.println(ih.saveImage("./newerName.jpg"));
		
		ih.scaleImage(Int.get(200), Int.get(200), Bln.get(true));
		System.out.println(ih.saveImage("newName.jpg"));
		System.out.println(ih.saveImage(""));// hash of path
		System.out.println(ih.saveImage(".jpg"));//test
		
		
		System.out.println(ih.convertToBase64());
	}
}