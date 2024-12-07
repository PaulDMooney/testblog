# Enhancing the Developer Experience of Testing Part 2

In my last post, I talked about writing tests with natural language descriptions, and leveraging structured testing
to provide organized and comprehensible output. Here I will expand more on these concepts. 

## Structured Testing Part 2
Structured testing is a nesting of contexts, until eventually terminating in one or more tests. In NodeJS test framework, a context is a `describe` function. In JUnit a context is the test class itself or a `@Nested` inner class. Contexts, just like 
tests, can also be described (or named) with natural language and can appear in the test output's tree structure.
Here are some of the ways we can use this contextual test structure for well described tests:

### Given-When-Then Pattern
Borrowing from [Behaviour Driven Development (BDD)](https://martinfowler.com/bliki/GivenWhenThen.html), one of the ways we can structure our tests is 
to use the Given-When-Then pattern. In this pattern we nest the `Given`s, `When`s, and `Then`s in a hierarchical layout
where the `Given`s and `When`s are contexts and `Then`s are the tests (in most popular testing frameworks). 

The `Given` context's clause describes some kind of preconditions. I like to include inputs here as well because 
it gives purpose to the `Given` clause even when the thing being tested does not require preconditions. I will 
talk more on this later, but some people may prefer to not include inputs here.

The `When` context's clause describes the execution. It can also be used to describe inputs if you prefer. Again, more
on this later.

The `Then` clause for tests should describe our expectation. This fits nicely with the "One assert per test" rule.
Sometimes, from one execution we have multiple expectations. For example, if we were testing a REST endpoint then we
might expect that the response body looks a particular way, but also separately we expect the response code to be a 
particular value. These are two conceptually different expectations to assert from the same single execution 
with all the same preconditions. And there could be more -  maybe there were certain response headers expected, too. 
Breaking our expectations up into multiple `Then` clauses/test lets us describe each expectation separately.

*Note*: It's common to see synonymous conventions to these like "Arrange-Act-Assert", or the use of "it should ..." for tests
instead of "Then...". This is all fine, as long as consistency is maintained.

There are generally at least two more kinds of contexts in this pattern that we will see:

1. The `And` context. This is used for providing additional preconditions or inputs to compliment the `Given` or `When`
contexts.

2. The class or file under test context. This is the highest level context and just describes the class or file containing
the units to be tested.
It could also just be a name for a group of tests in a particular test file. This comes implicitly in some test frameworks and may default to the name of the class or file containing the tests.

Here's an example all of this in action using JUnit:

```java
@DisplayName("REST Endpoint tests")
class RestEndpointTest {

    @DisplayName("Given an `id` for an existing record")
    @Nested
    class GivenValidId {

        @DisplayName("When calling the `GET /api/record/{id}` endpoint")
        @Nested
        class WhenCallingRecordEndpoint {

            @DisplayName("Then it should return the record for that `id`")
            @Test
            void testRecordReturned() {
                // test code here
            }

            @DisplayName("Then it should return a 200 response code")
            @Test
            void test200Response() {
                // test code here
            }

            // ... other tests
        }

        @DisplayName("And the `Accept` header is `application/xml`")
        @Nested
        class AndAcceptHeaderIsXml {

            @DisplayName("When calling the `GET /api/record/{id}` endpoint")
            @Nested
            class WhenCallingRecordEndpoint {

                @DisplayName("Then it should return the record for that `id` in XML format")
                @Test
                void testRecordReturnedInXml() {
                    // test code here
                }
            }
        }
    }
    
    @DisplayName("Given an `id` which does not correspond to an existing record")
    @Nested
    class GivenInvalidId {
        @DisplayName("When calling the `GET /api/record/{id}` endpoint")
        @Nested
        class WhenCallingRecordEndpoint {
                
            @DisplayName("Then it should return a 404 response code")
            @Test
            void test404Response() {
                // test code here
            }
        }
    }
}
```

### UnitUnderTest-Given-Then Pattern
It can be helpful to group all contexts and tests by the unit being tested. 
From an organizational standpoint, this is where the Given-When-Then pattern needs an alteration 
because, in its current form, it lacks a way to organize all tests for a particular unit.
This can be difficult from a code organization standpoint, and since test order is usually randomized, the tests
for a particular unit become scattered amongst the contexts and tests for other sibling units.

To combat this, I like to organize my tests in a UnitUnderTest-Given-Then pattern. "UnitUnderTest" is the
particular unit being tested, whether that be a method, function, REST endpoint, or other [unit](https://en.wikipedia.org/wiki/Unit_testing#Unit).
Our structured test hierarchy would look like this:

- ClassUnderTest
  - Method One Under Test
    - Given some arguments and preconditions
      - It should return an expected result
    - Given some other arguments and preconditions
      - It should return a different expected result 
  - Method Two Under Test
    - Given ...
      - It should ...

Here's an example of this pattern in action using JUnit. Notice how the test scenarios for a particular unit 
(in this case units are methods) are grouped together under a context for that unit:

```java
@DisplayName("PersonService")
public class PersonServiceTest {

    @DisplayName("findById")
    @Nested
    class FindById {

        @DisplayName("Given an `id` for an existing record")
        @Nested
        class GivenIdForExistingRecord {

            @DisplayName("It should return an `Optional` containing the record for that `id`")
            @Test
            void thenRecordIsReturned() {
                // test code here
            }
        }

        @DisplayName("Given an `id` which does not correspond to an existing record")
        @Nested
        class GivenIdForNoExistingRecords {

            @DisplayName("It should return an empty Optional")
            @Test
            void thenNullIsReturned() {
                // test code here
            }
        }
    }

    @DisplayName("deleteById")
    @Nested
    class DeleteById {

        @DisplayName("Given an `id` for an existing record")
        @Nested
        class GivenIdForExistingRecord {

            @DisplayName("It should delete the record for that `id`")
            @Test
            void thenRecordIsDeleted() {
                // test code here
            }
        }
    }
}
```

*Note*: This does put a bit of a demand on the reader to understand the convention going on here, as any sentence
that can be inferred from the hierarchy starts deeper into the hierarchy than it does in the "Given-When-Then" pattern.
I believe this is a not a difficult expectation for readers to pick up on this pattern, especially given the 
organizational advantage it provides.

#### Alternative: UnitUnderTest-Given-When-Then Pattern
So far, I've omitted the `When` context because I state the inputs in the `Given` context. This is a personal preference
and if you believe `Given`s should only state preconditions, then you can include a `When` context to describe the inputs.
Both of these do not need to exist - sometimes you don't have preconditions, and sometimes you don't have inputs.

Example with both `Given` and `When` contexts, because there is both a preconditon and an input:
* PersonService
  * deleteById
    * Given there is an existing record
      * When called with the `id` for the existing record
        * It should delete the record for that `id`

Example with just `Given` context, because there is only a precondition and no input:
* PersonService
  * deleteAll
    * Given multiple records exist
      * It should delete all records

Example with just `When` context, because there is only an input and no precondition:
* PersonService
  * getFullName
    * When called with a `firstName` and `lastName`
      * It should return the `firstName` and `lastName` concatenated with a space in-between.

## Some Dos and Don'ts
We've gone over the general framework for writing good tests, at least from a structure and output standpoint.
Let's look at some more specific tidbits as Dos and Don'ts.

### Do: Provide contexts which describe preconditions, inputs, and their relationship to each-other

Look at a test structure like
* PersonService
  * deleteById
    * Given an existing record
      * It should delete the record

This sounds wrong. We know from the description that a record exists and a record is deleted but was it the same record? 
How is it deciding which record to delete? Details are missing. 
Clearly there is an input which is not being described here
and then there's no relationship described between the input and anything else. The improved version of this test would be:
* PersonService
  * deleteById
    * Given an `id` for an existing record
      * It should delete the record for that `id`

Now it's easy to understand that what's being deleted and why.

### Don't: Put explicit values in the description

Unless that exact value has relevance in the code. For example, if we had
* PersonService
  * deleteById
    * Given an `id` of `1234` for an existing record
      * It should delete the record for that `id`

Reading this, it seems like `1234` is important to the unit being tested. As if, there is an explicit check in the code
for `1234`, and if we had a test Like "Given an `id` of `ABCDE` for an existing record..." then we might expect a 
completely different behaviour. In reality though `1234` is probably not important so it should be left out of the
description to avoid confusing the reader.
Another example of this might be where the both inputs and expectation are described with explicit values:
* MathUtil
  * hardToFullyDescribeMethodName
    * When called with a `radianValue` of `1`
      * It should return the `2.3974`

Now I'm left wondering what happens if the `radianValue` is `0` or `2`? What does `hardToFullyDescribeMethodName` actually do?
It's better to leave out explicit values and convey the preconditions, inputs, and expectations in more general terms:
* MathUtil
  * hardToFullyDescribeMethodName
    * When called with a `radianValue`
      * It should return the sum of tan and sin of the `radianValue`

It's fine to use explicit values in the actual test code, just not in the description.
There are some exceptions to this rule, like when the value is some kind of enumeration, boolean, or actual special case value.
Or when writing [parameterized tests](https://www.baeldung.com/parameterized-tests-junit-5). Then it makes sense to state
those values in the description.

### Don't: Leave out important information

I've seen tests like
* Should return a Person record with no bio

This leaves me asking so many question. The author might as well called this "test 1". I'm going to be generous and say
that maybe they did try to describe the preconditions and inputs:
* PersonService
  * findById
    * Given an `id` for an existing record
      * It should return a Person record with no bio

Maybe this is fine, because that's the general expectation is that this particular method leaves the bio field empty. But
if the reality is that there's a condition where the bio field should be empty then what is it? It's not described as it
should be:
* PersonService
  * findById
    * Given an `id` for an existing record
      * And the record's `showBio` field is `false`
        * It should return a Person record with no bio

### Do: Avoid overly vague terms
It's not uncommon to run into tests that have some overly vague terminology and say things like "it should return the appropriate value". 
What is the "appropriate value"?
While we want to be general in our descriptions of our preconditions, inputs, and expectations, we still don't want to
leave the reader guessing. This "appropriate value" gives no clue about what the unit being tested is supposed to do, just that whatever it is,
it does it.

This can be a bit of a challenge, as you also don't want to be overly specific or too verbose. You need to find the 
phrasing that is just descriptive enough to give the reader a good idea of what's going on. 
Ideally we should fully understand the test from reading its output, but there are times that's not possible or reasonable. So instead we need to find phrasing 
that invites the reader to dig into the test code if they need more specifics. It should be rare that they need to do this.

For example, with this test:
* PersonService
  * findById
    * Given an `id` for an existing record
      * It should return a Person record for that `id` with all fields populated from the database

This is a bit vague, I'm not really sure what "all fields populated from the database" exactly entails. Like is the
field `lastName` populated from the database column `SURNAME`? I don't know. I'll have to dig into the test code to find out.
While that's inconvenient, if there's 20 fields in a `Person` record, leaving it kind of vague makes it much easier to both
write and read these tests rather than describing how every single field gets populated.

We also want to be careful here not to mask any behaviours. For example, if the `Person` returned has an age, which is
computed based on the birthdate stored in the database, then that should have its own test somewhere and not just hidden
amongst the assertions in the "It should return a Person record for that `id` with all fields populated from the database"
test.

## Conclusion
This post has all been about the organization
and verbiage around tests. This is important because it not only helps the reader of these tests, but it can help
organize your thoughts about how to actually write the test code. This advice is all derived from my own experiences; 
I hope you find it useful.