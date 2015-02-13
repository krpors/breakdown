package nl.rivium.breakdown.core;

public interface ExecutionListener {
    void assertionFailed(Project p, TestSuite testSuite, TestCase testCase, TestStep testStep);
}
