package org.example;

import org.example.businessLogic.TransactionProcessor;
import org.example.network.NetworkManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(Main.class, args);

        TransactionProcessor processor = context.getBean(TransactionProcessor.class);
        NetworkManager manager = context.getBean(NetworkManager.class);

        manager.setTransactionProcessor(processor);

        // Lancement du serveur dans un thread séparé
        Thread serverThread = new Thread(manager);
        serverThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Arrêt du serveur...");
            manager.exit();
        }));
    }
}
