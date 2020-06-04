package eu.interopehrate.mr2deapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import eu.interopehrate.mr2d.exceptions.MR2DException;
import eu.interopehrate.mr2de.MR2DFactory;
import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;

public class MainActivity extends AppCompatActivity {

    private MR2D mr2d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button for MR2D instantiation
        Button b = findViewById(R.id.createButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Creating a Patient for connecting to MR2D

                    // Sets patient's reference NCP
                    if (((Switch)findViewById(R.id.fakeSwitch)).isChecked()) {
                        Patient p = new Patient();
                        p.addAddress().setCountry("FKE");
                        mr2d = MR2DFactory.create(p);
                    } else
                        mr2d = MR2DFactory.create(Locale.ITALY);

                    // Disable all buttons
                    findViewById(R.id.login).setEnabled(true);
                    findViewById(R.id.getLastButton).setEnabled(false);
                    findViewById(R.id.getAllButton).setEnabled(false);
                    findViewById(R.id.getRecordButton).setEnabled(false);
                    findViewById(R.id.resIdText).setEnabled(false);
                    findViewById(R.id.logout).setEnabled(false);
                } catch (MR2DException e) {
                    Log.e(getClass().getName(), "Error while loading MR2D", e);
                }
            }
        });

        b = findViewById(R.id.login);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            // "mario.rossi"
            // "carla.verdi"
            public void onClick(View view) {
                (new Login()).execute("mario.rossi");
            }
        });

        b = findViewById(R.id.logout);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new Logout()).execute();
            }
        });

        b = findViewById(R.id.getLastButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new GetPatientSummary()).execute(HealthRecordType.PATIENT_SUMMARY);
            }
        });

        b = findViewById(R.id.getAllButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GregorianCalendar gc = new GregorianCalendar(2019, Calendar.DECEMBER, 2);
                (new GetRecords()).execute();
                //(new GetRecords()).execute(gc.getTime());
            }
        });

        b = findViewById(R.id.getRecordButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resId = ((EditText)findViewById(R.id.resIdText)).getText().toString();
                if (resId != null && resId.isEmpty())
                    Snackbar.make(findViewById(R.id.getRecordButton), "Type resource id in input field", Snackbar.LENGTH_LONG)
                            .show();
                else
                    (new GetResourceById()).execute(resId);
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
                mr2d.login(objects[0].toString(), "interopehrate");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mr2d.isAuthenticated()) {
                findViewById(R.id.logout).setEnabled(true);
                findViewById(R.id.getLastButton).setEnabled(true);
                findViewById(R.id.getAllButton).setEnabled(true);
                findViewById(R.id.getRecordButton).setEnabled(true);
                findViewById(R.id.resIdText).setEnabled(true);
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
                findViewById(R.id.getLastButton).setEnabled(false);
                findViewById(R.id.getAllButton).setEnabled(false);
                findViewById(R.id.getRecordButton).setEnabled(false);
                findViewById(R.id.resIdText).setEnabled(false);
                findViewById(R.id.logout).setEnabled(false);
            }
        }
    }


    /*
     * ASYNC TASK #3: method MR2D.getLastRecord() for PATIENT_SUMMARY
     */
    private class GetPatientSummary extends AsyncTask<Object, Void, org.hl7.fhir.r4.model.Bundle> {
        @Override
        protected org.hl7.fhir.r4.model.Bundle doInBackground(Object[] args) {
            try {
                return (org.hl7.fhir.r4.model.Bundle)MainActivity.this.mr2d.
                        getLastRecord((HealthRecordType)args[0], ResponseFormat.STRUCTURED_UNCONVERTED);
            } catch (MR2DException e) {
                Log.e(getClass().getName(), "Error while interacting with NCP", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(org.hl7.fhir.r4.model.Bundle psBundle) {
            StringBuilder sb = new StringBuilder();
            if (psBundle == null || psBundle.getEntry().size() == 0)
                sb.append("No last resource found.");
            else {
                Composition ps = (Composition)psBundle.getEntryFirstRep().getResource();
                sb.append("Retrieved Patient Summary with title: ").append(ps.getTitle());
            }

            Snackbar.make(findViewById(R.id.getRecordButton), sb.toString(), Snackbar.LENGTH_LONG)
                    .show();
        }
    }


    /*
     * ASYNC TASK #4 method MR2D.getRecord() for an instance of Patient/
     */
    private class GetResourceById extends AsyncTask<String, Void, Resource> {
        @Override
        protected Resource doInBackground(String[] args) {
            try {
                return MainActivity.this.mr2d.getRecord(args[0]);
            } catch (MR2DException e) {
                Log.e(getClass().getName(), "Error while interacting with NCP", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Resource resource) {
            StringBuilder sb = new StringBuilder();
            if (resource == null)
                sb.append("No resource found for provided id.");
            else {
                sb.append("Retrieved resource of type: '")
                        .append(resource.getResourceType().getPath())
                        .append("' with id: ").append(resource.getId());
            }

            Snackbar.make(findViewById(R.id.getRecordButton), sb.toString(), Snackbar.LENGTH_LONG)
                    .show();
        }
    }


    /*
     * ASYNC TASK #5: method MR2D.getRecords() for all type and all formats
     */
    private class GetRecords extends AsyncTask<Object, HealthRecordBundle, HealthRecordBundle> {
        private int numRecords = 0;
        private int totalRecords = 0;
        private HealthRecordType currentType;
        private ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        private TextView progressLabel = (TextView) findViewById(R.id.progressLabel);

        @Override
        /*
         * Executes the getAllRecords methods, that may cause the submission
         * of an undefined number of REST invocations to the server. Invocations
         * are executed in a lazy way during iterations of results. This is the reason
         * also the oteration must be performed in the "doInBackground" method.
         */
        protected HealthRecordBundle doInBackground(Object... args) {
            HealthRecordBundle bundle = null;
            HealthRecordType cu = null;
            try {
                // executes method getAllRecords providing the starting date
                // bundle = MainActivity.this.mr2d.getAllRecords((Date)args[0], ResponseFormat.ALL);
                bundle = MainActivity.this.mr2d.getRecords(null,
                        ResponseFormat.ALL);

                for (HealthRecordType t : bundle.getHealthRecordTypes()) {
                    numRecords = 0;
                    currentType = t;
                    Log.d(getClass().getSimpleName(),"Tipo corrente: " + currentType);
                    while (bundle.hasNext(currentType)) {
                        Resource r = bundle.next(currentType);
                        // Log.d(getClass().getName(), r.getId());
                        // simulate saving resource to DB
                        try {
                            Thread.sleep(10);
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
        protected void onPostExecute(HealthRecordBundle bundle) {
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
        protected void onProgressUpdate(HealthRecordBundle... bundles) {
            if (bundles[0] == null)
                return;

            progressLabel.setText("Downloading " + currentType + "...");
            if (numRecords == 1) // sets the max to the progress bar
                progressBar.setMax(bundles[0].getTotal(currentType));

            progressBar.setProgress(numRecords);
        }
    }

}
