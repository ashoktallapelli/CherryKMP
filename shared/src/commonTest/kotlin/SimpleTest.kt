import kotlin.test.*

class SimpleTest {

    @Test
    fun testBasicFunctionality() {
        val result = 2 + 2
        assertEquals(4, result)
    }

    @Test
    fun testStringOperations() {
        val text = "CherryKMP"
        assertTrue(text.contains("Cherry"))
    }

    @Test
    fun testBooleanLogic() {
        val isTestingWorking = true
        assertTrue(isTestingWorking)
    }
}