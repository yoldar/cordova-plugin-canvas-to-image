# Canvas2ImagePlugin

This plugin allows you to save the contents of an HTML canvas tag to the iOS Photo Library, Android Gallery or WindowsPhone 8 Photo Album from your app.
On Android platforms you can change the format of the saved files (png/jpg) and define the folder where to save the pictures .

## Installation

```bash
cordova plugin add cordova-plugin-canvas2image
```

## Usage:

Call the `window.canvas2ImagePlugin.saveImageDataToLibrary()` method using success and error callbacks and the id attribute or the element object of the canvas to save:

### Example
```html
<canvas id="myCanvas" width="165px" height="145px"></canvas>
```

```javascript  ... default format: png   ... default quality 100%
function onDeviceReady()
{
	window.canvas2ImagePlugin.saveImageDataToLibrary(
        function(msg){
            console.log(msg);  //msg is the filename path (for android and iOS)
        },
        function(err){
            console.log(err);
        },
        document.getElementById('myCanvas')
    );
}
```

```javascript  ... format: jpg   ... quality 80%
function onDeviceReady()
{
	window.canvas2ImagePlugin.saveImageDataToLibrary(
        function(msg){
            console.log(msg);  //msg is the filename path (for android and iOS)
        },
        function(err){
            console.log(err);
        },
        document.getElementById('myCanvas'),
        '.jpg',
        80
    );
}

```javascript  ... format: jpg   ... quality 80% ... outputfolder : 'canvaspluginfolder'
function onDeviceReady()
{
	window.canvas2ImagePlugin.saveImageDataToLibrary(
        function(msg){
            console.log(msg);  //msg is the filename path (for android and iOS)
        },
        function(err){
            console.log(err);
        },
        document.getElementById('myCanvas'),
        '.jpg',
        80,
        'cunvaspluginfolder'
    );
}

with this plugin its easy to create a picture, add some text to the picture and save the edited picture to galery or wehre ever you want ! 
