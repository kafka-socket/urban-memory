package urban.memory

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

object TokenSpec : Spek({
    describe("A Token") {
        val valid = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwidXNlcl91aWQiOiJKb2huIERvZSIsImlhdCI6MTUxNjIzOTAyMn0.UhOiwlNwWRy9I_uTVQ4dyUSt8MHtT9uJiMJiJjVH87M"
        val invalid = "qqq"
        val unsigned = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwidXNlcl91aWQiOiJKb2huIERvZSIsImlhdCI6MTUxNjIzOTAyMn0.-kgXnneRFJ8aNnApbqSJo1JgQUifGFd1mz8aX0y6Zms"

        describe("isOk") {
            it("is ok") {
                assertTrue(Token(valid).isOk())
                assertFalse(Token(invalid).isOk())
                assertFalse(Token(unsigned).isOk())
            }
        }

        describe("user") {
            it("returns a user") {
                assertEquals("John Doe", Token(valid).user())
                assertNull(Token(invalid).user())
            }
        }
    }
})
