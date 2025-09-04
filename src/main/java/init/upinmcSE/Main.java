package init.upinmcSE;

import init.upinmcSE.dao.BookDAO;
import init.upinmcSE.dao.PatronDAO;
import init.upinmcSE.model.Book;
import init.upinmcSE.model.Patron;
import init.upinmcSE.repository.jdbc.BookJdbcRepository;
import init.upinmcSE.repository.jdbc.PatronJdbcRepository;
import init.upinmcSE.service.PatronService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        PatronDAO p = new PatronJdbcRepository();
        BookDAO b = new BookJdbcRepository();

        PatronService patronService = new PatronService(p, b);

        Patron patron = new Patron(1, "PatronA", 25);
        Book book = new Book(10, "BookA", 1998, 2, 8, 2, null);

        System.out.println("=== Optimistic Lock Test ===");
        runWithThreads(() -> {
            String result = patronService.borrowBookOptimistic(patron, book);
            System.out.println(Thread.currentThread().getName() + " -> " + result);
        });

        System.out.println("\n=== Pessimistic Lock Test ===");
        runWithThreads(() -> {
            String result = patronService.borrowBookPessimistic(patron, book);
            System.out.println(Thread.currentThread().getName() + " -> " + result);
        });
    }

    private static void runWithThreads(Runnable task) throws InterruptedException {
        int THREAD_COUNT = 5;
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}