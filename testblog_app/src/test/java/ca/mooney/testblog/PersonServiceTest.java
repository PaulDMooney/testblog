package ca.mooney.testblog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
