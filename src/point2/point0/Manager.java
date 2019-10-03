package point2.point0;

import point1.FairLock;
import point1.FairLock.Condition;

public class Manager {

    private boolean isLocked = false;
    private FairLock lock = new FairLock();
    private Condition condRequestA = lock.newCondition();
    private Condition condRequestB = lock.newCondition();
    private int waitingA = 0;
    private int waitingB = 0;

    Manager() {
    }

    public void requestA() throws InterruptedException {
        lock.lock();
        try {
            if (isLocked) {
                waitingA++;
                condRequestA.await();
            }
            isLocked = true;
        } finally {
            lock.unlock();
        }
    }

    public void requestB() throws InterruptedException {
        lock.lock();
        try {
            if (isLocked) {
                waitingB++;
                condRequestB.await();
            }
            isLocked = true;
        } finally {
            lock.unlock();
        }
    }

    public void releaseA() throws InterruptedException {
        lock.lock();
        try {
            isLocked = false;
            if (waitingB > 0) {
                waitingB--;
                condRequestB.signal();
            } else if (waitingA > 0) {
                waitingA--;
                condRequestA.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void releaseB() throws InterruptedException {
        lock.lock();
        try {
            isLocked = false;
            if (waitingB > 0) {
                waitingB--;
                condRequestB.signal();
            } else if (waitingA > 0) {
                waitingA--;
                condRequestA.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}