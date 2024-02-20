plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.5.11"
  id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "ru.ostrov77"
version = "3.0"
description = "ostrov77"

dependencies {
  paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
  implementation(fileTree("libs"))
}

sourceSets {
  main {
    java {
      srcDir("src/")
    }
    resources {
      srcDir("resources/")
    }
  }
}

tasks {
  // Configure reobfJar to run when invoking the build task
  assemble {
    dependsOn(reobfJar)
  }

  java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

    // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
    // See https://openjdk.java.net/jeps/247 for more information.
    //options.release.set(17)
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }



  reobfJar {
    // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
    // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
    outputJar.set(layout.buildDirectory.file("Ostrov.jar"))
  }
}






/*
dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.20.1-R0.1-SNAPSHOT")

    implementation("io.papermc", "paperlib", "1.0.8")
    implementation("xyz.jpenilla", "legacy-plugin-base", "0.0.1+98-SNAPSHOT")
    implementation("org.bstats", "bstats-bukkit", "3.0.2")

    implementation(platform("cloud.commandframework:cloud-bom:1.8.4"))
    implementation("cloud.commandframework", "cloud-paper")
    implementation("cloud.commandframework", "cloud-minecraft-extras")

    implementation("org.incendo.interfaces", "interfaces-paper", "1.0.0-SNAPSHOT")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7.1")
    compileOnly("net.essentialsx", "EssentialsX", "2.20.1") {
        isTransitive = false
    }
    compileOnly("org.checkerframework", "checker-qual", "3.40.0")
    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.0.9") {
        exclude("org.bukkit")
    }
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.17")
}
 */




/*processResources {
    filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    val props = mapOf(
      "name" to project.name,
      "version" to project.version,
      "description" to project.description,
      "apiVersion" to "1.20"
    )
    inputs.properties(props)
    filesMatching("plugin.yml") {
      expand(props)
    }
}*/

/*
reobfJar {
  // This is an example of how you might change the output location for reobfJar. It's recommended not to do this
  // for a variety of reasons, however it's asked frequently enough that an example of how to do it is included here.
  outputJar.set(layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar"))
}
 */
