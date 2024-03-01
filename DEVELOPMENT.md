# Development

## Publish New Version

1. Update new version in `tsm4j/build.gradle`
2. Create a new version locally
   ```bash
    ./gradlew publishTsm4jPublicationToMavenRepository
   ```
3. Navigate to `tsm4j/build/repos/releases` and run
   ```bash
   zip release.zip ./com/tsm4j/tsm4j/0.0.2/*
   ```
4. Open https://central.sonatype.com/publishing
   1. sign in
   2. upload
   3. wait for validation
   4. publish
