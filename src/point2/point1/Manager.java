package point2.point1;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manager
 */
public class Manager {
    Lock lock = new ReentrantLock();
    Condition condA = lock.newCondition();
    Condition condB = lock.newCondition();
    boolean isLocked = false;
    int waitingA = 0;
    int waitingB = 0;

    public void requestA() {
        lock.lock();
        try {
            if (isLocked) {
                waitingA++;
                condA.await();
            }
            isLocked = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void requestB() {
        lock.lock();
        try {
            if (isLocked) {
                waitingB++;
                condB.await();
            }
            isLocked = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void releaseA() {
        lock.lock();
        isLocked = false;
        if (waitingB > 0) {
            waitingB--;
            condB.signal();
        } else if (waitingA > 0) {
            waitingA--;
            condA.signal();
        }
        lock.unlock();
    }

    public void releaseB() {
        lock.lock();
        isLocked = false;
        if (waitingB > 0) {
            waitingB--;
            condB.signal();
        } else if (waitingA > 0) {
            waitingA--;
            condA.signal();
        }
        lock.unlock();
    }
}