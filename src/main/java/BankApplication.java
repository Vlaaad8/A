import com.luxoft.bankapp.exceptions.ActiveAccountNotSet;
import com.luxoft.bankapp.model.Account;
import com.luxoft.bankapp.model.Client;
import com.luxoft.bankapp.service.BankReportService;
import com.luxoft.bankapp.service.Banking;
import com.luxoft.bankapp.service.BankingImpl;
import com.luxoft.bankapp.service.feed.BankFeedService;
import com.luxoft.bankapp.service.feed.BankFeedServiceImpl;
import com.luxoft.bankapp.service.storage.ClientStorage;
import com.luxoft.bankapp.service.storage.Storage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class BankApplication
{
    private static final String[] CLIENT_NAMES =
            { "Jonny Bravo", "Adam Budzinski", "Anna Smith" };

    public static void main(String[] args)
    {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("application-context.xml", "test-clients.xml");

        Banking banking = initialize(context);

        workWithExistingClients(banking);

//        bankingServiceDemo(context);
//
//        bankReportsDemo(context);
//
//        bankFeedDemo(context);
    }

    private static void bankFeedDemo(ApplicationContext context)
    {
        System.out.println("\n=== Using BankFeedService ===\n");

        String fileName = "test.feed";

        BankFeedService feedService = (BankFeedService) context.getBean("feedService");

        feedService.saveFeed(fileName);

        System.out.println("Stored Clients");
        System.out.println("==============");
        try (BufferedReader reader = new BufferedReader(new FileReader("feeds/" + fileName)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        System.out.println("\nLoading clients from file.");
        System.out.println("==========================");

        Banking banking = new BankingImpl();

        Storage<Client> newStorage = new ClientStorage();
        banking.setStorage(newStorage);

        feedService = new BankFeedServiceImpl(banking);

        feedService.loadFeed(new File(fileName));

        banking.getClients().forEach(System.out::println);
    }

    private static void bankReportsDemo(ApplicationContext context)
    {
        System.out.println("\n=== Using BankReportService ===\n");

        BankReportService reportService = (BankReportService) context.getBean("bankReport");

        System.out.println("Number of clients: " + reportService.getNumberOfBankClients());

        System.out.println("Number of accounts: " + reportService.getAccountsNumber());


        System.out.println("Bank Credit Sum: " + reportService.getBankCreditSum());
    }

    private static void bankingServiceDemo(ApplicationContext context)
    {
        System.out.println("\n=== Initialization using Banking implementation ===\n");

        Banking banking = (Banking) context.getBean("banking");

        Client anna = (Client) context.getBean("client1");
        anna = banking.addClient(anna);

        Account saving = (Account) context.getBean("savingAccount3");
        saving.deposit(1000);

        banking.updateAccount(anna, saving);

        Account checking = (Account) context.getBean("checkingAccount3");
        checking.deposit(2000);

        banking.updateAccount(anna, checking);

        banking.getAllAccounts(anna).stream().forEach(System.out::println);
    }

    private static void workWithExistingClients(Banking banking)
    {
        System.out.println("\n=======================================");
        System.out.println("\n===== Work with existing clients ======");

        Client jonny = banking.getClient(CLIENT_NAMES[0]);
        try
        {
            jonny.deposit(5_000);
        }
        catch (ActiveAccountNotSet e)
        {
            System.out.println(e.getMessage());

            jonny.setDefaultActiveAccountIfNotSet();
            jonny.deposit(5_000);
        }

        System.out.println(jonny);

        Client adam = banking.getClient(CLIENT_NAMES[1]);
        adam.setDefaultActiveAccountIfNotSet();

        adam.withdraw(1500);

        double balance = adam.getBalance();
        System.out.println("\n" + adam.getName() + ", current balance: " + balance);

        banking.transferMoney(jonny, adam, 1000);

        System.out.println("\n=======================================");
        banking.getClients().forEach(System.out::println);
    }

    /*
     * Method that creates a few clients and initializes them with sample values
     */
    public static Banking initialize(ApplicationContext context)
    {
        Banking banking = (Banking) context.getBean("banking");


        Storage<Client> storage = (Storage<Client>) context.getBean("storage");
        banking.setStorage(storage);

        Client client_1 = (Client) context.getBean("client1");

        Client client_2 = (Client) context.getBean("client2");

        banking.addClient(client_1);
        banking.addClient(client_2);

        return banking;
    }
}
