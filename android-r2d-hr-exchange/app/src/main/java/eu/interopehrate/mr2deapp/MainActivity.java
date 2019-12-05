package eu.interopehrate.mr2deapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.interopehrate.mr2de.MR2DException;
import eu.interopehrate.mr2de.MobileR2DFactory;
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

        Button b = findViewById(R.id.createButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mr2d != null)
                    return;

                // Creating a Patient from Country FKE for connceting to FAKE MR2D
                Patient p = new Patient();
                p.addAddress().setCountry("FKE");
                // p.addAddress().setCountry("ITA");
                try {
                    mr2d = MobileR2DFactory.create(p, "abc-def-ghi-lmn");
                    findViewById(R.id.getLastButton).setEnabled(true);
                    findViewById(R.id.getAllButton).setEnabled(true);
                    findViewById(R.id.getRecordButton).setEnabled(true);
                    findViewById(R.id.resIdText).setEnabled(true);
                    findViewById(R.id.createButton).setEnabled(false);
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
            if (psBundle.getEntry().size() == 0)
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
            int i = ((ProgressBar) findViewById(R.id.progressBar)).getProgress();
            // Snackbar.make(findViewById(R.id.getRecordButton), "Downloaded  " +  totalRecords +
            //        " records from NCP.", Snackbar.LENGTH_LONG).show();
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