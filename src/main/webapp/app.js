var albumBucketName = "neighborlink.dev.demo";
var bucketRegion = "us-east-1";

AWS.config.update({
  region: bucketRegion
});


function uuidv4() {
  return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
      (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
  );
}

var getS3Client = function (data) {
  AWS.config.update({
    credentials: new AWS.Credentials({
      accessKeyId: data.accessKeyId,
      secretAccessKey: data.secretAccessKey,
      sessionToken: data.sessionToken
    })
  });
  var s3 = new AWS.S3({
    apiVersion: "2006-03-01",
    params: {
      Bucket: albumBucketName
    }
  });
  return s3;
};

function listAlbums() {
  $.post('token', function(data) {
    var s3 = getS3Client(data);
    s3.listObjects({ Delimiter: "/" }, function(err, data) {
      if (err) {
        return alert("There was an error listing your albums: " + err.message);
      } else {
        var albums = data.CommonPrefixes
            .filter(function(commonPrefix) {
              return commonPrefix.Prefix != 'uploads/'
            } )
            .map(function(commonPrefix) {
          var prefix = commonPrefix.Prefix;
          var albumName = decodeURIComponent(prefix.replace("/", ""));
          return getHtml([
            "<li>",
            "<span onclick=\"deleteAlbum('" + albumName + "')\">X</span>",
            "<span onclick=\"viewAlbum('" + albumName + "')\">",
            albumName,
            "</span>",
            "</li>"
          ]);
        });
        var message = albums.length
            ? getHtml([
              "<p>Click on an album name to view it.</p>",
              "<p>Click on the X to delete the album.</p>"
            ])
            : "<p>You do not have any albums. Please Create album.";
        var htmlTemplate = [
          "<h2>Albums</h2>",
          message,
          "<ul>",
          getHtml(albums),
          "</ul>",
          "<button onclick=\"createAlbum(prompt('Enter Album Name:'))\">",
          "Create New Album",
          "</button>"
        ];
        document.getElementById("app").innerHTML = getHtml(htmlTemplate);
      }
    });
  });
}

function createAlbum(albumName) {
  $.post('token', function(data) {
    var s3 = getS3Client(data);
    albumName = albumName.trim();
    if (!albumName) {
      return alert("Album names must contain at least one non-space character.");
    }
    if (albumName.indexOf("/") !== -1) {
      return alert("Album names cannot contain slashes.");
    }
    var albumKey = encodeURIComponent(albumName) + "/";
    s3.headObject({Key: albumKey}, function (err, data) {
      if (!err) {
        return alert("Album already exists.");
      }
      if (err.code !== "NotFound") {
        return alert("There was an error creating your album: " + err.message);
      }
      s3.putObject({Key: albumKey}, function (err, data) {
        if (err) {
          return alert("There was an error creating your album: " + err.message);
        }
        alert("Successfully created album.");
        viewAlbum(albumName);
      });
    });
  });
}

function viewAlbum(albumName) {
  $.post('token', function(data) {
    var s3 = getS3Client(data);

    var albumPhotosKey = encodeURIComponent(albumName) + "/";
    s3.listObjects({Prefix: albumPhotosKey}, function (err, data) {
      if (err) {
        return alert("There was an error viewing your album: " + err.message);
      }
      // 'this' references the AWS.Response instance that represents the response
      var href = this.request.httpRequest.endpoint.href;
      var bucketUrl = href + albumBucketName + "/";

      var photos = data.Contents
          .filter(function(it) {
            return it.Key != (albumName + '/')
          })
          .map(function (photo) {
        var photoKey = photo.Key;
        var photoUrl = bucketUrl + encodeURIComponent(photoKey);
        return getHtml([
          "<span>",
          "<div>",
          '<img style="width:128px;height:128px;" src="' + photoUrl + '"/>',
          "</div>",
          "<div>",
          "<span onclick=\"deletePhoto('" +
          albumName +
          "','" +
          photoKey +
          "')\">",
          "X",
          "</span>",
          "<span>",
          photoKey.replace(albumPhotosKey, ""),
          "</span>",
          "</div>",
          "</span>"
        ]);
      });
      var message = photos.length
          ? "<p>Click on the X to delete the photo</p>"
          : "<p>You do not have any photos in this album. Please add photos.</p>";
      var htmlTemplate = [
        "<h2>",
        "Album: " + albumName,
        "</h2>",
        message,
        "<div>",
        getHtml(photos),
        "</div>",
        '<input id="photoupload" type="file" accept="image/*">',
        '<button id="addphoto" onclick="addPhoto(\'' + albumName + "')\">",
        "Add Photo",
        "</button>",
        '<button onclick="listAlbums()">',
        "Back To Albums",
        "</button>"
      ];
      document.getElementById("app").innerHTML = getHtml(htmlTemplate);
    });
  });
}

function addPhoto(albumName) {
  $.post('token', function(data) {
    var s3 = getS3Client(data);

    var files = document.getElementById("photoupload").files;
    if (!files.length) {
      return alert("Please choose a file to upload first.");
    }
    var file = files[0];
    var fileName = file.name;
    var uuid = uuidv4();
    var photoKey = 'uploads/' + uuid;

    // Use S3 ManagedUpload class as it supports multipart uploads
    var upload = new AWS.S3.ManagedUpload({
      params: {
        Bucket: albumBucketName,
        Key: photoKey,
        Body: file,
        ACL: "public-read"
      },
      tags: [{
        Key: 'nl-album', Value: albumName
      }, {
        Key: 'nl-filename', Value: fileName
      }, {
        Key: 'nl-uuid', Value: uuid
      }]

    });

    var promise = upload.promise();

    promise.then(
        function (data) {
          $.post('upload',
              { file: uuid,
                album: albumName,
                targetFile: fileName},
              function(data) {
            viewAlbum(albumName);
          });
        },
        function (err) {
          return alert("There was an error uploading your photo: ", err.message);
        }
    );
  });
}

function deletePhoto(albumName, photoKey) {
  $.post('token', function(data) {
    var s3 = getS3Client(data);

    s3.deleteObject({Key: photoKey}, function (err, data) {
      if (err) {
        return alert("There was an error deleting your photo: ", err.message);
      }
      alert("Successfully deleted photo.");
      viewAlbum(albumName);
    });
  });
}

function deleteAlbum(albumName) {
  $.post('token', function(data) {
    var s3 = getS3Client(data);

    var albumKey = encodeURIComponent(albumName) + "/";
    s3.listObjects({Prefix: albumKey}, function (err, data) {
      if (err) {
        return alert("There was an error deleting your album: ", err.message);
      }
      var objects = data.Contents.map(function (object) {
        return {Key: object.Key};
      });
      s3.deleteObjects(
          {
            Delete: {Objects: objects, Quiet: true}
          },
          function (err, data) {
            if (err) {
              return alert("There was an error deleting your album: ", err.message);
            }
            alert("Successfully deleted album.");
            listAlbums();
          }
      );
    });
  });
}
