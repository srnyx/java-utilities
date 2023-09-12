# srnyx's Java Utilities [![Release](https://jitpack.io/v/srnyx/java-utilities.svg)](https://jitpack.io/#xyz.srnyx/java-utilities)

A general Java utility library for srnyx's projects

### Wiki / Javadocs

- **Wiki:** [github.com/srnyx/java-utilities/wiki](https://github.com/srnyx/java-utilities/wiki)
- **Javadocs:** [javadoc.jitpack.io/xyz/srnyx/java-utilities/latest/javadoc/index.html](https://javadoc.jitpack.io/xyz/srnyx/java-utilities/latest/javadoc/index.html)

## Importing

You can import the library using [Jitpack](https://jitpack.io/#xyz.srnyx/java-utilities). Make sure to replace `VERSION` with the version you want. You **MUST** use `implementation`.

- **Gradle Kotlin** (`build.gradle.kts`)**:**
```kotlin
// Required plugins
plugins { 
  java
  id("com.github.johnrengelman.shadow") version "8.1.1" // https://github.com/johnrengelman/shadow/releases/latest
}
// Jitpack repository
repositories { 
  maven("https://jitpack.io")
}
// Lazy Library dependency declaration
dependencies {
  implementation("xyz.srnyx", "java-utilities", "VERSION")
}
```
- **Gradle Groovy** (`build.gradle`)**:**
```groovy
// Required plugins
plugins {
  id 'java'
  id 'com.github.johnrengelman.shadow' version '8.1.1' // https://github.com/johnrengelman/shadow/releases/latest
}
// Jitpack repository
repositories {
  maven { url = 'https://jitpack.io' }
}
// Lazy Library dependency declaration
dependencies {
  implementation 'xyz.srnyx:java-utilities:VERSION'
}
```
* **Maven** (`pom.xml`)**:**
    * Shade plugin
  ```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.4.1</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>shade</goal>
            </goals>
          </execution>
        </executions>
        <!-- Exclude META-INF to avoid conflicts (not sure if this is needed) -->
        <configuration>
          <filters>
            <filter>
              <artifact>xyz.srnyx:*</artifact>
              <excludes>
                <exclude>META-INF/*.MF</exclude>
              </excludes>
            </filter>
          </filters>
        </configuration>
      </plugin>
    </plugins>
  </build>
  ```
    * Jitpack repository
  ```xml
   <repositories>
        <repository>
            <id>jitpack</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
  ```
    * Lazy Library dependency declaration
  ```xml
    <dependencies>
        <dependency>
            <groupId>xyz.srnyx</groupId>
            <artifactId>java-utilities</artifactId>
            <version>VERSION</version>
        </dependency>
    </dependencies>
  ```
