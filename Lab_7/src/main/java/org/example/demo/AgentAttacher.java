package org.example.demo;

import com.sun.tools.attach.VirtualMachine;

public class AgentAttacher {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: AgentAttacher <pid> <agent-jar>");
            return;
        }

        VirtualMachine vm = VirtualMachine.attach(args[0]);
        try {
            vm.loadAgent(args[1]);
        } finally {
            vm.detach();
        }

        System.out.println("Agent loaded.");
    }
}
