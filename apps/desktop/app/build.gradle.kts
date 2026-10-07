plugins { kotlin("jvm"); id("org.jetbrains.compose"); id("org.jetbrains.kotlin.plugin.compose") }
kotlin { jvmToolchain(17) }
dependencies { implementation(compose.desktop.currentOs); implementation(compose.material3); implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0"); implementation("org.json:json:20240303"); implementation("net.java.dev.jna:jna-platform:5.15.0"); implementation("uk.co.caprica:vlcj:4.8.3") }

val vlcRuntimeDir=layout.projectDirectory.dir("runtime/vlc")
val verifyVlcRuntime by tasks.registering {
 doLast {
  val dir=vlcRuntimeDir.asFile
  val required=listOf("libvlc.dll","libvlccore.dll","plugins")
  val missing=required.filter{!dir.resolve(it).exists()}
  if(missing.isNotEmpty()) throw GradleException("VLC runtime incompleto em ${dir.path}: faltando ${missing.joinToString()}")
 }
}
compose.desktop { application { mainClass="com.superiptv.desktop.MainKt"; nativeDistributions {
 targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe)
 packageName="SuperIPTV"; packageVersion="0.1.0"; description="SuperIPTV Desktop"
 appResourcesRootDir.set(project.layout.projectDirectory.dir("runtime"))
} } }
