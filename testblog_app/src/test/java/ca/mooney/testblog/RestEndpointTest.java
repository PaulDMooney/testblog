package ca.mooney.testblog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("REST Endpoint tests")
class RestEndpointTest {

    @DisplayName("Given an `id` for an existing record")
    @Nested
    class GivenValidId {

        @DisplayName("When calling the `GET /api/record/{id}` endpoint")
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

        @DisplayName("And the `Accept` header is `application/xml`")
        @Nested
        class AndAcceptHeaderIsXml {

            @DisplayName("When calling the `/api/record/{id}` endpoint")
            @Nested
            class WhenCallingRecordEndpoint {

                @DisplayName("It should return the record for that `id` in XML format")
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

                @DisplayName("It should return a 404 response code")
                @Test
                void test404Response() {
                    // test code here
                }
        }
    }
}
