import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileMatch {

    private static final String MASTER_FILE = "oldmast.txt";
    private static final String TRANSACTION_FILE = "trans.txt";
    private static final String NEW_MASTER_FILE = "newmast.txt";
    private static final String LOG_FILE = "log.txt";

    public static void main(String[] args) {
        Map<Integer, Account> accountMap = readMasterFile(MASTER_FILE);
        List<TransactionRecord> transactions = readTransactionFile(TRANSACTION_FILE);

        // Apply each transaction to the appropriate account
        List<TransactionRecord> unmatchedTransactions = new ArrayList<>();
        for (TransactionRecord tr : transactions) {
            Account account = accountMap.get(tr.getAccountNumber());
            if (account != null) {
                account.setBalance(account.getBalance() + tr.getAmount());
            } else {
                unmatchedTransactions.add(tr);
            }
        }

        writeNewMasterFile(NEW_MASTER_FILE, accountMap);
        writeLogFile(LOG_FILE, unmatchedTransactions);
    }

    private static Map<Integer, Account> readMasterFile(String filename) {
        Map<Integer, Account> accountMap = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                int accountNumber = Integer.parseInt(parts[0]);
                String firstName = parts[1];
                String lastName = parts[2];
                double balance = Double.parseDouble(parts[3]);
                accountMap.put(accountNumber, new Account(accountNumber, firstName, lastName, balance));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accountMap;
    }

    private static List<TransactionRecord> readTransactionFile(String filename) {
        List<TransactionRecord> transactions = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                int accountNumber = Integer.parseInt(parts[0]);
                double amount = Double.parseDouble(parts[1]);
                transactions.add(new TransactionRecord(accountNumber, amount));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private static void writeNewMasterFile(String filename, Map<Integer, Account> accounts) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(filename)))) {
            for (Account account : accounts.values()) {
                writer.printf("%d %s %s %.2f%n", account.getAccountNumber(), account.getFirstName(), account.getLastName(), account.getBalance());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeLogFile(String filename, List<TransactionRecord> unmatchedTransactions) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(filename)))) {
            for (TransactionRecord tr : unmatchedTransactions) {
                writer.printf("Unmatched transaction record for account number %d%n", tr.getAccountNumber());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
