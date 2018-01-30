repositories {
    mavenCentral()
    maven( url = "http://dl.bintray.com/arturbosch/code-analysis")
}

val detektConf by configurations.creating

dependencies {
    detektConf("io.gitlab.arturbosch.detekt:detekt-cli:1.0.0.RC6-2")
}
val detekt by tasks.creating(JavaExec::class) {
    group = "verification"
    main = "io.gitlab.arturbosch.detekt.cli.Main"
    classpath = detektConf
    val input = "${project.projectDir.absolutePath}"
    val config = "${project.projectDir}/detekt.yml"
    val reports = "${project.projectDir.absolutePath}/reports/"
    val baseline = "${project.projectDir.absolutePath}/reports/baseline.xml"
    val filters = ".*test.*, .*/build/.*"
    val params = listOf("-i", input, "-c", config, "-f", filters, "-o", reports, "-b", baseline, "--parallel")
    args(params)
}

val detektEstablishAcceptedErrors by tasks.creating(JavaExec::class) {
    group = "verification"
    main = "io.gitlab.arturbosch.detekt.cli.Main"
    classpath = detektConf
    val input = "${project.projectDir.absolutePath}"
    val config = "${project.projectDir}/detekt.yml"
    val reports = "${project.projectDir.absolutePath}/reports/"
    val baseline = "${project.projectDir.absolutePath}/reports/baseline.xml"
    val filters = ".*test.*, .*/build/.*"
    val params = listOf("-i", input, "-c", config, "-f", filters, "-o", reports, "-b", baseline, "-cb", "--parallel")
    args(params)
}
