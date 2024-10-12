# KTreeSitter Language Grammar - JSON

## Build

```sh
gradle :ktreesitter-json:generateGrammarFilesEnhanced

pushd build/generated/
cmake CMakeLists.txt
make

mkdir src/resources
cp lib* src/resources/
cp *.dll src/resources/

popd
gradle :ktreesitter-json:assemble
``` 

## Use

In build.gradle.kts, put:
```kotlin
implementation(project(":ktreesitter-json"))
```
