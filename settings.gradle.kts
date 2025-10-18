import java.util.*

pluginManagement {
	repositories {
		mavenLocal()
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		gradlePluginPortal()
	}
}

val properties = Properties()
properties.load(file("gradle.properties").inputStream())

dependencyResolutionManagement {
	versionCatalogs {
		create("mcLibs") {
			from(files("gradle/mc-${properties["minecraft.version.descriptor"]}/mc.versions.toml"))
		}
	}
}

rootProject.name = "coat"
