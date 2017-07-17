package me.bonoj.connectid.edit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import me.bonoj.connectid.R;
import me.bonoj.connectid.data.ConnectidConnection;
import me.bonoj.connectid.data.ConnectionsDataSource;
import me.bonoj.connectid.root.ConnectidApplication;

public class EditActivity extends AppCompatActivity implements EditContract.View {

    // TODO Allow user clicking outside of EditText to close the soft keyboard

    @BindView(R.id.name_et)
    EditText nameEt;

    @BindView(R.id.description_et)
    EditText descriptionEt;

    EditActivityPresenter presenter;

    @Inject
    ConnectionsDataSource connectionsDataSource;

    ConnectidConnection newConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ButterKnife.bind(this);

        ((ConnectidApplication) getApplication()).getApplicationComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter = new EditActivityPresenter(
                this, connectionsDataSource, AndroidSchedulers.mainThread());
    }

    @Override
    protected void onStop() {
        super.onStop();

        presenter.unsubscribe();
    }

    public void handleAddConnectionClicked(View view) {

        newConnection = new ConnectidConnection(
                nameEt.getText().toString(),
                descriptionEt.getText().toString());

        Log.i("MVP view", "clicked Add Connection\n" +
                newConnection.getName() + " - " +
                newConnection.getDescription());

        presenter.deliverNewConnection();


    }

    @Override
    public Single<ConnectidConnection> getNewConnection() {
        Log.i("MVP view", "getNewConnection returning " + newConnection.getName());

        return Single.fromCallable(new Callable<ConnectidConnection>() {
            @Override
            public ConnectidConnection call() throws Exception {

                System.out.println("Thread db: " + Thread.currentThread().getId());

                return newConnection;
            }
        });
    }

    @Override
    public void displaySuccess() {
        Log.i("MVP view", "displaySuccess called - sucessfully inserted into database");

        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void displayError() {
        Log.i("MVP view", "displayError called - failed to insert into database");
    }
}
