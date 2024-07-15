# Development

## Publish New Version

1. Update new version in `tsm4j/build.gradle` and `README.md`
2. Delete the old `tsm4j/build` directory if any, and create a new version locally
   ```bash
   rm -r tsm4j/build
   ./gradlew publishTsm4jPublicationToMavenRepository
   ```
   > :note: If you see
   > ```
   > > Cannot perform signing task ':tsm4j:signTsm4jPublication' because it has no configured signatory
   > ```
   > This means you do not have the right settings in  `~/.gradle/gradle.properties`, i.e. `signing.keyId`, `signing.password`, and `signing.secretKeyRingFile`.
   > For me, this probably means I am not running the command in my wsl2 ubuntu.
3. Zip the release files
   ```bash
   cd tsm4j/build/repos/releases
   zip release.zip ./com/tsm4j/tsm4j/1.0.2/*
   cd -
   ```
4. Open https://central.sonatype.com/publishing
   1. sign in (continue with google)
   2. upload
   3. wait for validation
   4. publish
