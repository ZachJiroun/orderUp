package toby.com.orderup;

import android.accounts.Account;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.ResultStatus;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v3.employees.AccountRole;
import com.clover.sdk.v3.employees.Employee;
import com.clover.sdk.v3.employees.EmployeeConnector;
import com.microsoft.windowsazure.mobileservices.*;

import java.net.MalformedURLException;


public class MainActivity extends Activity implements ServiceConnector.OnServiceConnectedListener {

    private Account account;
    private EmployeeConnector mEmployeeConnector;
    private Button submitButton;
    private MobileServiceClient mClient;
    private EditText editText;
    private TextView eId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateView();
    }

    private void populateView() {

        submitButton = (Button) findViewById(R.id.submitButton);

        editText = (EditText) findViewById(R.id.cellNumberEditText);

        eId = (TextView) findViewById(R.id.eId);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mClient = new MobileServiceClient(
                            "https://orderup2.azure-mobile.net/",
                            "BXfpcXcechDNSfdcEErRaVRpSKQXbu53",
                            getApplicationContext()
                    );
                    Item item = new Item();
                    item.Text = editText.getText().toString();
                   // Toast.makeText(getApplicationContext(), employeeId.toString(), Toast.LENGTH_LONG).show();
                    item.employeeid = "cheese";
                    mClient.getTable(Item.class).insert(item, new TableOperationCallback<Item>() {
                        public void onCompleted(Item entity, Exception exception, ServiceFilterResponse response) {
                            if (exception == null) {
                                // Insert succeeded
                                System.out.println("yey");
                            } else {
                                // Insert failed
                                System.out.println("you suck");
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();

        // Retrieve Clover Account
        if (account == null) {
            account = CloverAccount.getAccount(this);

            if (account == null){
                Toast.makeText(this, getString(R.string.no_account), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        // Connect to the EmployeeConnector
        connect();

        // Get the employee's role
        getEmployee();
    }

    @Override
    protected void onPause(){
        disconnect();
        super.onPause();
    }

    private void connect(){
        disconnect();
        if (account != null){
            mEmployeeConnector = new EmployeeConnector(this, account, this);
            mEmployeeConnector.connect();
        }
    }

    private void disconnect() {
        if (mEmployeeConnector != null) {
            mEmployeeConnector.disconnect();
            mEmployeeConnector = null;
        }
    }

    private void getEmployee(){
        mEmployeeConnector.getEmployee(new EmployeeConnector.EmployeeCallback<Employee>() {
            @Override
            public void onServiceSuccess(Employee result, ResultStatus status) {
                super.onServiceSuccess(result, status);
                //eId.setText(result.getId());
                Toast.makeText(getApplication(), result.getId(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onServiceConnected(ServiceConnector<? extends IInterface> serviceConnector) {
    }

    @Override
    public void onServiceDisconnected(ServiceConnector<? extends IInterface> serviceConnector) {
    }

}
