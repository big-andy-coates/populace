# How to release
Releases are published to Maven Central via [OSS Repository Hosting](http://central.sonatype.org/pages/ossrh-guide.html)

## Prerequisites
The credentials needed for signing and uploading should be stored in your gradle.properties file in your home directory.
The content would look like this:

```
signing.keyId=YourKey
signing.password=YourKeyPassword
signing.secretKeyRingFile=/path/to/your/key.gpg

ossrhUsername=yourOssrhUsername
ossrhPassword=yourOssrhPassword
```

If you don't have a public/private key, see the [gradle signing documentation](http://www.gradle.org/docs/current/userguide/signing_plugin.html)
for instructions.

If you don't have a OSSRH account, then [sign up for one](https://issues.sonatype.org/secure/Signup!default.jspa).

## Releasing
Couldn't be easier. Just run:

> `gradlew uploadArchives`