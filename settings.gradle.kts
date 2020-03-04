rootProject.name = "uuid"

buildCache {
    local {
        directory = "$rootDir/build/cache/"
        removeUnusedEntriesAfterDays = 30
    }
}
