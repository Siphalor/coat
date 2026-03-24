pluginManagement {
	repositories {
		mavenLocal()
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
			mavenContent {
				includeGroupAndSubgroups("net.fabricmc")
				includeGroup("fabric-loom")
			}
		}
		maven {
			name = "Siphalor"
			url = uri("https://maven.siphalor.de")
			mavenContent {
				includeGroupAndSubgroups("de.siphalor")
			}
		}
		gradlePluginPortal()
	}
}

plugins {
	id("de.siphalor.minecraft-modding-toolkit.settings-plugin") version("0.1.0")
}

smcmtk {
	fabricLoomVersion = "1.15-SNAPSHOT"
}

rootProject.name = "coat"
