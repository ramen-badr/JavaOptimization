package org.example.demo;

import java.lang.management.ManagementFactory;

public class DemoApplication {
    public static void main(String[] args) throws Exception {
        boolean wait = args.length > 0 && "wait".equalsIgnoreCase(args[0]);
        DemoService service = new DemoService();

        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        System.out.println("PID: " + pid);

        int iteration = 0;
        while (wait || iteration < 5) {
            try {
                System.out.println("Result: " + service.unstableOperation("call-" + iteration));
            } catch (Exception e) {
                System.out.println("Call failed: " + e.getMessage());
            }

            System.out.println("Stable result: " + service.stableOperation("call-" + iteration));

            try {
                service.unstableVoid(iteration);
            } catch (Exception e) {
                System.out.println("Void call failed: " + e.getMessage());
            }

            iteration++;
            Thread.sleep(1000);
        }
    }
}
