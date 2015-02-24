package nl.rivium.breakdown.core;

import org.junit.Test;

public class TestStringAssertion {

    /**
     * Simple check for non-regex mismatch.
     */
    @Test
    public void containsAssertion() {
        Project p = new Project("Test project");
        TestSuite suite = new TestSuite("Test suite", p);
        TestCase testCase = new TestCase("Test case", suite);
    }

    /**
     * Tests the regex assertion stuff.
     */
    @Test
    public void regexAssertion() {
        Project p = new Project("Test project");
        TestSuite suite = new TestSuite("Test suite", p);
        TestCase testCase = new TestCase("Test case", suite);
    }

    @Test
    public void cox() {
        Project p = new Project("Test project");
        TestSuite suite = new TestSuite("Test suite", p);
        TestCase testCase = new TestCase("Test case", suite);
    }
}
