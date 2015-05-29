package shioyang.java_conf.gr.jp.mytasks;

import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient googleApiClient = null;

    private boolean signInClicked = false;
    private boolean intentInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.signin_button);
        button.setOnClickListener(this);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        builder.addApi(Plus.API);
        builder.addScope(new Scope("profile"));
        googleApiClient = builder.build();
    }

    // ==================================================

    @Override
    public void onClick(View view) {
        if (!googleApiClient.isConnecting()) {
            signInClicked = true;
            googleApiClient.connect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            signInClicked = false;
        }

        intentInProgress = false;

        if (!googleApiClient.isConnected()) {
            googleApiClient.reconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        signInClicked = false;
        Toast.makeText(this, "User is connected.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!intentInProgress) {
            if (signInClicked && connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    intentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    intentInProgress = false;
                    googleApiClient.connect();
                }
            }
        }
    }

    // --------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.reconnect();
    }

    // ==================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
