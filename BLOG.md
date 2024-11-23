# Testing Blog

I would like to share some of my advice on writing good tests. I've often seen an overlooked aspect of testing is
what the developer's experience is when running tests. Particularly the next developer's experience, whether that be a different
person or future you who has just come back to some area of the code you have not worked with in a long time. Or maybe
it's current you practicing [TDD](https://martinfowler.com/bliki/TestDrivenDevelopment.html) and you need organize your 
thoughts around what and how to test. Many times
we run these tests and we can probably be confident that nothing broke but their output is incomprehensible! We aren't 
really sure what's supposed to be happening, or if it's safe to change. So let's see if we can fix that!

## Prioritizing the Test Output

One of the pillars of writing good tests is to make the test output look good for human eyes. We want it to be like 
self affirming documentation of what the expected behaviours of the various units of the system are, as well as the 
behaviours of the system in general. 
This may seem obvious, but the statement I'm really trying to make here is that the test code itself is not 
good enough to explain what's happening. It's not good enough for a couple of reasons. The first being, that it 
significantly slows developers down
having to read the test code. The second is that the test code does not express meaning and intentions. 
Sometimes it is nearly impossible to determine
what part of the test is consequential and what is ultimately inconsequential: Is the test asserting that value Y is equal
to X because that's exactly what was meant to be tested? Or are the values themselves less important and the assertion 
that they're equal just signalling that some underlying expected behaviour is happening?

So how do we know if we have good output? We just look at it. Various IDEs and other test runners usually provide some 
kind of test output report, this is what we're aiming to make look good.

![IntelliJ Test Output](./screenshots/intellij_testrun_output.jpg "IntelliJ Test Run Output")

![VSCode Test Explorer Output](./screenshots/vscode_testexplorer_output.jpg "VSCode Java Test Explorer Output")

The rest of this post will cover tips for how to make this output look good.

## Human Readable Test Descriptions
Depending on your test framework, this is may be a no brainer. NodeJS testing frameworks, RSpec, and certain flavours of Kotest
all have a required space for you to write real, human readable sentences right into the test. Making their output quite
legible. 

```javascript
describe('MyClassUnderTest', () => {
  it('Given an `id` for an existing record, when `deleteById` is called, then the record is deleted from the DB', () => {
    // test code here
  });
});
```
Results in an nice output like this:
![Nice JavaScript Output](./screenshots/javascript_nice_output.jpg "Nice JavaScript Output")

Other method based test frameworks like JUnit and TestNG don't require this and its led to these awful 
[conventions](https://enterprisecraftsmanship.com/posts/you-naming-tests-wrong/)
of writing out descriptions of tests as the method name with mixes of camel and snake case. Eg,

```java
@Test
void givenAndIdForExistingRecord_whenDeleteByIdCalled_thenRecordIsDeletedFromDB() {
    // test code here
} 
```

This is barely human readable, we can already tell before looking at the output. 
It's hard to pick out the methods and inputs involved from the rest of the test verbiage. When
viewing many of these test methods together, they're hard to pick out from each-other. It's just an eye sore. 
And nowhere else in life do we need to read sentences that look like this, so we shouldn't need to read tests like this either.

Fortunately these test frameworks usually have a way to add a description to the test. 
For example, In JUnit, you can use the `@DisplayName` annotation to write normal sentences. Or TestNG has
`@Test(name="...some name... ")`. We should ALWAYS use these kinds of annotations. And then it doesn't really
matter what the method name is.

An improved example of the test above:
```java
@Test
@DisplayName("Given an `id` for an existing record, when `deleteById` is called, then the record is deleted from the DB")
void testDeleteById1() {
    // test code here
}
```

This will give us a nice human readable output in the test report:

TODO: Example screenshot of the test report

## Structured Testing

Another tool that can help make our test output look good is to take advantage of the test frameworks ability to group
tests within a nesting of "contexts". This helps us create reusability in the test code, but more importantly in the test
sentence structure. Imagine we have these 3 [flat] tests for a REST endpoint:

- Given a valid `id`, when calling the `/api/record/{id}` endpoint, it should return the record for that `id`
- Given a valid `id`, when calling the `/api/record/{id}` endpoint, it should return a 200 response code
- Given a valid `id`, and the `Accept` header is `application/xml`, when calling the `/api/record/{id}` endpoint, 
it should return the record for that `id` in XML format

In this example there are two problems: We're repeating the same setup over and over again, and the test descriptions are
becoming verbose run-on sentences. Structured testing can help solve this problem. We can group these tests into nested contexts
like so:

- Given a valid `id`
  - When calling the `/api/record/{id}` endpoint
    - It should return the record for that `id`
    - It should return a 200 response code
  - And the `Accept` header is `application/xml`
    - When calling the `/api/record/{id}` endpoint 
      - It should return the record for that `id` in XML format

TODO: Show code and test output

This definitely looks cleaner. One could argue that this deviates from a natural language sentence structure, or that
there's extra cognitive load in having to piece together the full declaration of a single test from its setup, to execution, to expectation.
This might be true but in the first example with the flattened test structure we also have an arguably poor sentence structure given how
long they are, and there may be just as much mental load grouping the tests with similar contexts together. So in that
sense it's a wash. But from a visualization, and organization standpoint providing a structure like this is the clear winner.

In JUnit this can be accomplished using the `@Nested` annotation. In 

```java
@DisplayName("REST Endpoint tests")
class RestEndpointTest {

  @DisplayName("Given a valid `id`")
  @Nested
  class GivenValidId {

    @DisplayName("When calling the `/api/record/{id}` endpoint")
    @Nested
    class WhenCallingRecordEndpoint {

      @DisplayName("It should return the record for that `id`")
      @Test
      void testRecordReturned() {
        // test code here
      }

      @DisplayName("It should return a 200 response code")
      @Test
      void test200Response() {
        // test code here
      }
      
      // ... other tests
    }
  }
}
```

TODO: Counterpoint to https://kentcdodds.com/blog/avoid-nesting-when-youre-testing