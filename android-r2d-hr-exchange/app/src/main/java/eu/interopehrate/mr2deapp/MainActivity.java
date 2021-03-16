package eu.interopehrate.mr2deapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.interopehrate.mr2d.exceptions.MR2DException;
import eu.interopehrate.mr2de.MR2DFactory;
import eu.interopehrate.mr2de.api.HealthDataBundle;
import eu.interopehrate.mr2de.api.HealthDataType;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;

public class MainActivity extends AppCompatActivity {

    private MR2D mr2d;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("d/M/yyyy");

    /*
    private Patient r2dEndpoint = new Patient();
    private String username, password;
    private DiagnosticReport getLastLaboratoryResults() {
        MR2D mr2d = MR2DFactory.create(r2dEndpoint);

        try {
            mr2d.login(username, password);

            DiagnosticReport labRes = (DiagnosticReport)mr2d.getLastRecord(
                    HealthRecordType.LABORATORY_RESULT,
                    ResponseFormat.STRUCTURED_UNCONVERTED);

            return labRes;
        } catch (MR2DException e) {
            Log.e("Democlass", "Exception with MR2D", e);
            return null;
        }
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Login Button
        Button b = findViewById(R.id.login);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            // "mario.rossi"
            // "carla.verdi"
            public void onClick(View view) {
                Spinner users = (Spinner)findViewById(R.id.users);
                Spinner countries = (Spinner)findViewById(R.id.countries);

                String selectedUser = (String)users.getSelectedItem();
                String selectedCountry = (String)countries.getSelectedItem();

                Patient p = new Patient();
                if ("fake".equalsIgnoreCase(selectedCountry))
                    p.addAddress().setCountry("FKE");
                else if ("italy".equalsIgnoreCase(selectedCountry))
                    p.addAddress().setCountry("ITA");
                else if ("belgium".equalsIgnoreCase(selectedCountry))
                    p.addAddress().setCountry("BEL");
                else if ("greece".equalsIgnoreCase(selectedCountry))
                    p.addAddress().setCountry("GRC");

                // Instantiate MR2D library
                mr2d = MR2DFactory.create(p);
                // invokes login()
               (new Login()).execute(selectedUser);
            }
        });

        // Logout button
        b = findViewById(R.id.logout);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new Logout()).execute();
            }
        });

        b = findViewById(R.id.getAllButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Switch switchPS = (Switch)findViewById(R.id.switchPS);
                Switch switchLabRes = (Switch)findViewById(R.id.switchLabRes);
                Switch switchMedImg = (Switch)findViewById(R.id.switchMedImg);
                TextView dateTxt = (TextView)findViewById(R.id.dateTxt);

                // Checks if at least one Switch has been checked
                if (!switchPS.isChecked() && !switchLabRes.isChecked() && !switchMedImg.isChecked()) {
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.getAllButton), "Choose at least one type of HealthRecord", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }

                // Checks to see if a date has been provided by the user
                Date date = null;
                try {
                    date = dateFormatter.parse(dateTxt.getText().toString());
                } catch (ParseException e) {
                    // Log.d(getClass().getName(), "No valid date to parse.", e);
                }

                // Handles selected HealthRecordType types
                List<HealthDataType> types = new ArrayList<>();
                if (switchPS.isChecked())
                    types.add(HealthDataType.PATIENT_SUMMARY);
                if (switchLabRes.isChecked())
                    types.add(HealthDataType.LABORATORY_RESULT);
                if (switchMedImg.isChecked())
                    types.add(HealthDataType.MEDICAL_IMAGE);

                // Handles selected format
                Spinner formats = (Spinner)findViewById(R.id.formats);
                ResponseFormat format = ResponseFormat.valueOf(formats.getSelectedItem().toString());

                (new GetRecords()).execute(date, format, types.toArray(new HealthDataType[types.size()]));
            }
        });
    }

    /*
     * ASYNC TASK #1: method MR2D.login()
     */
    private class Login extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            try {
                String password = "worng";
                if(objects[0].toString().equals("mario.rossi"))
                    password = "interopehrate";
                else if (objects[0].toString().equals("xavi"))
                    password = "creus";
                mr2d.login(objects[0].toString(), password);
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage(), e.getCause());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mr2d.isAuthenticated()) {
                findViewById(R.id.logout).setEnabled(true);
                findViewById(R.id.getAllButton).setEnabled(true);
                findViewById(R.id.login).setEnabled(false);
            }
        }
    }

    /*
     * ASYNC TASK #2: method MR2D.logout()
     */
    private class Logout extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            mr2d.logout();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!mr2d.isAuthenticated()) {
                findViewById(R.id.login).setEnabled(true);
                findViewById(R.id.getAllButton).setEnabled(false);
                findViewById(R.id.logout).setEnabled(false);
            }
        }
    }

    /*
     * ASYNC TASK #5: method MR2D.getRecords() for all type and all formats
     */
    private class GetRecords extends AsyncTask<Object, HealthDataBundle, HealthDataBundle> {
        private int numRecords = 0;
        private int totalRecords = 0;
        private HealthDataType currentType;
        private ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        private TextView progressLabel = (TextView) findViewById(R.id.progressLabel);

        @Override
        /*
         * Executes the getAllRecords methods, that may cause the submission
         * of an undefined number of REST invocations to the server. Invocations
         * are executed in a lazy way during iterations of results. This is the reason
         * why the iteration operation must be performed in the "doInBackground" method.
         *
         * args[0] = array of HealthRecordType
         * args[1] = instance of ResponseFormat
         * args[2] = optional Date
         */
        protected HealthDataBundle doInBackground(Object... args) {
            HealthDataBundle bundle = null;
            HealthDataType cu = null;
            try {
                // executes method getAllRecords providing the starting date
                // bundle = MainActivity.this.mr2d.getAllRecords((Date)args[0], ResponseFormat.ALL);
                bundle = MainActivity.this.mr2d.getRecords((Date)args[0],
                        (ResponseFormat) args[1],
                        (HealthDataType[]) args[2]);
                totalRecords = 0;

                for (HealthDataType t : bundle.getHealthRecordTypes()) {
                    numRecords = 0;
                    currentType = t;
//                    Log.d(getClass().getSimpleName(),"Tipo corrente: " + currentType);
                    while (bundle.hasNext(currentType)) {
                        Resource r = bundle.next(currentType);
                        // Log.d(getClass().getName(), r.getId());
                        // simulate saving resource to DB
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        numRecords++;
                        publishProgress(bundle);
                    }
                    totalRecords += numRecords;
                }
            } catch (MR2DException e) {
                Log.e(getClass().getName(), "Error while interacting with NCP", e);
            }

            return bundle;
        }

        @Override
        /*
         * Used to write the total number of records downloaded
         * at the end of the "doInBackground" method
         */
        protected void onPostExecute(HealthDataBundle bundle) {
            progressLabel.setText("Downloaded " +  totalRecords + " records.");
        }

        @Override
        protected void onPreExecute() {
            progressLabel.setText("Starting download...");
        }

        @Override
        /*
         * Used to update the progress bar. It shows the type whose download
         * is running and the number of records downloaded compared to the
         * total.
         */
        protected void onProgressUpdate(HealthDataBundle... bundles) {
            if (bundles[0] == null)
                return;

            progressLabel.setText("Downloading " + currentType + "...");
            if (numRecords == 1) // sets the max to the progress bar
                progressBar.setMax(bundles[0].getTotal(currentType));

            progressBar.setProgress(numRecords);
        }
    }

}
