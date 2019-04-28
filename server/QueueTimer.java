package server;

import java.util.TimerTask;

public class QueueTimer extends TimerTask
{
    private Server server;

    QueueTimer(Server server)
    {
        this.server = server;
    }

    @Override
    public void run() {
        // We need to make the server stop accepting
        // clients once the timer has stopped
        server.stopAcceptingClients();
        System.out.println("Server has stopped accepting connections.");
    }
}
