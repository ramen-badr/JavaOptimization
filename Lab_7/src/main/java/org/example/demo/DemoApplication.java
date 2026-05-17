package org.example.demo;

import java.lang.management.ManagementFactory;

public class DemoApplication {
    public static void main(String[] args) throws Exception {
        boolean wait = args.length > 0 && "wait".equalsIgnoreCase(args[0]);
        DemoService service = new DemoService();

        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        System.out.println("\nPID: " + pid);

        int iteration = 1;
        while (wait || iteration <= 5) {
            System.out.println("\nIteration: " + iteration);

            try {
                System.out.println("Result of unstable operation: " + service.unstableOperation("call-" + iteration));
            } catch (Exception e) {
                System.out.println("Call of unstable operation failed: " + e.getMessage());
            }

            System.out.println("Result of stable operation: " + service.stableOperation("call-" + iteration));

            try {
                service.unstableVoid(iteration);
            } catch (Exception e) {
                System.out.println("Call of unstable void failed: " + e.getMessage());
            }

            iteration++;
            Thread.sleep(1000);
        }
    }
}
