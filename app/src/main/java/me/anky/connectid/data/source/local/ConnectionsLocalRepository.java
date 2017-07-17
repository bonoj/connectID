package me.anky.connectid.data.source.local;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import me.anky.connectid.data.ConnectidConnection;
import me.anky.connectid.data.ConnectionsDataSource;

import static android.content.ContentProviderOperation.newInsert;

public class ConnectionsLocalRepository implements
        ConnectionsDataSource {

    private final List<ConnectidConnection> connections = new ArrayList<>();

    private Context context;

    public ConnectionsLocalRepository(Context context) {
        this.context = context;

        // FOR DEBUG: clear the database when app launches
        // deleteAllEntries();

        // FOR DEBUG: populate the database with data if it is empty
        initDatabase();


    }

    @Override
    public Single<List<ConnectidConnection>> getConnections() {
        prepareConnectionsList();

        return Single.fromCallable(new Callable<List<ConnectidConnection>>() {
            @Override
            public List<ConnectidConnection> call() throws Exception {

                System.out.println("Thread db: " + Thread.currentThread().getId());

                Log.i("MVP model", "getConnections returned " + connections.size() + " connections");

                sortConnectionsAlphabetically();

                return connections;
            }
        });
    }

    private void initDatabase() {

        Cursor cursor = getAllEntries();
        if (cursor == null || cursor.getCount() == 0) {
            insertDummyData();
            Log.i("MVP model", "initialized database");
        }
    }

    private void prepareConnectionsList() {
        connections.clear();

        int databaseId;
        String name;
        String description;

        Cursor cursor = getAllEntries();
        if (cursor != null && cursor.getCount() != 0) {

            while (cursor.moveToNext()) {
                databaseId = cursor.getInt(cursor.getColumnIndex(ConnectidColumns._ID));
                name = cursor.getString(cursor.getColumnIndex(ConnectidColumns.NAME));
                description = cursor.getString(cursor.getColumnIndex(ConnectidColumns.DESCRIPTION));
                connections.add(new ConnectidConnection(databaseId, name, description));
            }
        }
    }

    private void sortConnectionsAlphabetically() {
        Collections.sort(connections, new Comparator<ConnectidConnection>() {
            @Override
            public int compare(ConnectidConnection o1, ConnectidConnection o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }

    private Cursor getAllEntries() {
        return context.getContentResolver().query(
                ConnectidProvider.Connections.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    private void deleteAllEntries() {
        context.getContentResolver().delete(
                ConnectidProvider.Connections.CONTENT_URI,
                null,
                null);
    }

    private void insertDummyData() {

        List<ConnectidConnection> dummyConnections = new ArrayList<>();

        dummyConnections.add(new ConnectidConnection("Aragorn", "you have my sword"));
        dummyConnections.add(new ConnectidConnection("Legolas", "and you have my bow"));
        dummyConnections.add(new ConnectidConnection("Gimli", "and my axe!"));
        dummyConnections.add(new ConnectidConnection("Gandalf", "fly, you fools!"));
        dummyConnections.add(new ConnectidConnection("Bilbo", "misses his ring"));
        dummyConnections.add(new ConnectidConnection("Frodo", "misses his finger"));
        dummyConnections.add(new ConnectidConnection("Sam", "ringbearerbearer"));
        dummyConnections.add(new ConnectidConnection("Boromir", "one does not simply"));
        dummyConnections.add(new ConnectidConnection("Saruman", "don't trust him"));
        dummyConnections.add(new ConnectidConnection("Gollum", "naughty"));
        dummyConnections.add(new ConnectidConnection("Smeagol", "nice"));
        dummyConnections.add(new ConnectidConnection("Elrond", "Agent Smith"));
        dummyConnections.add(new ConnectidConnection("Arwen", "Agent Smith's daughter"));

        // TODO allow attempted duplicate entry to retrieve existing data and merge new data
        dummyConnections.add(new ConnectidConnection("Legolas", "one legolas, two legoli"));

        ArrayList<ContentProviderOperation> batchOperations =
                new ArrayList<>(dummyConnections.size());

        for (ConnectidConnection connection : dummyConnections) {

            ContentProviderOperation.Builder builder = newInsert(
                    ConnectidProvider.Connections.CONTENT_URI);
            builder.withValue(ConnectidColumns.NAME, connection.getName());
            builder.withValue(ConnectidColumns.DESCRIPTION, connection.getDescription());
            batchOperations.add(builder.build());
        }

        try {
            context.getContentResolver().applyBatch(ConnectidProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {

            // TODO Add some sort of Analytics for reporting.
            Log.e("DATABASE_TEST", "Error applying batch insert", e);
        }
    }

    @Override
    public int insertNewConnection(ConnectidConnection newConnection) {

        Log.i("MVP model", "insertNewConnection inserting " + newConnection.getName());

        ContentValues contentValues = new ContentValues();
        contentValues.put(ConnectidColumns.NAME, newConnection.getName());
        contentValues.put(ConnectidColumns.DESCRIPTION, newConnection.getDescription());

        Uri uri = context.getContentResolver().insert(ConnectidProvider.Connections.CONTENT_URI, contentValues);

        Log.i("MVP model", "insertNewConnection inserted uri " + uri.toString());

        return generateResultCode(uri);
    }

    @Override
    public int deleteConnection(int databaseId) {

        Uri uri = ConnectidProvider.Connections.withId(databaseId);

        Log.i("MVP model", "deleteConnection deleted " + uri.toString());

        return context.getContentResolver().delete(uri, null, null);
    }

    private int generateResultCode(Uri uri) {

        int lastPathSegment = Integer.parseInt(uri.getLastPathSegment());
        if (lastPathSegment == -1) {
            return -1;
        } else {
            return 1;
        }
    }
}

