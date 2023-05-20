import dev.jmoore.Account;
import dev.jmoore.Protocore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class Tests {
    private static Account account;

    @BeforeAll
    public static void setup() throws Exception {
        account = new Account();
    }

    @Test
    public void testLogin() {
        assertDoesNotThrow(() -> account.login());
    }

    @Test
    public void init() {
        final Protocore core = new Protocore();
        assertDoesNotThrow(() -> {
            core.init(System.out::println);
            core.getClient().connect(() -> System.out.println("Connected!"));
        });
    }
}
