Image-Modul for the Basex-Framework
===================================

This is a XQuery modul for BaseX for resizing images.

For installation get [BaseX (Website)](http://basex.org) or [BaseX (on github)](https://github.com/BaseXdb/basex), go to your BaseX-Repository and

	$ git clone git://github.com/arolle/basex-img-xq.git

The Java sources are included.

Tested with BaseX version 7.1.0-Snapshot, due to bugs only working with BaseX GUI. Not working with basex-web at present.

Import the Modul
----------------

Import the modul in your XQuery-project via

	import module namespace img = "http://basex.org/util/img";

and have fun resizing images.

Methods
-------

The modul offers the following functions (as defined in `basex-img-xq/wrapper.xq`):

* `img:scale($path, $width, $height, [$newPath, [$preserveRatio]] ) as xs:string`  
	scales image located in `$path` and returns the new location.  

	* `$newPath` defaults to `""`  
		accepts any relative or absolute path as well as just the files name.
		e.g. Having given an image located in `"/home/user/Desktop/image.jpg"`
		The following values assigned to `$newPath` are equivalent
			
			"newName"
			"./newName"
			"./newName.jpg"
			"newName.jpg"
			same path as "/home/user/Desktop/newName.jpg"
		  
			"/home/user/Desktop/"
			""
			same path as "/home/user/Desktop/#HASH#.jpg"
		
		where `#HASH#` refers to `md5("/home/user/Desktop/image.jpg")`
	
	* `$preserveRatio`, defaults to true.  
		true: the ratio of the given image and the scaled version stay the same. e.g. maximal height is `$height`, maximal width is `$width`  
		false: scaled versions size is exactly `$width * $height`

* `img:scaleNbase64($path, $width, $height, [$preserveRatio]) as xs:base64Binary`  
	scales (like `img:scale`) and returns the scaled versions base64 represenation
* `img:base64($path) as xs:base64Binary`  
	returns the base64 represenation of the given image