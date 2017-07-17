package me.bonoj.connectid.root;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.bonoj.connectid.data.ConnectionsDataSource;
import me.bonoj.connectid.data.source.local.ConnectionsLocalRepository;

@Module
public class ApplicationModule {

    private final ConnectidApplication application;

    private ConnectionsLocalRepository connectionsLocalRepository;

    public ApplicationModule(ConnectidApplication application){
        this.application = application;

        connectionsLocalRepository = new ConnectionsLocalRepository(application);
    }

    @Provides
    @Singleton
    public Context provideContext(){
        return application;
    }

    @Provides
    @Singleton
    ConnectionsDataSource provideConnectionsRepository(Context context) {
        //return new ConnectionsLocalRepository(context);
        return connectionsLocalRepository;
    }
}
