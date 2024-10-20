package ca.mooney.testblog;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@DisplayName("MyService")
public class MyServiceTest {

    @DisplayName("Given an `id` for an existing record")
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GivenIdForExistingRecord {

        @DisplayName("When `findById` is called")
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class WhenFindByIdIsCalled {

            @DisplayName("It should return the record for that `id`")
            @Test
            void thenRecordIsReturned() {
                assertTrue(true);
            }

            @DisplayName("It should record an audit event")
            @Test
            void thenAuditEventIsRecorded() {
                assertTrue(true);
            }
        }


        @DisplayName("When `deleteById` is called")
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class WhenDeleteByIdIsCalled {

            @DisplayName("It should delete the record for that `id` from the database")
            @Test
            void thenRecordIsDeleted() {
                assertTrue(true);
            }

            @DisplayName("It should `true`")
            @Test
            void thenAuditEventIsRecorded() {
                assertTrue(true);
            }
        }
    }


}
