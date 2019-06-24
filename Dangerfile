has_files_to_lint = (git.added_files + git.modified_files).any? {|f| f.end_with? '.kt'}

lint_success = !has_files_to_lint || system("./gradlew ktlint")
fail 'ktlint failed; please run `./gradlew ktlintformat` to fix formatting issues.' unless lint_success
