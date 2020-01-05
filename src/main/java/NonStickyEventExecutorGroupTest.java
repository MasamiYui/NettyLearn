
import io.netty.util.concurrent.*;
import org.junit.Test;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

public class NonStickyEventExecutorGroupTest {


    @Test
    public void NonStickyEventExecutorGroupTest() throws InterruptedException {
        int taskNum = 100;
        EventExecutorGroup group = new UnorderedThreadPoolEventExecutor(32, new ThreadPoolExecutor.DiscardPolicy());//线程池大小32
        NonStickyEventExecutorGroup nonStickyGroup = new NonStickyEventExecutorGroup(group, 32);//最大可执行线程
        final CountDownLatch latch = new CountDownLatch(taskNum);
        try {
            for (int i = 0; i < 100; i++) {
                //模拟多个任务执行
                System.out.println("已发送第"+i+"个任务");
                Runnable myTask = new MyTask(i, latch);
                nonStickyGroup.submit(myTask);
            }
            System.out.println("所有任务提交完毕");
            latch.await();
            System.out.println("所有任务执行完毕");
        }finally {
            nonStickyGroup.shutdownGracefully();
        }

    }

    public class MyTask implements Runnable{

        private int taskNum;
        private CountDownLatch latch;

        MyTask(int taskNum, CountDownLatch latch){
            this.taskNum = taskNum;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                System.out.println("开始执行:"+taskNum+"号任务"+",Thread Name："+Thread.currentThread().getName());
                Thread.sleep(1000*getRandomNum(5,30));//模拟耗时任务
                System.out.println("执行完成:"+taskNum+"号任务"+",Thread Name："+Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                latch.countDown();
            }
        }
    }

    public int getRandomNum(int min, int max){
        return new Random().nextInt(max-min)+min;
    }
}