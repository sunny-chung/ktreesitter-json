# KTreeSitter Language Grammar - JSON

![KTreeSitter JSON Language](https://img.shields.io/maven-central/v/io.github.sunny-chung/ktreesitter-json)

## What

This is the integration of [the JSON language grammar extension](https://github.com/tree-sitter/tree-sitter-json) to the [Kotlin Multiplatform official support](https://tree-sitter.github.io/kotlin-tree-sitter/) to the [Tree-sitter](https://tree-sitter.github.io/tree-sitter/) library.

It can parse a very large JSON text, potentially with grammar errors, into an AST (Abstract Syntax Tree) in an incremental basis (e.g. the full text is not parsed again when there are small changes). The AST can be used for syntax highlighting, structural analysis, pattern queries, etc..

## Use

In build.gradle.kts, put:
```kotlin
implementation("io.github.tree-sitter:ktreesitter:0.23.0")
implementation("io.github.sunny-chung:ktreesitter-json:0.23.0.1")
```

Example use:
```kotlin
fun loadNativeLibraries() {
    val systemArch = if (currentOS() == WindowsOS) {
        "x64"
    } else {
        getSystemArchitecture()
    }.uppercase()
    val libFileName = when (currentOS()) {
        LinuxOS -> "lib${name}-${systemArch}.so"
        MacOS -> "lib${name}-${systemArch}.dylib"
        else -> "${name}-${systemArch}.dll"
    }
    val dest = File("temp", libFileName)
    dest.parentFile.mkdirs()
    TreeSitterJson.javaClass.classLoader.getResourceAsStream(libFileName).use { `is` ->
        `is` ?: throw RuntimeException("Lib $libFileName not found")
        FileOutputStream(dest).use { os ->
            `is`.copyTo(os)
        }
    }
    System.load(dest.absolutePath)
}

val language = Language(TreeSitterJson.language())
val parser = Parser(language)

var testString = "{\n" +
        "  \"a\":\"fasd\",\n" +
        "  \"fasf\":\"sags\",\n" +
        "  \"sfgsfg\": \"aewda\"\n" +
        "}"
val accessor: ParseCallback = { byte: UInt, point: Point ->
    if (byte.toInt() in testString.indices) {
        testString[byte.toInt()].toString()
    } else {
        ""
    }
}

var ast = parser.parse(null, accessor)
println("AST: ${ast.rootNode.sexp()}")
```

For detailed and latest usage guide & API, please refer to the official documentation. This example will not be updated over time.

## Versioning

### `"${tree-sitter-json-version}.${patch}"`
, where `patch` is a non-negative integer, starting from `0`, indicating the version of the bug fix integration release to that [tree-sitter-json](https://github.com/tree-sitter/tree-sitter-json) version.

## Manual Build

As you may observe, there is no custom code added here. You can build on you own.

Note that the C library binaries for each OS / architecture need to be built separately on each OS & architecture combination.

For macOS / Linux,

```sh
gradle :ktreesitter-json:generateGrammarFilesEnhanced

pushd build/generated/
cmake CMakeLists.txt
make

mkdir src/resources
cp lib* src/resources/

popd
gradle :ktreesitter-json:assemble
```

For Windows,

```sh
gradle :ktreesitter-json:generateGrammarFilesEnhanced

cd build/generated/
cmake CMakeLists.txt
cmake --build . --target INSTALL --config Release

mkdir src/resources
cp Release/*.dll src/resources/

cd ../..
gradle :ktreesitter-json:assemble
```

