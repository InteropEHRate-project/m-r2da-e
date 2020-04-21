package eu.interopehrate.mr2deapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.interopehrate.mr2d.exceptions.MR2DException;
import eu.interopehrate.mr2de.MobileR2DFactory;
import eu.interopehrate.mr2de.api.HealthRecordBundle;
import eu.interopehrate.mr2de.api.HealthRecordType;
import eu.interopehrate.mr2de.api.MR2D;
import eu.interopehrate.mr2de.api.ResponseFormat;

public class MainActivity extends AppCompatActivity {
    private static final String MARIO_ROSSI = "Mario Rossi";
    private static final String MARIO_ROSSI_SESSION = "f70e7d7e-ad8a-478d-9e02-2499e37fb7a8";

    private static final String CARLA_VERDI = "Carla Verdi";
    private static final String CARLA_VERDI_SESSION = "7cde8fcd-dccd-47e1-ba25-e2fd96813649";

    private MR2D mr2d;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner for Patient
        Spinner patientSpinner = (Spinner)findViewById(R.id.patientSpinner);
        patientSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,
                new String[]{MARIO_ROSSI, CARLA_VERDI}));

        // Button for MR2D instantiation
        Button b = findViewById(R.id.createButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Creating a Patient for connecting to MR2D
                    Patient p = new Patient();

                    // Checks selected Patient
                    String session = MARIO_ROSSI_SESSION;
                    if (patientSpinner.getSelectedItem().equals(CARLA_VERDI))
                        session = CARLA_VERDI_SESSION;

                    // Sets patient's reference NCP
                    if (((Switch)findViewById(R.id.fakeSwitch)).isChecked())
                        p.addAddress().setCountry("FKE");
                    else
                        p.addAddress().setCountry("ITA");

                    // Creates an instance of MR2D
                    mr2d = MobileR2DFactory.create(p, session);

                    // Disable all buttons
                    findViewById(R.id.getLastButton).setEnabled(true);
                    findViewById(R.id.getAllButton).setEnabled(true);
                    findViewById(R.id.getRecordButton).setEnabled(true);
                    findViewById(R.id.resIdText).setEnabled(true);
                } catch (MR2DException e) {
                    Log.e(getClass().getName(), "Error while loading MR2D", e);
                }
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
                (new GetRecords()).execute(gc.getTime());
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
     * AsyncTask for getting Patient Summary: method MR2D.getLastRecord()
     */
    private class GetPatientSummary extends AsyncTask<Object, Void, org.hl7.fhir.r4.model.Bundle> {
        @Override
        protected org.hl7.fhir.r4.model.Bundle doInBackground(Object[] args) {
            try {
                return (org.hl7.fhir.r4.model.Bundle)MainActivity.this.mr2d.
                        getLastRecord((HealthRecordType)args[0], ResponseFormat.STRUCTURED_CONVERTED);
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
     * AsyncTask for getting a specific Resource: method MR2D.getRecord()
     */
    private class GetResourceById extends AsyncTask<String, Void, Resource> {
        @Override
        protected Resource doInBackground(String[] args) {
            try {
                return MainActivity.this.mr2d.getRecord(args[0], ResponseFormat.STRUCTURED_CONVERTED);
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
     * AsyncTask for getting a set of heterogeneous health records: method MR2D.getRecords()
     */
    private class GetRecords extends AsyncTask<Object, HealthRecordBundle, HealthRecordBundle> {
        private int numRecords = 0;
        private int totalRecords = 0;
        private HealthRecordType currentType;
        private ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        private TextView progressLabel = (TextView) findViewById(R.id.progressLabel);

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
                bundle = MainActivity.this.mr2d.getAllRecords((Date)args[0], null);

                for (HealthRecordType t : bundle.getHealthRecordTypes()) {
                    numRecords = 0;
                    currentType = t;
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
    }

}
