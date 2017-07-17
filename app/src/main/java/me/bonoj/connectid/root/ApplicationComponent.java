package me.bonoj.connectid.root;

import javax.inject.Singleton;

import dagger.Component;
import me.bonoj.connectid.connections.ConnectionsActivity;
import me.bonoj.connectid.details.DetailsActivity;
import me.bonoj.connectid.edit.EditActivity;

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(ConnectidApplication application);

    void inject(ConnectionsActivity connectionsActivity);

    void inject(EditActivity editActivty);

    void inject(DetailsActivity detailsActivity);
}
