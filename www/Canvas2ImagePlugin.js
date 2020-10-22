module.exports = {

  saveImageDataToLibrary: function (successCallback, failureCallback, canvasId, fileName, quality, picfolder, add2galery) {
    // successCallback required
    if (typeof successCallback != "function") {
      console.log("Canvas2ImagePlugin Error: successCallback is not a function");
    }
    else if (typeof failureCallback != "function") {
      console.log("Canvas2ImagePlugin Error: failureCallback is not a function");
    }
    else {
      var canvas = (typeof canvasId === "string") ? document.getElementById(canvasId) : canvasId;
      var imageData = canvas.toDataURL().replace(/data:image\/png;base64,/, '');
      var destQuality = 100;
      try {
        if (quality) destQuality = parseFloat(quality);
      } catch (e) { }
      var params = [imageData, fileName, destQuality, picfolder, add2galery];
      return cordova.exec(successCallback, failureCallback, "Canvas2ImagePlugin", "saveImageDataToLibrary", params);
    }
  }
};

