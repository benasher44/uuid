rootProject.name = "uuid"
enableFeaturePreview("GRADLE_METADATA")

buildCache {
    local<DirectoryBuildCache> {
        directory = "$rootDir/build/cache/"
        removeUnusedEntriesAfterDays = 30
    }
}
