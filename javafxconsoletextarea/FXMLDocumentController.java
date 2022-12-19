package javafxconsoletextarea;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

class Producer implements Runnable {

    @FXML

    private List<Integer> sharedQueue;
    private int maxSize = 4; 
    private int productionSize = 30; 
    int producerNo;

    public Producer(List<Integer> sharedQueue, int producerNo) {
        this.sharedQueue = sharedQueue;
        this.producerNo = producerNo;
    }

    @Override
    public void run() {
        for (int i = 1; i <= productionSize; i++) { 
            try {
                produce(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void produce(int i) throws InterruptedException {

        synchronized (sharedQueue) {
           
            while (sharedQueue.size() == maxSize) {
                System.out.println(Thread.currentThread().getName() + ", Queue is full, producerThread is waiting for "
                        + "consumerThread to consume, sharedQueue's size= " + maxSize);
                sharedQueue.wait();
            }

            
            int producedItem = (productionSize * producerNo) + i;

            System.out.println(Thread.currentThread().getName() + " Produced : " + producedItem);
            sharedQueue.add(producedItem);
            Thread.sleep((long) (Math.random() * 1000));
            sharedQueue.notify();
        }
    }

}



class Consumer implements Runnable {
    private List<Integer> sharedQueue;

    public Consumer(List<Integer> sharedQueue) {
        this.sharedQueue = sharedQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                consume();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void consume() throws InterruptedException {

        synchronized (sharedQueue) {
           
            while (sharedQueue.size() == 0) {
                System.out.println(Thread.currentThread().getName() + ", Queue is empty, consumerThread is waiting for "
                        + "producerThread to produce, sharedQueue's size= " + sharedQueue.size());
                sharedQueue.wait();
            }

            Thread.sleep((long) (Math.random() * 2000));
            System.out.println(Thread.currentThread().getName() + " CONSUMED : " + sharedQueue.remove(0));
            sharedQueue.notify();
        }
    }

}

public class FXMLDocumentController implements Initializable {

    @FXML
    private TextArea textAreaUI;
    static TextArea staticTxtArea;

    @FXML

    private void handleButtonAction(ActionEvent event) {

        List<Integer> sharedQueue = new LinkedList<Integer>(); // Creating shared object

        Producer producer0 = new Producer(sharedQueue, 0);
        Consumer consumer0 = new Consumer(sharedQueue);

        Thread producerThread0 = new Thread(producer0, "Chef 1");
        Thread consumerThread0 = new Thread(consumer0, "Customer 1");
        producerThread0.start();
        consumerThread0.start();

        Producer producer1 = new Producer(sharedQueue, 1);
        Consumer consumer1 = new Consumer(sharedQueue);

        Thread producerThread1 = new Thread(producer1, "Chef 2");
        Thread consumerThread1 = new Thread(consumer1, "Customer 2");
        producerThread1.start();
        consumerThread1.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        staticTxtArea = textAreaUI;
    }

}
