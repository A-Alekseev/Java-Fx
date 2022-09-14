package ru.gb.javafxapplication.level3.lesson4.homework;

public class L3L4main {
    public static void main(String[] args) {
        new L3L4main().startWriting();
    }

    private static final int COUNT = 5;
    String lastLetter = "C";

    void startWriting() {
        new Thread(()-> print("A", "C")).start();
        new Thread(()-> print("B", "A")).start();
        new Thread(()-> print("C", "B")).start();
    }

    Thread createPrintingThread(String letter, String expectedLastLetter){
        return new Thread(()-> print(letter, expectedLastLetter));
    }

    synchronized void print(String letter, String expectedLastLetter){
        try {
            for (int i = 0; i < COUNT; i++) {
                while (true) {
                    if (expectedLastLetter.equals(lastLetter)) {
                        break;
                    }
                    wait();
                }

                System.out.print(letter);
                lastLetter = letter;
                notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
