package toby.com.orderup;

import android.accounts.Account;
import android.app.Activity;
import android.os.Bundle;
import android.os.IInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.ResultStatus;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v3.employees.AccountRole;
import com.clover.sdk.v3.employees.Employee;
import com.clover.sdk.v3.employees.EmployeeConnector;


public class MainActivity extends Activity implements ServiceConnector.OnServiceConnectedListener, EmployeeConnector.OnActiveEmployeeChangedListener{

    private Account account;
    private EmployeeConnector mEmployeeConnector;
    private AccountRole mRole;
    private Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateView();
    }


    private void populateView() {
        submitButton = (Button) findViewById(R.id.submitButton);
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
                mRole = result.getRole();
            }
        });
    }

    @Override
    public void onActiveEmployeeChanged(Employee employee) {
        if (employee != null) {
            mRole = employee.getRole();
        }
    }

    @Override
    public void onServiceConnected(ServiceConnector<? extends IInterface> serviceConnector) {
    }

    @Override
    public void onServiceDisconnected(ServiceConnector<? extends IInterface> serviceConnector) {
    }

}
