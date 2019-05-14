package ru.ifmo.rain.nemchunovich.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class HelloUDPServer implements HelloServer {
    private static final int TRIES = 10;
    private final BlockingQueue<RunningClient> runningServers;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid amount of arguments provided");
            System.out.println("Usage: [port] [threads]");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            HelloUDPServer server = new HelloUDPServer();
            int threads = Integer.parseInt(args[1]);
            server.start(port, threads);

        } catch (NumberFormatException e) {
            System.out.println("Usage: [port] [threads]");
        }
    }

    private class RunningClient {
        private final DatagramSocket socket;
        private final ExecutorService workerThreads;
        private final int buffSize;
        private final int threads;
        private final int port;

        RunningClient(int port, int threads) throws SocketException {
            this.threads = threads;
            this.port = port;
            this.socket = new DatagramSocket(port);
            this.workerThreads = Executors.newFixedThreadPool(threads);
            this.buffSize = socket.getReceiveBufferSize();
        }

        void start() {
            for (int i = 0; i < threads; i++) {
                workerThreads.submit(() -> {
                    DatagramPacket pack = new DatagramPacket(new byte[buffSize], buffSize);
                    while (!Thread.interrupted()) {
                        try {
                            socket.receive(pack);
                            String requestString = new String(pack.getData(), pack.getOffset(), pack.getLength(), StandardCharsets.UTF_8);
                            String responseString = "Hello, " + requestString;
                            pack.setData(responseString.getBytes());
                            for (int ii = 0; ii < TRIES; ++ii) {
                                try {
                                    socket.send(pack);
                                    break;
                                } catch (IOException e) {
                                    System.err.println("Unable to send response: " + e.getMessage() + ". Retrying");
                                }
                            }

                        } catch (IOException e) {
                            System.err.println("Failed to receive packet: " + e.getMessage());
                        }

                    }
                });
            }
            System.out.println("Started listening on " + this.port + " in " + this.threads + " threads");
        }

        void close() {
            workerThreads.shutdownNow();
            socket.close();
        }
    }

    public HelloUDPServer() {
        runningServers = new LinkedBlockingQueue<>();
    }

    public void start(int port, int threads) {
        try {
            RunningClient server = new RunningClient(port, threads);
            runningServers.add(server);
            server.start();
        } catch (SocketException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }

    }

    @Override
    public void close() {
        runningServers.forEach(RunningClient::close);
    }
}
