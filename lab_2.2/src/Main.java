import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

class Processor {
    private Queue<Process> queue = new LinkedList<>();
    private boolean processor1Busy = false;
    private boolean processor2Busy = false;
    private int maxQueueLength = 0;
    private int discardedCount1 = 0;
    private int interruptedCount2 = 0;

    public void generateProcess(int threadId) {
        Process process = new Process(threadId);
        synchronized (this) {
            if (threadId == 1) {
                if (processor1Busy) {
                    if (processor2Busy) {
                        discardedCount1++;
                        System.out.println("Process 1 discarded.");
                    } else {
                        processor2Busy = true;
                        System.out.println("Process 1 sent to Processor 2.");
                    }
                } else {
                    processor1Busy = true;
                    System.out.println("Process 1 started on Processor 1.");
                }
            } else if (threadId == 2) {
                if (processor2Busy) {
                    if (processor1Busy) {
                        interruptedCount2++;
                        System.out.println("Process 2 interrupted.");
                    } else {
                        processor1Busy = true;
                        System.out.println("Process 2 sent to Processor 1.");
                    }
                } else {
                    processor2Busy = true;
                    System.out.println("Process 2 started on Processor 2.");
                }
            }

            if (queue.size() > maxQueueLength) {
                maxQueueLength = queue.size();
            }
        }
    }

    public void processCompleted(int threadId) {
        synchronized (this) {
            if (threadId == 1) {
                processor1Busy = false;
            } else if (threadId == 2) {
                processor2Busy = false;
            }

            if (!queue.isEmpty()) {
                Process nextProcess = queue.poll();
                generateProcess(nextProcess.getThreadId());
            }
        }
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public int getDiscardedCount1() {
        return discardedCount1;
    }

    public int getInterruptedCount2() {
        return interruptedCount2;
    }
}

class Process {
    private int threadId;

    public Process(int threadId) {
        this.threadId = threadId;
    }

    public int getThreadId() {
        return threadId;
    }
}

public class Main {
    public static void main(String[] args) {
        Processor processor = new Processor();
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            int threadId = random.nextInt(2) + 1;
            processor.generateProcess(threadId);
        }

        System.out.println("Max Queue Length: " + processor.getMaxQueueLength());
        System.out.println("Percentage of Discarded Processes for Thread 1: " + (processor.getDiscardedCount1() / 100.0) * 100 + "%");
        System.out.println("Percentage of Interrupted Processes for Thread 2: " + (processor.getInterruptedCount2() / 100.0) * 100 + "%");
    }
}
