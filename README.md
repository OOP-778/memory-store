# Data Store
![Latest Version](https://img.shields.io/maven-metadata/v/https/repo.codemc.org/repository/maven-public/com/oop/memory-store/maven-metadata.xml.svg)


## Getting Started

### Get Data Store

Maven:
```
<repository>
    <id>code-mc</id>
    <url>https://repo.codemc.org/repository/maven-releases/</url>
</repository>

<dependency>
    <groupId>com.oop</groupId>
    <artifactId>memory-store</artifactId>
    <version>latest build version</version>
</dependency>
```

Gradle:
```
repositories {
    maven { url 'https://repo.codemc.org/repository/maven-releases/' }
}

dependencies {
    implementation "com.oop:memory-store:latest build version"
}
```

### Information
This is a fork of [link](https://github.com/jparams/data-store) that adds expiring store references.
All the credit goes to the original author.

### What is Data Store?
Data Store is a full-featured indexed Java Collection capable of ultra-fast data lookup.

Here is one I ran earlier...

![Benchmark Results](benchmark-results.png)

Fast, huh!

So how does it work? Well, Data Store, or “Store” for short, builds on top of java.util.Collection, providing a standard interface for adding, removing and iterating elements. This is where other implementation of java.util.Collection stop, but Data Store does a lot more. Data Store provides the ability to add multiple indexes on the elements stored in the collection. The indexing adds a slight overhead when adding elements into the collection, but makes element lookup ridiculously quick.

Whilst indexing is the key functionality we are trying to promote with this library, we have made an effort to ensure that Data Store is a delight to use. Do you need a synchronized Data Store for your multi-threaded environment? How about an unmodifiable view of your Data Store? Maybe you just want to copy an existing Data Store quickly and easily? You can do all this and more!

### Create a Data Store
Creating a Data Store is easy!

```java
Store<Person> store = new MemoryStore<>();
```

There we go, a shiny new Store has been created. Go ahead and use this as you would any other implementation of java.util.Collection. Add, remove, clear.. all the usual methods.

In case you were wondering, MemoryStore is the default implementation of the Store interface. It is a "Memory" Store because it holds all elements added to the Store in memory.

### Using a Data Store
Lets go ahead and use our Store. Here is some of the cool stuff you can do!

#### Indexing
Index your Store and enable ultra-fast data lookup. It is as simple as calling the `index` method. Here goes.....

```java
store.index("firstName", Person::getFirstName);
```

Easy, right?! With our index created, lets query some data and lets query them fast!

```java
List<Person> result1 = store.get("firstName", "James");
```

Let's do something a bit more advanced and work with multiple indexes!

```java
store.index("firstName", Person::getFirstName);
store.index("lastName", Person::getLastName);

// Lets do a more advanced query
Person person = store.getFirst(Query.where("firstName", "james").and("lastName", "smith"));
```

Data Store is backed by hash maps, so this lookup is quick. No looping required.

If you are still not sold on this awesome framework and want more indexing goodness, read on to learn about Reducers and Comparison Policies.

#### Reducer
A reducer provides a mechanism for reducing all values that map to the same key. 

#### Limit
You can use a limiting reducer to limit the number of values associated with a single index. Say, we only want to index the first 2 people with the first name James, well we can do this using a reducer.

Example:
```java
Index<Person> index = store.index(IndexDefinition.withKeyMapping(Person::getFirstName).withReducer(new MinReducer(2, Retain.OLDEST)));
```

This means that `index.get("James")` will only ever produce a max of two results. All other people will the first name James will not be indexed.

#### Min
Say, we want to group by first name then reduce the group down by the date of birth, well we can do this using a Min reducer. This means that we can easily find the youngest person with the given first name.

Example:
```java
Index<Person> index = store.index(IndexDefinition.withKeyMapping(Person::getFirstName).withReducer(new MinReducer(Person::getDateOfBirth, false)));
```

This means that `index.get("James")` will only ever produce a max of two results. All other people will the first name James will not be indexed.

#### Max
Say, we want to group by first name then reduce the group down by the date of birth, well we can do this using a Max reducer. This means that we can easily find the oldest person with the given first name.

Example:
```java
Index<Person> index = store.index(IndexDefinition.withKeyMapping(Person::getFirstName).withReducer(new MaxReducer(Person::getDateOfBirth, false)));
```

#### Comparison Policy
Lets build on top of the already powerful indexing feature by introducing Comparison Policies. These policies define how indexes are created and how they are matched.

Here are some available out of the box, but feel free to write your own!

##### Case Insensitive Comparison Policy
Use this policy for creating case insensitive indexes. Example:

```java
// Create a store
Store<Person> store = new MemoryStore<>();

// Add a case insensitive index
Index<Person> index = store.index(IndexDefinition.withKeyMapping(Person::getLastName).withComparisonPolicy(new CaseInsensitiveComparisonPolicy()));

// Query index - these will produce the same result
index.get("James");
index.get("JAMES");
index.get("james");
index.get("JAMes");
```

##### Offset Date Time Comparison Policy
Comparison policy for comparing two OffsetDateTime values normalized to an UTC time offset.

```java
// Create a store
Store<Person> store = new MemoryStore<>();

// Add a case insensitive index
Index<Person> index = store.addIndex(Person::getLastActive, new OffsetDateTimeComparisonPolicy());

// Query index - these will produce the same result
index.get(OffsetDateTime.of(LocalDateTime.of(2018, 5, 5, 13, 55, 30), ZoneOffset.ofHours(2)));
index.get(OffsetDateTime.of(LocalDateTime.of(2018, 5, 5, 14, 55, 30), ZoneOffset.ofHours(3)));
index.get(OffsetDateTime.of(LocalDateTime.of(2018, 5, 5, 15, 55, 30), ZoneOffset.ofHours(4)));
```

### Builder
For that extra continence, data store comes with a builder. If you want to do more with a single line of code, we have you covered.

Example:

```java
Store<Person> store = MemoryStore.<Person>newStore()
    .withIndex("firstName", Person::getFirstName)
    .withIndex("lastName", IndexDefinition.withKeyMapping(Person::getLastName).withComparisonPolicy(new CaseInsensitiveComparisonPolicy()))
    .withValues(person1, person32)
    .build();
```

### Expiring Store example
```java
    final ExpiringStore<UUID> store =
        new ExpiringMemoryStore<>(ExpiringPolicy.create(5, TimeUnit.SECONDS, false));

    Store<UUID> uuids = store.synchronizedStore();
    uuids.add(UUID.randomUUID());

    try {
      TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    store.invalidate();
    System.out.println(store.size());
```

## Compatibility
This library is compatible with Java 8 and above.

## License
[MIT License](http://www.opensource.org/licenses/mit-license.php)

Copyright 2017 JParams

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
