plugins {
    id("io.github.tree-sitter.ktreesitter-plugin") version "0.23.0"
//    kotlin("jvm")
    kotlin("multiplatform")
    id("sunnychung.publication")
}

val treeSitterJsonVersion = "0.23.0"

group = "io.github.sunny-chung"
version = "$treeSitterJsonVersion.0"

grammar {
    /* Default options */
    // The base directory of the grammar
    baseDir.set(file("tree-sitter-json"))
//    // The name of the C interop def file
//    interopName.set("grammar")
//    // The name of the JNI library
//    libraryName.set("ktreesitter-${grammarName.get()}")
    libraryName.set("tree-sitter-json")

    /* Required options */
    // The name of the grammar
    grammarName.set("json")
    // The name of the class
    className.set("TreeSitterJson")
    // The name of the package
    packageName.set("io.github.treesitter.ktreesitter.json")
    // The source files of the grammar
    files.set(arrayOf(
        baseDir.get().resolve("src/parser.c"),
        // baseDir.get().resolve("src/scanner.c")
    ))
}

repositories {
    mavenCentral()
}

val generateTask = tasks.generateGrammarFiles.get()

kotlin {
    jvm {}

    jvmToolchain(17)

    sourceSets {
        val generatedSrc = generateTask.generatedSrc.get()
        configureEach {
            kotlin.srcDir(generatedSrc.dir(name).dir("kotlin"))
            println("Configure: $name")
        }

        commonMain {
            resources.srcDir(generatedSrc.dir("resources"))
        }
    }
}

/**
 * The purpose of this task is to disable default `System.loadLibrary()` calls,
 * and fix the CMakeLists.txt for Windows.
 */
val enhancedGenerateTask = task("generateGrammarFilesEnhanced") {
    dependsOn(generateTask)

    doLast {
        val generatedSrc = generateTask.generatedSrc.get()
        val jvmSrc = generatedSrc.dir("jvmMain").dir("kotlin")
        jvmSrc.asFileTree.filter { it.isFile && it.name.endsWith(".kt") }
            .forEach { file ->
                println("Check src ${file.name}")
                val sourceCode = file.readText()
                if (sourceCode.contains("^\\s*System\\.loadLibrary\\(".toRegex(RegexOption.MULTILINE))) {
                    println("Replacing ${file.name}")
                    val enhancedCode = sourceCode.replace("^(\\s*)(System\\.loadLibrary\\()".toRegex(RegexOption.MULTILINE)) {
                        "${it.groups[1]!!.value}// ${it.groups[2]!!.value}"
                    }
                    println(enhancedCode)
                    file.writeText(enhancedCode)
                }
            }

        if (isWindowsOs()) {
            val file = layout.buildDirectory.get().dir("generated").file("CMakeLists.txt").asFile
            val sourceCode = file.readText()
            if (sourceCode.contains("\\")) {
                println("Replacing ${file.name}")
                val enhancedCode = sourceCode.replace("\\", "/")
                println(enhancedCode)
                file.writeText(enhancedCode)
            }
        }
    }
}

fun isWindowsOs(): Boolean {
    return System.getProperty("os.name").startsWith("Win")
}
