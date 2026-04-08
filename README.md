# srnyx's Java Utilities [![Release](https://repo.srnyx.com/api/badge/latest/releases/xyz/srnyx/java-utilities?color=006d82&name=Reposilite)](https://repo.srnyx.com/#/releases/xyz/srnyx/java-utilities)

A general Java utility library for srnyx's projects

### Wiki / Javadocs

- **Wiki:** [github.com/srnyx/java-utilities/wiki](https://github.com/srnyx/java-utilities/wiki)
- **Javadocs:** [repo.srnyx.com/javadoc/releases/xyz/srnyx/java-utilities/latest](https://repo.srnyx.com/javadoc/releases/xyz/srnyx/java-utilities/latest)

## Importing

You can import the library using [Reposilite](https://repo.srnyx.com/#/releases/xyz/srnyx/java-utilities). Make sure to replace `VERSION` with the version you want.

- **Gradle Kotlin** (`build.gradle.kts`)**:**
```kotlin
// Required plugins
plugins { 
  java
  id("com.gradleup.shadow") version "8.3.9" // https://github.com/GradleUp/shadow/releases/latest
}
// Reposilite repository
repositories { 
  maven("https://repo.srnyx.com/releases/")
}
// Java Utilities dependency declaration
dependencies {
  implementation("xyz.srnyx:java-utilities:VERSION")
}
```
- **Gradle Groovy** (`build.gradle`)**:**
```groovy
// Required plugins
plugins {
  id 'java'
  id 'com.gradleup.shadow' version '8.3.9' // https://github.com/GradleUp/shadow/releases/latest
}
// Reposilite repository
repositories {
  maven { url = 'https://repo.srnyx.com/releases/' }
}
// Java Utilities dependency declaration
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
    * Reposilite repository
  ```xml
   <repositories>
        <repository>
            <id>srnyx</id>
            <url>https://repo.srnyx.com/releases/</url>
        </repository>
    </repositories>
  ```
    * Java Utilities dependency declaration
  ```xml
    <dependencies>
        <dependency>
            <groupId>xyz.srnyx</groupId>
            <artifactId>java-utilities</artifactId>
            <version>VERSION</version>
        </dependency>
    </dependencies>
  ```
