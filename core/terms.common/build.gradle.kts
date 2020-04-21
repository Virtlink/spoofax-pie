plugins {
  id("org.metaborg.gradle.config.java-library")
}

dependencies {
  api(platform(project(":spoofax.depconstraints")))

  api(project(":common"))
  api("org.metaborg:log.api")
  api("org.metaborg:org.spoofax.terms")

  compileOnly("org.checkerframework:checker-qual-android")
}