import de.siphalor.jcyo.gradle.JcyoTask
import java.util.*

plugins {
	alias(libs.plugins.loom)
	java
	`maven-publish`
	alias(libs.plugins.jcyo)
}

//archivesBaseName = project.archives_base_name
group = "de.siphalor.coat"
version = properties["version"]!!

val mcProps = Properties().apply {
	val propFile = project.layout.settingsDirectory.file("gradle/mc-${project.properties["minecraft_version_descriptor"]}/gradle.properties")
	load(propFile.asFile.inputStream())
}

val extraSources = mcProps["extra_sources"]?.toString()?.split(",")?.map { file("src/${it.trim()}") } ?: listOf()
val mergedAccessWidenerDir = project.layout.buildDirectory.dir("merged-accesswidener")
val mergedAccessWidenerName = "coat.accesswidener"
val mergedAccessWidenerFile = mergedAccessWidenerDir.map { it.file(mergedAccessWidenerName) }

sourceSets {
	main {
		extraSources.forEach {
			java.srcDir(it.resolve("java"))
		}
		resources.srcDir(mergedAccessWidenerDir)
	}
	create("testmod") {
		compileClasspath += sourceSets.main.get().compileClasspath
		runtimeClasspath += sourceSets.main.get().runtimeClasspath
	}
}

val wideners = extraSources.flatMap { it.listFiles { _, name -> name.endsWith(".accesswidener") }.orEmpty().toList() }
val merged = mergedAccessWidenerDir.get().file(mergedAccessWidenerName).asFile
merged.createNewFile()
val writer = merged.writer()
writer.write("accessWidener v1 named\n")
wideners.forEach {
	it.bufferedReader().let { reader ->
		reader.readLine()
		writer.write(reader.readText())
		reader.close()
	}
}
writer.close()

loom {
	accessWidenerPath.set(mergedAccessWidenerFile)

	runs {
		create("testmodClient") {
			client()
			name("Testmod Client")
			source(sourceSets.getByName("testmod"))
		}
	}
}

repositories {
	maven { url = uri("https://maven.siphalor.de") }
	mavenLocal()
}

dependencies {
	annotationProcessor(libs.lombok)
	compileOnly(libs.lombok)

	minecraft(mcLibs.minecraft)
	mappings(loom.officialMojangMappings())
	modImplementation(libs.fabric.loader)

	// testmod dependencies will not be remapped in the testmodImplementation configuration
	"modImplementation"(mcLibs.amecs.api) {
		exclude(module = "lazydfu")
	}
	"modImplementation"(fabricApi.module("fabric-api-base", mcLibs.versions.fabric.api.get()))
	"modImplementation"(fabricApi.module("fabric-key-binding-api-v1", mcLibs.versions.fabric.api.get()))
	"modImplementation"(fabricApi.module("fabric-resource-loader-v0", mcLibs.versions.fabric.api.get()))

	"testmodImplementation"(sourceSets.main.map { it.output })
}

tasks.processResources {
	inputs.property("version", project.version)

	from(sourceSets.main.get().resources.srcDirs) {
		include("fabric.mod.json")
		expand("version" to project.version)
		duplicatesStrategy = DuplicatesStrategy.INCLUDE
	}
}

java {
	sourceCompatibility = JavaVersion.toVersion(mcLibs.versions.java.get())
	targetCompatibility = JavaVersion.toVersion(mcLibs.versions.java.get())
}

val jcyoVars = mcProps.stringPropertyNames()
	.filter { it.startsWith("preprocessor.") }
	.map { it to mcProps[it] }
	.associate { (key, value) -> key.substring("preprocessor.".length) to value }
val jcyo = tasks.register<JcyoTask>("jcyo") {
	inputDirectory = file("src/main/java")
	variables = jcyoVars
}
val testmodJcyo = tasks.register<JcyoTask>("testmodJcyo") {
	inputDirectory = file("src/testmod/java")
	variables = jcyoVars
}

tasks.compileJava {
	dependsOn(jcyo)
}
tasks.named("compileTestmodJava") {
	dependsOn(testmodJcyo)
}

tasks.jar {
	from(file("LICENSE"))
}
