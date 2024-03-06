# Development

## Publish New Version

1. Update new version in `tsm4j/build.gradle`
2. Create a new version locally
   ```bash
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
   zip release.zip ./com/tsm4j/tsm4j/0.0.3/*
   ```
4. Open https://central.sonatype.com/publishing
   1. sign in
   2. upload
   3. wait for validation
   4. publish
