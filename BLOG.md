# Testing Blog

I would like to share some of my advice on writing good tests. I've often seen an overlooked aspect of testing which is
the developer's experience when running tests. Particularly the next developer's experience, whether that be a different
person or future you who has just come back to some area of the code you have not worked with in a long time. Many times
we run these tests and we can probably be confident that nothing broke but their output is incomprehensible, we aren't 
really sure what's supposed to be happening, or if it's safe to change. So let's see if we can fix that!

## Prioritizing the Test Output

The trick to making good tests is going to be to make the test output look good for human eyes. We want it to be like 
self affirming documentation of what the expected behaviours of the various units of the system are, as well as the system 
in general. This may seem obvious, but the statement I'm really trying to make here is that the test code itself is not 
good enough. It's not good enough for a couple of reasons. The first being, that it significantly slows developers down
having to read the test code and divine meaning and intentions from that. Sometimes it is nearly impossible to determine
what part of the test is consequential and what's ultimately inconsequential. The second reason is that making the test output
look good starts with statements of expectations and these statements of expectations re-affirm what the test should actually
be testing. This not only makes them much easier to understand but makes it easier for the future dev to understand what
should change in the system.

Various IDEs and other test runners usually provide some kind of test output report, this is what we're aiming to make
look good.

TODO Examples from IntelliJ, VSCode, other test runners...

The rest of this post will cover tips for how to make this output look good.

## Human Readable Test Descriptions
Depending on your test framework, this is a no brainer. NodeJS testing frameworks, RSpec, and certain flavours of Kotest'
all have a required space for you to write real, human readable sentences right into the test. Making their output quite
legible. Other method based test frameworks like JUnit and TestNG don't require this and its led to these awful conventions
of writing out descriptions of tests as the method name with mixes of camel and snake case. Eg,

```java
@Test
void givenAndIdForExistingRecord_whenDeleteByIdCalled_thenRecordIsDeletedFromDB() {
    // test code here
} 
```

This is barely human readable. It's hard to pick out the differences in test verbiage vs methods and inputs. When
viewing many of these together, they're hard to pick out from each-other. Its just hard on the eyes, and no where else
in life to we sentences that look like this so we shouldn't need to do this in our tests either.

Fortunately these test frameworks usually have a way to add a description to the test. 
For example, In JUnit, you can use the `@DisplayName` annotation to write normal sentences. Or TestNG has
`@Test(name="...some name... ")`. We should ALWAYS these kinds of annotations. And then it doesn't really
matter what the method name is.

An improved example of the test above:
```java
@DisplayName("Given an `id` for an existing record, when `deleteById` is called, then the record is deleted from the DB")
void testDeleteById1() {
    // test code here
}
```

## Structured Testing

TODO: Counterpoint to https://kentcdodds.com/blog/avoid-nesting-when-youre-testing