S3 Direct Upload Demo
=====================

This is a POC of authenticated direct multi-part upload to S3 using temporary
credentials obtains from Amazon's [Security Token Service](https://docs.aws.amazon.com/STS/latest/APIReference/Welcome.html).

The simple javascript app uses a limited access role that allows listing of objects
and *only* uploading to an `uploads\` folder in the bucket.  The Java backend provides
a token endpoint that can return the temporary credentials to the client and an endpoint
to move the uploaded image to a different folder using different server-side credentials.


### AWS Setup 
* S3 bucket with public accesss enabled (everyone can list objects) and the following bucket policy:  
```
{
    "Version": "2008-10-17",
    "Statement": [
        {
            "Sid": "",
            "Effect": "Allow",
            "Principal": {
                "AWS": "*"
            },
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::(bucket-name)/*"
        },
        {
            "Sid": "",
            "Effect": "Allow",
            "Principal": {
                "AWS": "arn:aws:iam::(account-id):role/(role name)"
            },
            "Action": "s3:PutObject",
            "Resource": "arn:aws:s3:::(bucket-name)/uploads/*"
        }
    ]
}
```
* An IAM role with the following policy attached to allow public access to the images, but restrict uploads:
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:PutObjectTagging",
                "s3:PutObjectAcl"
            ],
            "Resource": "arn:aws:s3:::(bucket)/uploads/*"
        },
        {
            "Effect": "Allow",
            "Action": "s3:ListBucket",
            "Resource": "arn:aws:s3:::(bucket)"
        }
    ]
}
```
* A trust relationship on the IAM role to whatever identity the backend is running with so the backend can assume the role
and ask for temporary credentials
* AWS access key and secret access key for the backend to use with full S3 access

### To Run
* Set `AWS_ACCESS_KEY_ID` and `AWS_SECRET_KEY` as environment variables for the Java backend and run the Spring Boot application
* [http://localhost:8080/](http://localhost:8080)


### TO DOs
* Since the browser is relied on to trigger the backend to move the file out of `uploads/` there needs
to be some kind of periodic cleanup task that looks for old files in `uploads/`.  Tags are attached to the uploads
to help identify them
* Error handling for the move (particularly around name collisions)
* Add/delete album, delete photo, etc. on the backend (the original example this is based on was a completely front-end solution 
and for purposes of this POC we only want the front end to have upload access)
* Lots of cleanup - the javascript code is ugly and inefficient

