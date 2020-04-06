# Rest API test tools for JVM

This project is not a framework, but rather a set of tools and practices to get the most out of REST API testing. 

### Motivation

The existing REST testing tools are on one hand user friendly, not requiring much programming knowledge, but on the other hand not powerful enough to cover more complex use cases. 
These tools allow you to test basic contract of an API, but fall behind in power and flexibility to allow for more complex, or functional API testing.

While you can test REST APIs with traditional unit testing frameworks in combination with many great REST clients that are available,
this approach usually yields very complex and un-readable tests. 

This project is trying to find the sweets spot between readability and power by using a set of proven tools testing
together with a test writing style that tries to provide as much readability as possible.

### Usage

**Gradle**
```groovy
repositories {
	maven { url 'https://jitpack.io' }
}
```
```groovy
testCompile 'com.github.motycka:groovy-rest-test:v1.0.0-beta.1'
```

**Maven**

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.motycka</groupId>
    <artifactId>groovy-rest-test</artifactId>
    <version>v1.0.0-beta.1</version>
    <scope>test</scope>
</dependency>
```


### Examples

TBD