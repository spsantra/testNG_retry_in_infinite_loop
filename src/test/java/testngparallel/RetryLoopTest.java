package testngparallel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import testngparallel.listeners.RetriesCount;
import testngparallel.listeners.RetryAnalyzer;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.AssertJUnit.fail;

public class RetryLoopTest {
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final AtomicInteger atomicInt = new AtomicInteger(0);
	final Object existingObject = new Object();

	@RetriesCount(3)
	@Test(retryAnalyzer = RetryAnalyzer.class, dataProvider = "dpNewObject")
	public void willStopAfter3FailuresWithDataProvider(Object newObject) {
		logger.info("Run: " + atomicInt.incrementAndGet());
		Assert.fail("Kaboom!");
	}

	@RetriesCount(3)
	@Test(retryAnalyzer = RetryAnalyzer.class, dataProvider = "dpNewSameObject")
	public void willStopAfter3Failures(Object existingObject) {
		logger.info("Run: " + atomicInt.incrementAndGet());
		Assert.fail("Kaboom!");
	}

	@DataProvider
	public Object[][] dpNewObject() {
		return new Object[][]{new Object[] {new Object()}};
	}

	@DataProvider
	public Object[][] dpNewSameObject() {
		return new Object[][]{new Object[] {existingObject}};
	}


	@Test(retryAnalyzer = RetryAnalyzer.class, dataProvider = "dpCustomObject")
	public void willNotStopAfter3FailuresCustom(Custom newObject) {
		newObject.setId(UUID.randomUUID());
		logger.info("Run: {}", atomicInt.incrementAndGet());
		fail();
	}

	@DataProvider(name = "dpCustomObject")
	public Custom[] dpIntObject() {
		return new Custom[]{new Custom()};
	}

	public static class Custom{
		UUID id;
		public void setId(UUID id) {
			this.id = id;
		}
		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Custom custom = (Custom) o;
			return Objects.equals(id, custom.id);
		}
		@Override
		public int hashCode() {
			return Objects.hashCode(id);
		}
	}
}
