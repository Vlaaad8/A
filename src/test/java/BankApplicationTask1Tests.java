import com.luxoft.bankapp.model.Client;
import com.luxoft.bankapp.service.Banking;
import com.luxoft.bankapp.service.BankingImpl;
import com.luxoft.bankapp.service.storage.ClientStorage;
import com.luxoft.bankapp.service.storage.Storage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(locations = {"classpath:application-context.xml",
        "classpath:test-clients.xml"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankApplicationTask1Tests
{
    private static final String[] CLIENT_NAMES =
            { "Jonny Bravo", "Adam Budzinski", "Anna Smith" };

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Banking banking;

    @Autowired
    private Storage storage;

    @BeforeAll
    public void init()
    {
        BankApplication.initialize(applicationContext);
    }

    @Test
    public void storageBeanConfiguration()
    {
        assertNotNull(storage, "storage bean should be configured");
        assertTrue((storage instanceof ClientStorage), "storage should be instantiated with ClientStorage class");
    }

    @Test
    public void bankingBeanConfiguration()
    {
        assertNotNull(banking, "banking bean should be configured");
        assertTrue((banking instanceof BankingImpl), "storage should be instantiated with BankingImpl class");
    }

    @Test
    public void initializationClient1()
    {
        Client client = banking.getClient(CLIENT_NAMES[0]);
        assertNotNull(client, "banking should have client with name: " + CLIENT_NAMES[0]);

        assertEquals(2, client.getAccounts().size());
    }

    @Test
    public void initializationClient2()
    {
        Client client = banking.getClient(CLIENT_NAMES[1]);
        assertNotNull(client, "banking should have client with name: " + CLIENT_NAMES[1]);

        assertEquals(1, client.getAccounts().size());
    }

}
