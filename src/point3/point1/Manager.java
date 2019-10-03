package point3.point1;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Manager {
    private int state;
    private final Lock lock = new ReentrantLock();
    private final Condition condRequestA = lock.newCondition();
    private final Condition condGetA = lock.newCondition();
    private final Condition condReleaseA = lock.newCondition();
    private final Condition condRequestB = lock.newCondition();
    private final Condition condGetB = lock.newCondition();
    private final Condition condReleaseB = lock.newCondition();
    private final int[] nextRequestA = { 1, -1, -1, 5, 6, -1, -1, -1, -1, -1, -1, 12, 13, -1, -1, -1, -1 };
    private final int[] nextGetA = { -1, 3, -1, -1, -1, -1, -1, -1, -1, 3, -1, -1, -1, -1, -1, 3, 5 };
    private final int[] nextReleaseA = { -1, -1, -1, 0, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
    private final int[] nextRequestB = { 2, -1, -1, 4, -1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
    private final int[] nextGetB = { -1, -1, 11, -1, -1, -1, -1, -1, 11, -1, 12, -1, -1, -1, -1, -1, -1 };
    private final int[] nextReleaseB = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 15, 16, -1, -1, -1 };

    public void requestA() {
        lock.lock();
        try {
            while (nextRequestA[state] == -1)
                condRequestA.await();
            state = nextRequestA[state];
            while (nextGetA[state] == -1)
                condGetA.await();
            state = nextGetA[state];
            if (nextRequestA[state] != -1)
                condRequestA.signalAll();
            if (nextGetA[state] != -1)
                condGetA.signalAll();
            if (nextReleaseA[state] != -1)
                condReleaseA.signalAll();
            if (nextRequestB[state] != -1)
                condRequestB.signalAll();
            if (nextGetB[state] != -1)
                condGetB.signalAll();
            if (nextReleaseB[state] != -1)
                condReleaseB.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void releaseA() {
        lock.lock();
        try {
            while (nextReleaseA[state] == -1)
                condReleaseA.await();
            state = nextReleaseA[state];
            if (nextRequestA[state] != -1)
                condRequestA.signalAll();
            if (nextGetA[state] != -1)
                condGetA.signalAll();
            if (nextReleaseA[state] != -1)
                condReleaseA.signalAll();
            if (nextRequestB[state] != -1)
                condRequestB.signalAll();
            if (nextGetB[state] != -1)
                condGetB.signalAll();
            if (nextReleaseB[state] != -1)
                condReleaseB.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void requestB() {
        lock.lock();
        try {
            while (nextRequestB[state] == -1)
                condRequestB.await();
            state = nextRequestB[state];
            while (nextGetB[state] == -1)
                condGetB.await();
            state = nextGetB[state];

            if (nextRequestA[state] != -1)
                condRequestA.signalAll();
            if (nextGetA[state] != -1)
                condGetA.signalAll();
            if (nextReleaseA[state] != -1)
                condReleaseA.signalAll();
            if (nextRequestB[state] != -1)
                condRequestB.signalAll();
            if (nextGetB[state] != -1)
                condGetB.signalAll();
            if (nextReleaseB[state] != -1)
                condReleaseB.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void releaseB() {
        lock.lock();
        try {
            while (nextReleaseB[state] == -1)
                condReleaseB.await();
            state = nextReleaseB[state];
            if (nextRequestA[state] != -1)
                condRequestA.signalAll();
            if (nextGetA[state] != -1)
                condGetA.signalAll();
            if (nextReleaseA[state] != -1)
                condReleaseA.signalAll();
            if (nextRequestB[state] != -1)
                condRequestB.signalAll();
            if (nextGetB[state] != -1)
                condGetB.signalAll();
            if (nextReleaseB[state] != -1)
                condReleaseB.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}