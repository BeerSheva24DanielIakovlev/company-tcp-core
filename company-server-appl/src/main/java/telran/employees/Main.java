package telran.employees;

import telran.net.*;

public class Main {
    private static final int PORT = 6000;

    public static void main(String[] args) {
        Company company = new CompanyImpl();

        TcpServer server = new TcpServer(new CompanyProtocol(company), PORT);
        server.run();
    }
}
