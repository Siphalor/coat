import de.siphalor.jcyo.gradle.JcyoTask
import java.util.*

plugins {
	alias(libs.plugins.loom)
	java
	`maven-publish`
	alias(libs.plugins.jcyo)
}

val minecraftVersionDescriptor = project.properties["minecraft.version.descriptor"] as String
val mcProps = Properties().apply {
	val propFile = project.layout.settingsDirectory.file("gradle/mc-${minecraftVersionDescriptor}/gradle.properties")
	load(propFile.asFile.inputStream())
}

group = "de.siphalor.${project.name}"
val archivesBaseName = "${project.name}-mc${minecraftVersionDescriptor}"
val shortVersion = "${properties["version"]}"
version = "${shortVersion}+mc${mcLibs.versions.minecraft.get()}"

val extraSources = mcProps["extra_sources"]?.toString()?.split(",")?.map { file("src/${it.trim()}") } ?: listOf()
val mergedAccessWidenerDir = project.layout.buildDirectory.dir("merged-accesswidener")
val mergedAccessWidenerName = "coat.accesswidener"
val mergedAccessWidenerFile = mergedAccessWidenerDir.map { it.file(mergedAccessWidenerName) }

sourceSets {
	main {
		extraSources.forEach {
			java.srcDir(it.resolve("java"))
			resources.srcDir(it.resolve("resources"))
		}
		resources.srcDir(mergedAccessWidenerDir)
	}
	create("testmod") {
		compileClasspath += sourceSets.main.get().compileClasspath
		runtimeClasspath += sourceSets.main.get().runtimeClasspath
	}
}

val wideners = extraSources.flatMap { it.listFiles { _, name -> name.endsWith(".accesswidener") }.orEmpty().toList() }
mergedAccessWidenerDir.get().asFile.mkdirs()
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
	maven {
		name = "Siphalor"
		url = uri("https://maven.siphalor.de")
		mavenContent {
			includeGroupAndSubgroups("de.siphalor")
		}
	}
	mavenLocal()
}

configurations {
	apiElements {
		outgoing.capability("de.siphalor:coat-${mcProps["minecraft.version.major"]}:${shortVersion}")
	}
	runtimeElements {
		outgoing.capability("de.siphalor:coat-${mcProps["minecraft.version.major"]}:${shortVersion}")
	}
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
	val mixins = sourceSets.main.get().resources.srcDirs
		.flatMap { it.listFiles { f -> f.name.endsWith("mixins.json") }.orEmpty().toList() }
		.joinToString(",") { "\"${it.name}\"" }
	inputs.property("extraMixins", mixins)

	from(sourceSets.main.get().resources.srcDirs) {
		include("fabric.mod.json")
		expand(
			"version" to project.version,
			"mixins" to mixins
		)
		duplicatesStrategy = DuplicatesStrategy.INCLUDE
	}
}

java {
	sourceCompatibility = JavaVersion.toVersion(mcLibs.versions.java.get())
	targetCompatibility = JavaVersion.toVersion(mcLibs.versions.java.get())
}

val jcyoVars: Map<String, String> = mcProps.stringPropertyNames()
	.filter { it.startsWith("preprocessor.") }
	.map { it to mcProps[it] }
	.associate { (key, value) -> key.substring("preprocessor.".length) to value.toString() }

val jcyo = registerJcyoTask("jcyo", "src/main/java")
val renderStateHelpersJcyo = registerJcyoTask("renderStateHelpersJcyo", "src/render-state-helpers/java")
val testmodJcyo = registerJcyoTask("testmodJcyo", "src/testmod/java")
fun registerJcyoTask(name: String, input: String): TaskProvider<JcyoTask> {
	return tasks.register<JcyoTask>(name) {
		inputDirectory = file(input)
		variables = jcyoVars
	}
}

tasks.compileJava {
	dependsOn(jcyo, renderStateHelpersJcyo)
}
tasks.named("compileTestmodJava") {
	dependsOn(testmodJcyo)
}

tasks.jar {
	from(file("LICENSE"))
}


publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = archivesBaseName
			version = shortVersion

			from(components["java"])
		}
	}

	repositories {
		if (project.hasProperty("siphalor.maven.user")) {
			maven {
				name = "Siphalor"
				url = uri("https://maven.siphalor.de/upload.php")
				credentials {
					username = project.property("siphalor.maven.user") as String
					password = project.property("siphalor.maven.password") as String
				}
			}
		}
	}
}

