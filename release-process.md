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
1. `git pull`
1. Change the `version` variable in the root `build.gradle` file. Populace uses [semantic versioning](semver.org).
1. Search for the old version number and replace with new version. This will normally only be found in documentation.
1. Check in and push changes.

### Create release branch:
1. `git checkout master`
1. `git pull`
1. `git checkout -b release/<new version number>` e.g. `git checkout -b release/1.2`
1. `git push origin release/<new version number>`

### Packaging release
Couldn't be easier. Just run:

> `gradlew uploadArchives`

This will build the archives and upload them to the staging area of Sonatype OSS. They then need to be released.

### Promoting release on maven central
Follow these [Sonatype OSS release instructions](http://central.sonatype.org/pages/releasing-the-deployment.html)

### Release on Github
Create a release on Github. The release description should include details of all issues resolved, any breaking changes
and anything else relevant to the release.

### Releasing the javadocs
Aside from the javadocs jar released to maven central, the javadocs for the released version should also be made
available on the projects website i.e. <http://datalorax.github.io/populace>. This is currently a manual process.

1. Switch to the release branch: `git checkout release/<release-number>`
1. Build the javadocs: `gradlew clean javadoc`
1. Copy the contents of `/populace/populace-core/build/docs/javadoc` and
`/populace/populace-module-jaxb-annotations/build/docs/javadoc` somewhere outside the repository
1. Switch to the gh-pages branch `git checkout gh-pages`
1. Copy the contents of the core module's javadoc directory into `javadocs/v<release-number>/populace-core`
1. Copy the contents of the jaxb module's javadoc directory into `javadocs/v<release-number>/populace-module-jaxb-annotations`
1. Update the downloads section of `/javadocs/index/html` to include buttons for the new core and jaxb versions.
1. Add, commit and push your changes.