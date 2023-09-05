import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class NomeClasseTest {
	@Test
	public void testDivide() {
		Lola cut = new Lola();

		int result = cut.divide(10, 2);
		assertEquals(5, result);

		assertThrows(ArithmeticException.class, () -> {
			cut.divide(10, 0);
		});
	}
}