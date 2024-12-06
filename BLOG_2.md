# Testing Blog 2
In my last post I talked about the writing tests with natural language descriptions, and leveraging structured testing
to provide organized and comprehensible output. Here I will expand more on some of these concepts.

## Structured Testing Part 2
Structured testing is a nesting of contexts until eventually terminating in one or more tests. Contexts, just like the
tests, can also be described (or named) with natural language and can appear in the test output in a tree structure.
Here are some of the ways we can use this contextual test structure for well described tests:

### Given-When-Then Pattern
Borrowing from [BDD](https://martinfowler.com/bliki/GivenWhenThen.html), one of the ways we can structure our tests is 
to use the Given-When-Then pattern. In this pattern we nest the `Given`s, `When`s, and `Then`s in a hierarchical layout
where the `Given`s and `When`s are contexts and `Then`s are the tests (in most popular testing frameworks). 

The `Given` context's clause describes some kind of preconditions. I also like to include inputs here as well because 
it still gives purpose to the `Given` clause when the thing being tested does not require preconditions. I will 
talk more on this later, but generally most might not prefer to do that.

The `When` context's clause describes the execution.

The `Then` clause for tests should describe our expectation. This fits nicely with the "One assert per test" rule.
Sometimes from one execution we expect multiple expectations. For example, if we were testing a REST endpoint then we
might expect the response body looks a particular way, but also separately we expect the response code to be a 
particular value. These are two conceptually different expectations to assert from the same single execution 
with all the same preconditions. And there could be more, maybe there were certain response headers expected too. 
Breaking our expectations up into multiple `Then` clauses/test lets us describe each expectation separately.

*Note*: It's common to see synonymous conventions to these like "Arrange-Act-Assert", or the use of "it should ..." for tests
instead of "Then...". This is all fine if, as long as consistency is maintained.

There are generally at least two more kinds of contexts in this pattern we will see:

1. The `And` context. This is used for providing additional preconditions or inputs to compliment the `Given` or `When`
contexts.

2. The class or file under test context. This is the highest level context and just describes the class or file containing
the units to be tested.
 Or is just a name for a group of tests in a particular test file. This comes implicitly in some test frameworks and may
default to the name of the class or file containing the tests.

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
    
    @DisplayName("Given an `id` for a no existing records")
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
A lot of the time we're testing methods of a class, or functions in a file, or REST Endpoints, or similar. 
It helps to group tests by the unit/thing being tested. 
From an organizational standpoint, this is where the Given-When-Then pattern needs an alteration, depending on how you use it. 
Usually the "When" part is something like "when method XYZ is called" which, when there's multiple "Given" contexts, 
it can be repetitive, especially when nothing differentiates your "When" clauses from each other. Which can happen when
the inputs are described in your "Given" clauses. A bigger problem is the tests aren't organized by unit. Since test runners usually randomize the order 
of the tests, so all the tests for a particular unit
become scattered amongst the tests for other sibling units. And the test code structure can be disorganized as well.

To combat this, I like to organize my tests in a UnitUnderTest-Given-Then pattern. Where "UnitUnderTest" is usually the
particular method, function, or endpoint being tested. 
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

Here's an example of this pattern in action using JUnit. Notice how the test scenarios for a particular unit (in this case units are methods) are
grouped together under a context for that unit:

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

        @DisplayName("Given an `id` for no existing records")
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

*Note*: This does put a bit of a demand on the reader to understand the convention going on here. As any sentence
that can be inferred from the hierarchy starts deeper into the hierarchy than it does in the "Given-When-Then" pattern.
I believe this is a not a difficult expectation for readers to pick up on this pattern, especially for the 
organizational advantage it provides.

#### Alternative: UnitUnderTest-Given-When-Then Context
So far, I've omitted the `When` context because I state the inputs in the `Given` context. This is a personal preference
and if you believe `Given`s should only state preconditions, then you can include a `When` context to describe the inputs.
Both of these do not need to exist, sometimes you don't have preconditions, or sometimes you don't have inputs.

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

### Do: Provide contexts which describe preconditions, inputs, and their relationship to each-other

Look at a test like
* PersonService
  * deleteById
    * Given an existing record
      * It should delete the record

This sounds wrong. We know from the description that a record exists and a record is deleted but was it the same record? How
is it deciding which record to delete? Details are missing. Clearly there is an input which is not being described here
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
While we want to be general in our descriptions of our preconditions, inputs, and expectations, we still don't want the
leave the reader guessing. This "appropriate value" gives no clue about what the unit being tested is supposed to do, just that whatever it is,
it does it.

## Conclusion
That's a wrap on Part 2 on my compilation of tips for writing good tests. So far this has all been about the organization
and verbiage around the tests. Which is important because it not only helps the reader of these tests, but it can help
organize your thoughts about how to actually write the test code. Maybe later we will get into more advice around
what to test and how to test it. This is a compilation of my advice derived from my own experiences. I hope you find it useful.
There may be much more advice out there so keep an eye out.