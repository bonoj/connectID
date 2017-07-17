package me.bonoj.connectid.edit;

import io.reactivex.Single;
import me.bonoj.connectid.data.ConnectidConnection;

public interface EditContract {

    interface View {

        Single<ConnectidConnection> getNewConnection();

        void displayError();

        void displaySuccess();
    }

    interface Presenter {

        void deliverNewConnection();

        void unsubscribe();
    }
}
