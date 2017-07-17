package me.bonoj.connectid.connections;

import java.util.List;

import me.bonoj.connectid.data.ConnectidConnection;

public interface ConnectionsContract {

    interface View {

        void displayConnections(List<ConnectidConnection> connections);

        void displayNoConnections();

        void displayError();
    }

    interface Presenter {

        //void setView(ConnectionsContract.View view);

        void loadConnections();

        void unsubscribe();
    }
}
