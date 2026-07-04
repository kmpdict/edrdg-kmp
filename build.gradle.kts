import io.gitlab.arturbosch.detekt.report.ReportMergeTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.detekt) apply false
}

tasks.register<ReportMergeTask>("detektMerge") {
    description = "Merges all existing Detekt reports into one merge.sarif"
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
    val reportTree = fileTree(baseDir = rootDir) {
        include("**/detekt/main.sarif")
    }
    input.from(reportTree)
}
