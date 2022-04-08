package com.queues.howto;

// Include the following imports to use queue APIs
// Include the following imports to use queue APIs
import com.azure.core.util.*;
import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;

import javax.xml.crypto.Data;
import java.time.Duration;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // Define the connection-string with your values
        final String connectStr =
                "DefaultEndpointsProtocol=https;" +
                        "AccountName= myAccountName" +
                        "AccountKey= myaccountKey";

        // création de la queue
        createQueue(connectStr, "producteur");

        createQueue(connectStr, "consommateur1");
        createQueue(connectStr, "consommateur2");



        //Processus producteur on  créé 100 messages
        new Thread(() -> {
            int i = 0;
            while (i < 100) {
                addQueueMessage(connectStr, "producteur", new Date().toString());
                i++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // premier processus consommateur
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (getQueueLength(connectStr, "producteur") > 0) {
                System.out.println("reste dans la queue producteur" + getQueueLength(connectStr, "producteur"));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dequeueMessageAndDoTreatment(connectStr, "producteur", "1");

            }
            System.out.println("plus de message dans la queue producteur");
        }).start();

        // deuxième processus consommateur
        new Thread(() -> {

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (getQueueLength(connectStr, "producteur") > 0) {
                System.out.println("reste dans la queue producteur" + getQueueLength(connectStr, "producteur"));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dequeueMessageAndDoTreatment(connectStr, "producteur", "2");

            }
            System.out.println("plus de message dans la queue producteur");
        }).start();

    }

    public static String createQueue(String connectStr, String QueueName)
    {
        try
        {
            // Create a unique name for the queue
            String queueName = QueueName;

            System.out.println("Creating queue: " + queueName);

            // Instantiate a QueueClient which will be
            // used to create and manipulate the queue
            QueueClient queue = new QueueClientBuilder()
                    .connectionString(connectStr)
                    .queueName(queueName)
                    .buildClient();

            // Create the queue
            queue.create();
            return queue.getQueueName();
        }
        catch (QueueStorageException e)
        {
            // Output the exception message and stack trace
            System.out.println("Error code: " + e.getErrorCode() + "Message: " + e.getMessage());
            return null;
        }

    }

    public static void addQueueMessage (String connectStr, String queueName, String messageText)
    {
        try
        {
            // Instantiate a QueueClient which will be
            // used to create and manipulate the queue
            QueueClient queueClient = new QueueClientBuilder()
                    .connectionString(connectStr)
                    .queueName(queueName)
                    .buildClient();

            System.out.println("Adding message to the queue: " + messageText);

            // Add a message to the queue
            queueClient.sendMessage(messageText);
        }
        catch (QueueStorageException e)
        {
            // Output the exception message and stack trace
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void dequeueMessageAndDoTreatment(String connectStr, String queueName, String processNumber)
    {
        try
        {
            // Instantiate a QueueClient which will be
            // used to create and manipulate the queue
            QueueClient queueClient = new QueueClientBuilder()
                    .connectionString(connectStr)
                    .queueName(queueName)
                    .buildClient();

            // Get the first queue message
            QueueMessageItem message = queueClient.receiveMessage();

            // Check for a specific string
            if (null != message)
            {
                System.out.println("Dequeing message: " + message.getMessageText());

                addQueueMessage(connectStr, "consommateur"+ processNumber, "message traité par le processus " + processNumber);

                // Delete the message
                queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
            }
            else
            {
                System.out.println("No visible messages in queue");
            }
        }
        catch (QueueStorageException e)
        {
            // Output the exception message and stack trace
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static long getQueueLength(String connectStr, String queueName)
    {
        try
        {
            // Instantiate a QueueClient which will be
            // used to create and manipulate the queue
            QueueClient queueClient = new QueueClientBuilder()
                    .connectionString(connectStr)
                    .queueName(queueName)
                    .buildClient();

            QueueProperties properties = queueClient.getProperties();
            long messageCount = properties.getApproximateMessagesCount();

            return messageCount;
        }
        catch (QueueStorageException e)
        {
            // Output the exception message and stack trace
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
}


