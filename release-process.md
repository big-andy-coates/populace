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

### Bump the version number:
1. `git checkout master`
1. Change the `version` variable in the root `build.gradle` file. Populace uses [semantic versioning](semver.org).
1. Search for the old version number and replace with new version. This will normally only be found in documentation.
1. Check in and push changes.

### Create release branch:
1. `git checkout master`
1. `git checkout -b release/<new version number>` e.g. `git checkout -b release/1.2.3`
1. `git push origin release/<new version number>`

### Packaging release
Couldn't be easier. Just run:

> `gradlew uploadArchives`

This will build the archives and upload them to the staging area of Sonatype OSS. They then need to be released.

### Promoting release on maven central
Follow these [Sonatype OSS release instructions](http://central.sonatype.org/pages/releasing-the-deployment.html)