module namespace img="http://basex.org/util/img";
declare namespace baseximg = "java:org.basex.module.util.img.ImageHandling";

declare function img:scale($path as xs:string, $width as xs:integer, $height as xs:integer) as xs:string {
	img:scale($path, $width, $height, '')
};
declare function img:scale($path as xs:string, $width as xs:integer, $height as xs:integer, $newPath as xs:string)  as xs:string {
	img:scale($path, $width, $height, $newPath, fn:true())
};
declare function img:scale($path as xs:string, $width as xs:integer, $height as xs:integer, $newPath as xs:string, $preserveRatio as xs:boolean) as xs:string {
	try {
		let $imageHandling := baseximg:new($path)
		return (baseximg:scaleImage($imageHandling, $width, $height, $preserveRatio), baseximg:saveImage($imageHandling, $newPath))[1]
	} catch * {
		error(QName('http://basex.org/util/img','IMG1'), 'File does not exist')
	}
};


declare function img:scaleNbase64($path as xs:string, $width as xs:integer, $height as xs:integer) as xs:base64Binary {
	img:scaleNbase64($path, $width, $height, fn:true())
};

declare function img:scaleNbase64($path as xs:string, $width as xs:integer, $height as xs:integer, $preserveRatio as xs:boolean) as xs:base64Binary {
	let $imageHandling := baseximg:new($path)
	let $unUsed := baseximg:scale($imageHandling, $width, $height)
	return (baseximg:convertToBase64($imageHandling))
};

declare function img:base64($path as xs:string) as xs:base64Binary {
	let $imageHandling := baseximg:new($path)
	return baseximg:convertToBase64($imageHandling)
};