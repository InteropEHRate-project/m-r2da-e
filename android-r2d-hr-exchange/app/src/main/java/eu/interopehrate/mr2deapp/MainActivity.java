package eu.interopehrate.mr2deapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;

import eu.interopehrate.mr2da.MR2DAFactory;
import eu.interopehrate.mr2da.api.AsynchronousMR2DA;
import eu.interopehrate.protocols.common.FHIRResourceCategory;

public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("d/M/yyyy");
    private AsynchronousMR2DA mr2da;
    private String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdHRyaWJ1dGVzIjoiZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKU1V6STFOaUo5LmV5SmtZWFJoTXlJNklseHlYRzVjY2x4dVhISmNibHh5WEc0OElVUlBRMVJaVUVVZ2FIUnRiRDVjY2x4dVhISmNibHh5WEc1Y2NseHVQR2gwYld3Z2JHRnVaejFjSW1WdVhDSS1YSEpjYmx4eVhHNDhhR1ZoWkQ1Y2NseHVJQ0FnSUZ4eVhHNWNjbHh1UEcxbGRHRWdZMmhoY25ObGREMWNJblYwWmkwNFhDSS1YSEpjYmp4dFpYUmhJR2gwZEhBdFpYRjFhWFk5WENKWUxWVkJMVU52YlhCaGRHbGliR1ZjSWlCamIyNTBaVzUwUFZ3aVNVVTlaV1JuWlZ3aVBseHlYRzQ4YldWMFlTQnVZVzFsUFZ3aWRtbGxkM0J2Y25SY0lpQmpiMjUwWlc1MFBWd2lkMmxrZEdnOVpHVjJhV05sTFhkcFpIUm9MQ0JwYm1sMGFXRnNMWE5qWVd4bFBURmNJajVjY2x4dVBHMWxkR0VnYm1GdFpUMWNJbVJsYzJOeWFYQjBhVzl1WENJZ1kyOXVkR1Z1ZEQxY0lrTkZSaUJsU1VRZ0xTQmxTVVJCVTF3aVBseHlYRzQ4YldWMFlTQnVZVzFsUFZ3aVlYVjBhRzl5WENJZ1kyOXVkR1Z1ZEQxY0lrTkZSaUJsU1VRZ0xTQmxTVVJCVTF3aVBseHlYRzQ4YkdsdWF5QnlaV3c5WENKemRIbHNaWE5vWldWMFhDSWdhSEpsWmoxY0luSmxjMjkxY21ObGN5OXphMmx1TUM5amMzTXZZbTl2ZEhOMGNtRndMbTFwYmk1amMzTmNJajVjY2x4dVBHeHBibXNnY21Wc1BWd2ljM1I1YkdWemFHVmxkRndpSUdoeVpXWTlYQ0p5WlhOdmRYSmpaWE12YzJ0cGJqQXZZM056TDJOMWMzUnZiUzVqYzNOY0lqNWNjbHh1UEd4cGJtc2djbVZzUFZ3aWMzUjViR1Z6YUdWbGRGd2lJR2h5WldZOVhDSnlaWE52ZFhKalpYTXZjMnRwYmpBdlkzTnpMMkYzWlhOdmJXVXRZbTl2ZEhOMGNtRndMV05vWldOclltOTRMbU56YzF3aUlDOC1YSEpjYmx4eVhHNDhiR2x1YXlCeVpXdzlYQ0poY0hCc1pTMTBiM1ZqYUMxcFkyOXVYQ0lnYzJsNlpYTTlYQ0kxTjNnMU4xd2lJR2h5WldZOVhDSnlaWE52ZFhKalpYTXZjMnRwYmpBdmFXMW5MMkZ3Y0d4bExYUnZkV05vTFdsamIyNHROVGQ0TlRjdWNHNW5YQ0ktWEhKY2JqeHNhVzVySUhKbGJEMWNJbUZ3Y0d4bExYUnZkV05vTFdsamIyNWNJaUJ6YVhwbGN6MWNJall3ZURZd1hDSWdhSEpsWmoxY0luSmxjMjkxY21ObGN5OXphMmx1TUM5cGJXY3ZZWEJ3YkdVdGRHOTFZMmd0YVdOdmJpMDJNSGcyTUM1d2JtZGNJajVjY2x4dVBHeHBibXNnY21Wc1BWd2lZWEJ3YkdVdGRHOTFZMmd0YVdOdmJsd2lJSE5wZW1WelBWd2lOeko0TnpKY0lpQm9jbVZtUFZ3aWNtVnpiM1Z5WTJWekwzTnJhVzR3TDJsdFp5OWhjSEJzWlMxMGIzVmphQzFwWTI5dUxUY3llRGN5TG5CdVoxd2lQbHh5WEc0OGJHbHVheUJ5Wld3OVhDSmhjSEJzWlMxMGIzVmphQzFwWTI5dVhDSWdjMmw2WlhNOVhDSTNObmczTmx3aUlHaHlaV1k5WENKeVpYTnZkWEpqWlhNdmMydHBiakF2YVcxbkwyRndjR3hsTFhSdmRXTm9MV2xqYjI0dE56WjROell1Y0c1blhDSS1YSEpjYmp4c2FXNXJJSEpsYkQxY0ltRndjR3hsTFhSdmRXTm9MV2xqYjI1Y0lpQnphWHBsY3oxY0lqRXhOSGd4TVRSY0lpQm9jbVZtUFZ3aWNtVnpiM1Z5WTJWekwzTnJhVzR3TDJsdFp5OWhjSEJzWlMxMGIzVmphQzFwWTI5dUxURXhOSGd4TVRRdWNHNW5YQ0ktWEhKY2JqeHNhVzVySUhKbGJEMWNJbUZ3Y0d4bExYUnZkV05vTFdsamIyNWNJaUJ6YVhwbGN6MWNJakV5TUhneE1qQmNJaUJvY21WbVBWd2ljbVZ6YjNWeVkyVnpMM05yYVc0d0wybHRaeTloY0hCc1pTMTBiM1ZqYUMxcFkyOXVMVEV5TUhneE1qQXVjRzVuWENJLVhISmNianhzYVc1cklISmxiRDFjSW1Gd2NHeGxMWFJ2ZFdOb0xXbGpiMjVjSWlCemFYcGxjejFjSWpFME5IZ3hORFJjSWlCb2NtVm1QVndpY21WemIzVnlZMlZ6TDNOcmFXNHdMMmx0Wnk5aGNIQnNaUzEwYjNWamFDMXBZMjl1TFRFME5IZ3hORFF1Y0c1blhDSS1YSEpjYmp4c2FXNXJJSEpsYkQxY0ltRndjR3hsTFhSdmRXTm9MV2xqYjI1Y0lpQnphWHBsY3oxY0lqRTFNbmd4TlRKY0lpQm9jbVZtUFZ3aWNtVnpiM1Z5WTJWekwzTnJhVzR3TDJsdFp5OWhjSEJzWlMxMGIzVmphQzFwWTI5dUxURTFNbmd4TlRJdWNHNW5YQ0ktWEhKY2JqeHNhVzVySUhKbGJEMWNJbUZ3Y0d4bExYUnZkV05vTFdsamIyNWNJaUJ6YVhwbGN6MWNJakU0TUhneE9EQmNJaUJvY21WbVBWd2ljbVZ6YjNWeVkyVnpMM05yYVc0d0wybHRaeTloY0hCc1pTMTBiM1ZqYUMxcFkyOXVMVEU0TUhneE9EQXVjRzVuWENJLVhISmNianhzYVc1cklISmxiRDFjSW1samIyNWNJaUIwZVhCbFBWd2lhVzFoWjJVdmNHNW5YQ0lnYUhKbFpqMWNJbkpsYzI5MWNtTmxjeTl6YTJsdU1DOXBiV2N2Wm1GMmFXTnZiaTB6TW5nek1pNXdibWRjSWlCemFYcGxjejFjSWpNeWVETXlYQ0ktWEhKY2JqeHNhVzVySUhKbGJEMWNJbWxqYjI1Y0lpQjBlWEJsUFZ3aWFXMWhaMlV2Y0c1blhDSWdhSEpsWmoxY0luSmxjMjkxY21ObGN5OXphMmx1TUM5cGJXY3ZZVzVrY205cFpDMWphSEp2YldVdE1Ua3llREU1TWk1d2JtZGNJaUJ6YVhwbGN6MWNJakU1TW5neE9USmNJajVjY2x4dVBHeHBibXNnY21Wc1BWd2lhV052Ymx3aUlIUjVjR1U5WENKcGJXRm5aUzl3Ym1kY0lpQm9jbVZtUFZ3aWNtVnpiM1Z5WTJWekwzTnJhVzR3TDJsdFp5OW1ZWFpwWTI5dUxUazJlRGsyTG5CdVoxd2lJSE5wZW1WelBWd2lPVFo0T1RaY0lqNWNjbHh1UEd4cGJtc2djbVZzUFZ3aWFXTnZibHdpSUhSNWNHVTlYQ0pwYldGblpTOXdibWRjSWlCb2NtVm1QVndpY21WemIzVnlZMlZ6TDNOcmFXNHdMMmx0Wnk5bVlYWnBZMjl1TFRFMmVERTJMbkJ1WjF3aUlITnBlbVZ6UFZ3aU1UWjRNVFpjSWo1Y2NseHVQR3hwYm1zZ2NtVnNQVndpYldGdWFXWmxjM1JjSWlCb2NtVm1QWEpsYzI5MWNtTmxjeTl6YTJsdU1DOXBiV2N2YldGdWFXWmxjM1F1YW5OdmJsd2lQbHh5WEc1Y2NseHVQRzFsZEdFZ2FIUjBjQzFsY1hWcGRqMG5jSEpoWjIxaEp5QmpiMjUwWlc1MFBTZHVieTFqWVdOb1pTY3ZQbHh5WEc0OGJXVjBZU0JvZEhSd0xXVnhkV2wyUFNkallXTm9aUzFqYjI1MGNtOXNKeUJqYjI1MFpXNTBQU2R1YnkxallXTm9aU3dnYm04dGMzUnZjbVVzSUcxMWMzUXRjbVYyWVd4cFpHRjBaU2N2UGx4eVhHNDhiV1YwWVNCb2RIUndMV1Z4ZFdsMlBWd2lSWGh3YVhKbGMxd2lJR052Ym5SbGJuUTlYQ0l0TVZ3aUx6NWNjbHh1UEcxbGRHRWdhSFIwY0MxbGNYVnBkajFjSWtOdmJuUmxiblF0Vkhsd1pWd2lJR052Ym5SbGJuUTlYQ0owWlhoMEwyaDBiV3c3SUdOb1lYSnpaWFE5VlZSR0xUaGNJaTgtWEhKY2JseHlYRzQ4YldWMFlTQnVZVzFsUFZ3aWJYTmhjSEJzYVdOaGRHbHZiaTFVYVd4bFEyOXNiM0pjSWlCamIyNTBaVzUwUFZ3aUkyWm1ZelF3WkZ3aVBseHlYRzQ4YldWMFlTQnVZVzFsUFZ3aWJYTmhjSEJzYVdOaGRHbHZiaTFVYVd4bFNXMWhaMlZjSWlCamIyNTBaVzUwUFZ3aWNtVnpiM1Z5WTJWekwzTnJhVzR3TDJsdFp5OXRjM1JwYkdVdE1UUTBlREUwTkM1d2JtZGNJajVjY2x4dVBHMWxkR0VnYm1GdFpUMWNJblJvWlcxbExXTnZiRzl5WENJZ1kyOXVkR1Z1ZEQxY0lpTm1abVptWm1aY0lqNWNjbHh1UENFdExTQklWRTFNTlNCemFHbHRJR0Z1WkNCU1pYTndiMjVrTG1weklHWnZjaUJKUlRnZ2MzVndjRzl5ZENCdlppQklWRTFNTlNCbGJHVnRaVzUwY3lCaGJtUWdiV1ZrYVdFZ2NYVmxjbWxsY3lBdExUNWNjbHh1UENFdExWdHBaaUJzZENCSlJTQTVYVDVjY2x4dVBITmpjbWx3ZENCemNtTTlYQ0pvZEhSd2N6b3ZMMjl6Y3k1dFlYaGpaRzR1WTI5dEwyaDBiV3cxYzJocGRpOHpMamN1TWk5b2RHMXNOWE5vYVhZdWJXbHVMbXB6WENJLVBDOXpZM0pwY0hRLVhISmNianh6WTNKcGNIUWdjM0pqUFZ3aWFIUjBjSE02THk5dmMzTXViV0Y0WTJSdUxtTnZiUzl5WlhOd2IyNWtMekV1TkM0eUwzSmxjM0J2Ym1RdWJXbHVMbXB6WENJLVBDOXpZM0pwY0hRLVhISmNiandoVzJWdVpHbG1YUzB0UGx4eVhHNDhjMk55YVhCMElIUjVjR1U5WENKMFpYaDBMMnBoZG1GelkzSnBjSFJjSWlCemNtTTlYQ0p5WlhOdmRYSmpaWE12YzJ0cGJqQXZhbk12YW5GMVpYSjVMVEV1TVRFdU15NXRhVzR1YW5OY0lqNDhMM05qY21sd2RENWNjbHh1UEhOamNtbHdkQ0IwZVhCbFBWd2lkR1Y0ZEM5cVlYWmhjMk55YVhCMFhDSWdjM0pqUFZ3aWNtVnpiM1Z5WTJWekwzTnJhVzR3TDJwekwySnZiM1J6ZEhKaGNDNXRhVzR1YW5OY0lqNDhMM05qY21sd2RENWNjbHh1UEhOamNtbHdkQ0IwZVhCbFBWd2lkR1Y0ZEM5cVlYWmhjMk55YVhCMFhDSWdjM0pqUFZ3aWNtVnpiM1Z5WTJWekwzTnJhVzR3TDJwekwyWjFibU4wYVc5dUxtcHpYQ0ktUEM5elkzSnBjSFEtWEhKY2JseHlYRzRnSUNBZ1BIUnBkR3hsUG1WSlJFRlRJRUYxZEdobGJuUnBZMkYwYVc5dUlGTmxjblpwWTJVZ0tFbGtVQ2s4TDNScGRHeGxQbHh5WEc0OEwyaGxZV1EtWEhKY2JqeGliMlI1UGx4eVhHNDhJUzB0VTFSQlVsUWdTRVZCUkVWU0xTMC1YSEpjYmp4b1pXRmtaWElnWTJ4aGMzTTlYQ0pvWldGa1pYSmNJajVjY2x4dVhIUThaR2wySUdOc1lYTnpQVndpWTI5dWRHRnBibVZ5WENJLVhISmNibHgwWEhROGFERS1aVWxFUVZNZ1FYVjBhR1Z1ZEdsallYUnBiMjRnVTJWeWRtbGpaU0FvU1dSUUtUd3ZhREUtWEhKY2JseDBQQzlrYVhZLVhISmNiand2YUdWaFpHVnlQbHh5WEc0OElTMHRSVTVFSUVoRlFVUkZVaTB0UGx4eVhHNDhiV0ZwYmo1Y2NseHVQR1JwZGlCamJHRnpjejFjSW1OdmJuUmhhVzVsY2x3aVBseHlYRzRnSUNBZ1BHUnBkaUJqYkdGemN6MWNJbkp2ZDF3aVBseHlYRzRnSUNBZ0lDQWdJRHhrYVhZZ1kyeGhjM005WENKMFlXSXRZMjl1ZEdWdWRGd2lQbHh5WEc0Z0lDQWdJQ0FnSUNBZ0lDQThaR2wySUhKdmJHVTlYQ0owWVdKd1lXNWxiRndpSUdOc1lYTnpQVndpZEdGaUxYQmhibVVnWm1Ga1pTQnBiaUJoWTNScGRtVmNJaUJwWkQxY0luUmhZaTB3TWx3aVBseHlYRzRnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdQR1JwZGlCamJHRnpjejFjSW1OdmJDMXRaQzB4TWx3aVBseHlYRzRnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUR4b01qNVNaWE53YjI1elpWeHlYRzRnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUR3dmFESS1YSEpjYmlBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0E4TDJScGRqNWNjbHh1SUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJRnh5WEc0OFpHbDJJR05zWVhOelBWd2lZMjlzTFcxa0xUWmNJajVjY2x4dUlDQWdJRHhrYVhZZ1kyeGhjM005WENKcGJHeDFjM1J5WVhScGIyNWNJajVjY2x4dUlDQWdJQ0FnUEdneFBqeHpjR0Z1SUdOc1lYTnpQVndpWkdWdGJ5MXVZVzFsWENJLVNVUlFQQzl6Y0dGdVBqd3ZhREUtWEhKY2JpQWdJQ0E4TDJScGRqNWNjbHh1UEM5a2FYWS1YSEpjYmlBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0E4WkdsMklHTnNZWE56UFZ3aVkyOXNMVzFrTFRaY0lqNWNjbHh1SUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBOFptOXliU0JwWkQxY0ltZGxibVZ5YVdOR2IzSnRYQ0lnYm1GdFpUMWNJbWRsYm1WeWFXTkdiM0p0WENJLVhISmNibHh5WEc0Z0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0E4YVc1d2RYUWdkSGx3WlQxY0ltaHBaR1JsYmx3aUlHbGtQVndpWlhKeWIzSk5aWE56WVdkbFhDSWdibUZ0WlQxY0ltVnljbTl5VFdWemMyRm5aVndpSUhaaGJIVmxQVndpYm5Wc2JGd2lMejVjY2x4dUlDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnUEdsdWNIVjBJSFI1Y0dVOVhDSm9hV1JrWlc1Y0lpQnBaRDFjSW1WeWNtOXlUV1Z6YzJGblpWUnBkR3hsWENJZ2JtRnRaVDFjSW1WeWNtOXlUV1Z6YzJGblpWUnBkR3hsWENKY2NseHVJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUhaaGJIVmxQVndpYm5Wc2JGd2lMejVjY2x4dUlDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQThMMlp2Y20wLVhISmNibHh5WEc0Z0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lEeG1iM0p0SUdsa1BWd2ljbVZrYVhKbFkzUkdiM0p0WENJZ2JtRnRaVDFjSW5KbFpHbHlaV04wUm05eWJWd2lJR0ZqZEdsdmJqMWNJbWgwZEhBNkx5OXNiMk5oYkdodmMzUTZPREE0TUM5VGNHVmphV1pwWTFCeWIzaDVVMlZ5ZG1salpTOUpaSEJTWlhOd2IyNXpaVndpSUcxbGRHaHZaRDFjSW5CdmMzUmNJajVjY2x4dUlDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnUEdScGRpQmpiR0Z6Y3oxY0ltWnZjbTB0WjNKdmRYQmNJajVjY2x4dUlDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lEeHNZV0psYkNCbWIzSTlYQ0pxVTI5dVVtVnpjRzl1YzJWRVpXTnZaR1ZrWENJLVUyMXpjM0JVYjJ0bGJpQlNaWE53YjI1elpUd3ZiR0ZpWld3LVhISmNiaUFnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0E4ZEdWNGRHRnlaV0VnYm1GdFpUMWNJbXBUYjI1U1pYTndiMjV6WlVSbFkyOWtaV1JjSWlCcFpEMWNJbXBUYjI1U1pYTndiMjV6WlVSbFkyOWtaV1JjSWlCamJHRnpjejFjSW1admNtMHRZMjl1ZEhKdmJGd2lYSEpjYmlBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ2NtOTNjejFjSWpFd1hDSS1lMXh1SUNBZ1hDSnlaWE53YjI1elpWd2lJRG9nZTF4dUlDQWdJQ0FnWENKaGRIUnlhV0oxZEdWZmJHbHpkRndpSURvZ1d5QjdYRzRnSUNBZ0lDQWdJQ0JjSW5SNWNHVmNJaUE2SUZ3aWMzUnlhVzVuWDJ4cGMzUmNJaXhjYmlBZ0lDQWdJQ0FnSUZ3aWJtRnRaVndpSURvZ1hDSk1aV2RoYkU1aGJXVmNJaXhjYmlBZ0lDQWdJQ0FnSUZ3aWRtRnNkV1Z6WENJZ09pQmJJSHRjYmlBZ0lDQWdJQ0FnSUNBZ0lGd2lkbUZzZFdWY0lpQTZJRndpUTNWeWNtVnVkQ0JNWldkaGJDQk9ZVzFsWENKY2JpQWdJQ0FnSUNBZ0lIMGdYVnh1SUNBZ0lDQWdmU3dnZTF4dUlDQWdJQ0FnSUNBZ1hDSjBlWEJsWENJZ09pQmNJbk4wY21sdVoxOXNhWE4wWENJc1hHNGdJQ0FnSUNBZ0lDQmNJbTVoYldWY0lpQTZJRndpVEdWbllXeFFaWEp6YjI1SlpHVnVkR2xtYVdWeVhDSXNYRzRnSUNBZ0lDQWdJQ0JjSW5aaGJIVmxjMXdpSURvZ1d5QjdYRzRnSUNBZ0lDQWdJQ0FnSUNCY0luWmhiSFZsWENJZ09pQmNJakV5TlRRMU56UTROME52YlhCaGJubGNJbHh1SUNBZ0lDQWdJQ0FnZlNCZFhHNGdJQ0FnSUNCOUxDQjdYRzRnSUNBZ0lDQWdJQ0JjSW5SNWNHVmNJaUE2SUZ3aVlXUmtjbVZ6YzBsa1hDSXNYRzRnSUNBZ0lDQWdJQ0JjSW01aGJXVmNJaUE2SUZ3aVEzVnljbVZ1ZEVGa1pISmxjM05jSWl4Y2JpQWdJQ0FnSUNBZ0lGd2lkbUZzZFdWY0lpQTZJSHRjYmlBZ0lDQWdJQ0FnSUNBZ0lGd2lZM1pmWVdSa2NtVnpjMTloY21WaFhDSWdPaUJjSWtWMGRHVnlZbVZsYTF3aUxGeHVJQ0FnSUNBZ0lDQWdJQ0FnWENKaFpHMXBibDkxYm1sMFgyWnBjbk4wWDJ4cGJtVmNJaUE2SUZ3aVFrVmNJaXhjYmlBZ0lDQWdJQ0FnSUNBZ0lGd2lZV1J0YVc1ZmRXNXBkRjl6WldOdmJtUmZiR2x1WlZ3aUlEb2dYQ0pGVkZSRlVrSkZSVXRjSWl4Y2JpQWdJQ0FnSUNBZ0lDQWdJRndpYkc5allYUnZjbDlrWlhOcFoyNWhkRzl5WENJZ09pQmNJakk0WENJc1hHNGdJQ0FnSUNBZ0lDQWdJQ0JjSW14dlkyRjBiM0pmYm1GdFpWd2lJRG9nWENKRVNVZEpWQ0JpZFdsc1pHbHVaMXdpTEZ4dUlDQWdJQ0FnSUNBZ0lDQWdYQ0p3YjE5aWIzaGNJaUE2SUZ3aU1USXpORndpTEZ4dUlDQWdJQ0FnSUNBZ0lDQWdYQ0p3YjNOMFgyTnZaR1ZjSWlBNklGd2lNVEEwTUZ3aUxGeHVJQ0FnSUNBZ0lDQWdJQ0FnWENKd2IzTjBYMjVoYldWY0lpQTZJRndpUlZSVVJWSkNSVVZMSUVOSVFWTlRSVndpTEZ4dUlDQWdJQ0FnSUNBZ0lDQWdYQ0owYUc5eWIzVm5hR1poY21WY0lpQTZJRndpVW5WbElFSmxiR3hwWVhKa1hDSmNiaUFnSUNBZ0lDQWdJSDFjYmlBZ0lDQWdJSDBzSUh0Y2JpQWdJQ0FnSUNBZ0lGd2lkSGx3WlZ3aUlEb2dYQ0p6ZEhKcGJtZGZiR2x6ZEZ3aUxGeHVJQ0FnSUNBZ0lDQWdYQ0p1WVcxbFhDSWdPaUJjSWtaaGJXbHNlVTVoYldWY0lpeGNiaUFnSUNBZ0lDQWdJRndpZG1Gc2RXVnpYQ0lnT2lCYklIdGNiaUFnSUNBZ0lDQWdJQ0FnSUZ3aWRtRnNkV1ZjSWlBNklGd2lUV0Z5YVc5Y0lseHVJQ0FnSUNBZ0lDQWdmU0JkWEc0Z0lDQWdJQ0I5TENCN1hHNGdJQ0FnSUNBZ0lDQmNJblI1Y0dWY0lpQTZJRndpYzNSeWFXNW5YMnhwYzNSY0lpeGNiaUFnSUNBZ0lDQWdJRndpYm1GdFpWd2lJRG9nWENKR2FYSnpkRTVoYldWY0lpeGNiaUFnSUNBZ0lDQWdJRndpZG1Gc2RXVnpYQ0lnT2lCYklIdGNiaUFnSUNBZ0lDQWdJQ0FnSUZ3aWRtRnNkV1ZjSWlBNklGd2lVbTl6YzJsY0lseHVJQ0FnSUNBZ0lDQWdmU0JkWEc0Z0lDQWdJQ0I5TENCN1hHNGdJQ0FnSUNBZ0lDQmNJblI1Y0dWY0lpQTZJRndpWkdGMFpWd2lMRnh1SUNBZ0lDQWdJQ0FnWENKdVlXMWxYQ0lnT2lCY0lrUmhkR1ZQWmtKcGNuUm9YQ0lzWEc0Z0lDQWdJQ0FnSUNCY0luWmhiSFZsWENJZ09pQmNJakU1TmpVdE1ERXRNREZjSWx4dUlDQWdJQ0FnZlN3Z2UxeHVJQ0FnSUNBZ0lDQWdYQ0owZVhCbFhDSWdPaUJjSW5OMGNtbHVaMTlzYVhOMFhDSXNYRzRnSUNBZ0lDQWdJQ0JjSW01aGJXVmNJaUE2SUZ3aVIyVnVaR1Z5WENJc1hHNGdJQ0FnSUNBZ0lDQmNJblpoYkhWbGMxd2lJRG9nV3lCN1hHNGdJQ0FnSUNBZ0lDQWdJQ0JjSW5aaGJIVmxYQ0lnT2lCY0lrMWhiR1ZjSWx4dUlDQWdJQ0FnSUNBZ2ZTQmRYRzRnSUNBZ0lDQjlMQ0I3WEc0Z0lDQWdJQ0FnSUNCY0luUjVjR1ZjSWlBNklGd2ljM1J5YVc1blgyeHBjM1JjSWl4Y2JpQWdJQ0FnSUNBZ0lGd2libUZ0WlZ3aUlEb2dYQ0pRWlhKemIyNUpaR1Z1ZEdsbWFXVnlYQ0lzWEc0Z0lDQWdJQ0FnSUNCY0luWmhiSFZsYzF3aUlEb2dXeUI3WEc0Z0lDQWdJQ0FnSUNBZ0lDQmNJblpoYkhWbFhDSWdPaUJjSWpFeU16UTJlWEJOYWx3aVhHNGdJQ0FnSUNBZ0lDQjlJRjFjYmlBZ0lDQWdJSDBnWFN4Y2JpQWdJQ0FnSUZ3aVlYVjBhR1Z1ZEdsallYUnBiMjVmWTI5dWRHVjRkRjlqYkdGemMxd2lJRG9nWENKQlhDSXNYRzRnSUNBZ0lDQmNJbU5zYVdWdWRGOUpjRjlCWkdSeVpYTnpYQ0lnT2lCY0lqSXhNaTR4TURFdU1UY3pMamcwWENJc1hHNGdJQ0FnSUNCY0ltTnlaV0YwWldSZmIyNWNJaUE2SUZ3aU1qQXlNUzB3T1Mwd09GUXhOam93TlRvME55NDNNREZhWENJc1hHNGdJQ0FnSUNCY0ltbGtYQ0lnT2lCY0lqUmlNR1V6WTJSbUxUWXpOamt0TkdZeE1TMWhOemd3TFdWbE5HRTBPV0UxWm1RNE5Wd2lMRnh1SUNBZ0lDQWdYQ0pwYm5KbGMzQnZibk5sWDNSdlhDSWdPaUJjSWpVMVlqbG1NMk5qTFRabU16a3RORFpsTmkxaE5HRXdMVE5qWVdZMFkyWTVPR0V3WVZ3aUxGeHVJQ0FnSUNBZ1hDSnBjM04xWlhKY0lpQTZJRndpUkVWTlR5MUpSRkJjSWl4Y2JpQWdJQ0FnSUZ3aWMzUmhkSFZ6WENJZ09pQjdYRzRnSUNBZ0lDQWdJQ0JjSW5OMFlYUjFjMTlqYjJSbFhDSWdPaUJjSW5OMVkyTmxjM05jSWx4dUlDQWdJQ0FnZlN4Y2JpQWdJQ0FnSUZ3aWMzVmlhbVZqZEZ3aUlEb2dYQ0l3TVRJek5EVTJYQ0lzWEc0Z0lDQWdJQ0JjSW5abGNuTnBiMjVjSWlBNklGd2lNVndpWEc0Z0lDQjlYRzU5UEM5MFpYaDBZWEpsWVQ1Y2NseHVJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJRHhwYm5CMWRDQjBlWEJsUFZ3aWFHbGtaR1Z1WENJZ2FXUTlYQ0prYjA1dmRHMXZaR2xtZVZSb1pWSmxjM0J2Ym5ObFhDSWdibUZ0WlQxY0ltUnZUbTkwYlc5a2FXWjVWR2hsVW1WemNHOXVjMlZjSWx4eVhHNGdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lIWmhiSFZsUFZ3aWIyNWNJaTgtWEhKY2JseHlYRzRnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnUEdsdWNIVjBJSFI1Y0dVOVhDSm9hV1JrWlc1Y0lpQnBaRDFjSWxOTlUxTlFVbVZ6Y0c5dWMyVmNJaUJ1WVcxbFBWd2lVMDFUVTFCU1pYTndiMjV6WlZ3aUx6NWNjbHh1SUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdQQzlrYVhZLVhISmNiaUFnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lEeHBkR1Z0SUhSNWNHVTlYQ0ppZFhSMGIyNWNJaUJ6ZEhsc1pUMG5aR2x6Y0d4aGVUcHViMjVsT3ljZ2FXUTlYQ0pwWkhCVGRXSnRhWFJpZFhSMGIyNWNJbHh5WEc0Z0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQmpiR0Z6Y3oxY0ltSjBiaUJpZEc0dFpHVm1ZWFZzZENCaWRHNHRiR2NnWW5SdUxXSnNiMk5yWENJZ2IyNWpiR2xqYXoxY0luSmxkSFZ5YmlCaVlYTmxOalJmWlc1amIyUmxLQ2s3WENJLVUzVmliV2wwWEhKY2JpQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUR3dmFYUmxiVDVjY2x4dUlDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQThMMlp2Y20wLVhISmNiaUFnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnUEc1dmMyTnlhWEIwUGx4eVhHNGdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBOFptOXliU0JwWkQxY0ltNXZTbUYyWVZOamNtbHdkRVp2Y20xY0lpQnVZVzFsUFZ3aWJtOUtZWFpoVTJOeWFYQjBVbVZrYVhKbFkzUkdiM0p0WENJZ1lXTjBhVzl1UFZ3aWFIUjBjRG92TDJ4dlkyRnNhRzl6ZERvNE1EZ3dMMU53WldOcFptbGpVSEp2ZUhsVFpYSjJhV05sTDBsa2NGSmxjM0J2Ym5ObFhDSmNjbHh1SUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ2JXVjBhRzlrUFZ3aWNHOXpkRndpUGx4eVhHNGdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdQR2x1Y0hWMElIUjVjR1U5WENKb2FXUmtaVzVjSWlCcFpEMWNJbE5OVTFOUVVtVnpjRzl1YzJWT2IwcFRYQ0lnYm1GdFpUMWNJbE5OVTFOUVVtVnpjRzl1YzJWY0lseHlYRzRnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJSFpoYkhWbFBWd2laWGR2WjBsRFFXbGpiVlo2WTBjNWRXTXlWV2xKUkc5blpYZHZaMGxEUVdkSlEwRnBXVmhTTUdOdGJHbGtXRkpzV0RKNGNHTXpVV2xKUkc5blYzbENOME5wUVdkSlEwRm5TVU5CWjBsRFNqQmxXRUpzU1dsQk5rbERTbnBrU0Vwd1ltMWtabUpIYkhwa1EwbHpRMmxCWjBsRFFXZEpRMEZuU1VOS2RWbFhNV3hKYVVFMlNVTktUVnBYWkdoaVJUVm9ZbGRWYVV4QmIyZEpRMEZuU1VOQlowbERRV2xrYlVaelpGZFdla2xwUVRaSlJuTm5aWGR2WjBsRFFXZEpRMEZuU1VOQlowbERRV2xrYlVaelpGZFZhVWxFYjJkSmEwNHhZMjVLYkdKdVVXZFVSMVp1V1ZkM1oxUnRSblJhVTBsTFNVTkJaMGxEUVdkSlEwRm5abE5DWkVOcFFXZEpRMEZuU1Vnd2MwbEljMHRKUTBGblNVTkJaMGxEUVdkSmJsSTFZMGRWYVVsRWIyZEpiazR3WTIxc2RWb3hPWE5oV0U0d1NXbDNTMGxEUVdkSlEwRm5TVU5CWjBsdE5XaGlWMVZwU1VSdlowbHJlR3hhTWtaelZVZFdlV015T1hWVFYxSnNZbTVTY0ZwdGJHeGphVWx6UTJsQlowbERRV2RKUTBGblNVTktNbGxYZURGYVdFMXBTVVJ2WjFkNVFqZERhVUZuU1VOQlowbERRV2RKUTBGblNVTktNbGxYZURGYVUwbG5UMmxCYVUxVVNURk9SRlV6VGtSbk0xRXlPWFJqUjBaMVpWTkpTMGxEUVdkSlEwRm5TVU5CWjJaVFFtUkRhVUZuU1VOQlowbElNSE5KU0hOTFNVTkJaMGxEUVdkSlEwRm5TVzVTTldOSFZXbEpSRzluU1cxR2ExcElTbXhqTTA1S1drTkpjME5wUVdkSlEwRm5TVU5CWjBsRFNuVlpWekZzU1dsQk5rbERTa1JrV0VwNVdsYzFNRkZYVW10amJWWjZZM2xKYzBOcFFXZEpRMEZuU1VOQlowbERTakpaVjNneFdsTkpaMDlwUWpkRGFVRm5TVU5CWjBsRFFXZEpRMEZuU1VOS2FtUnNPV2hhUjFKNVdsaE9lbGd5Um5sYVYwVnBTVVJ2WjBsclZqQmtSMVo1V1cxV2JHRjVTWE5EYVVGblNVTkJaMGxEUVdkSlEwRm5TVU5LYUZwSE1YQmliRGt4WW0xc01GZ3lXbkJqYms0d1dESjRjR0p0VldsSlJHOW5TV3RLUmtscGQwdEpRMEZuU1VOQlowbERRV2RKUTBGblNXMUdhMkpYYkhWWU0xWjFZVmhTWm1NeVZtcGlNalZyV0RKNGNHSnRWV2xKUkc5blNXdFdWVlpGVmxOUmExWkdVM2xKYzBOcFFXZEpRMEZuU1VOQlowbERRV2RKUTBwellqSk9hR1JIT1hsWU1sSnNZekpzYm1KdFJqQmlNMGxwU1VSdlowbHFTVFJKYVhkTFNVTkJaMGxEUVdkSlEwRm5TVU5CWjBsdGVIWlpNa1l3WWpOS1ptSnRSblJhVTBsblQybEJhVkpGYkVoVFZsRm5XVzVXY0dKSFVuQmliV05wVEVGdlowbERRV2RKUTBGblNVTkJaMGxEUVdsalJ6bG1XVzA1TkVscFFUWkpRMGw0VFdwTk1FbHBkMHRKUTBGblNVTkJaMGxEUVdkSlEwRm5TVzVDZG1NelVtWlpNamxyV2xOSlowOXBRV2xOVkVFd1RVTkpjME5wUVdkSlEwRm5TVU5CWjBsRFFXZEpRMHAzWWpOT01GZ3lOV2hpVjFWcFNVUnZaMGxyVmxWV1JWWlRVV3RXUmxONVFrUlRSVVpVVlRCVmFVeEJiMmRKUTBGblNVTkJaMGxEUVdkSlEwRnBaRWRvZG1OdE9URmFNbWh0V1ZoS2JFbHBRVFpKUTBwVFpGZFZaMUZ0Vm5OaVIyeG9ZMjFSYVVOcFFXZEpRMEZuU1VOQlowbElNRXRKUTBGblNVTkJaMlpUZDJkbGQyOW5TVU5CWjBsRFFXZEpRMEZwWkVoc2QxcFRTV2RQYVVGcFl6TlNlV0ZYTlc1WU1uaHdZek5SYVV4QmIyZEpRMEZuU1VOQlowbERRV2xpYlVaMFdsTkpaMDlwUVdsU2JVWjBZVmQ0TlZSdFJuUmFVMGx6UTJsQlowbERRV2RKUTBGblNVTktNbGxYZURGYVdFMXBTVVJ2WjFkNVFqZERhVUZuU1VOQlowbERRV2RKUTBGblNVTktNbGxYZURGYVUwbG5UMmxCYVZSWFJubGhWemhwUTJsQlowbERRV2RKUTBGblNVZ3daMWhSYjJkSlEwRm5TVU5DT1V4RFFqZERhVUZuU1VOQlowbERRV2RKUTBvd1pWaENiRWxwUVRaSlEwcDZaRWhLY0dKdFpHWmlSMng2WkVOSmMwTnBRV2RKUTBGblNVTkJaMGxEU25WWlZ6RnNTV2xCTmtsRFNrZGhXRXA2WkVVMWFHSlhWV2xNUVc5blNVTkJaMGxEUVdkSlEwRnBaRzFHYzJSWFZucEphVUUyU1VaeloyVjNiMmRKUTBGblNVTkJaMGxEUVdkSlEwRnBaRzFHYzJSWFZXbEpSRzluU1d4S2RtTXpUbkJKWjI5blNVTkJaMGxEUVdkSlEwSTVTVVl3UzBsRFFXZEpRMEZuWmxOM1oyVjNiMmRKUTBGblNVTkJaMGxEUVdsa1NHeDNXbE5KWjA5cFFXbGFSMFl3V2xOSmMwTnBRV2RKUTBGblNVTkJaMGxEU25WWlZ6RnNTV2xCTmtsRFNrVlpXRkpzVkRKYVEyRllTakJoUTBselEybEJaMGxEUVdkSlEwRm5TVU5LTWxsWGVERmFVMGxuVDJsQmFVMVVhekpPVXpCM1RWTXdkMDFUU1V0SlEwRm5TVU5CWjJaVGQyZGxkMjluU1VOQlowbERRV2RKUTBGcFpFaHNkMXBUU1dkUGFVRnBZek5TZVdGWE5XNVlNbmh3WXpOUmFVeEJiMmRKUTBGblNVTkJaMGxEUVdsaWJVWjBXbE5KWjA5cFFXbFNNbFoxV2tkV2VVbHBkMHRKUTBGblNVTkJaMGxEUVdkSmJscG9Za2hXYkdONVNXZFBhVUppU1VoelMwbERRV2RKUTBGblNVTkJaMGxEUVdkSmJscG9Za2hXYkVscFFUWkpRMHBPV1ZkNGJFbG5iMmRKUTBGblNVTkJaMGxEUWpsSlJqQkxTVU5CWjBsRFFXZG1VM2RuWlhkdlowbERRV2RKUTBGblNVTkJhV1JJYkhkYVUwbG5UMmxCYVdNelVubGhWelZ1V0RKNGNHTXpVV2xNUVc5blNVTkJaMGxEUVdkSlEwRnBZbTFHZEZwVFNXZFBhVUZwVlVkV2VXTXlPWFZUVjFKc1ltNVNjRnB0Ykd4amFVbHpRMmxCWjBsRFFXZEpRMEZuU1VOS01sbFhlREZhV0UxcFNVUnZaMWQ1UWpkRGFVRm5TVU5CWjBsRFFXZEpRMEZuU1VOS01sbFhlREZhVTBsblQybEJhVTFVU1hwT1JGbzFZMFV4Y1VsbmIyZEpRMEZuU1VOQlowbERRamxKUmpCTFNVTkJaMGxEUVdkbVUwSmtURUZ2WjBsRFFXZEpRMEZwV1ZoV01HRkhWblZrUjJ4cVdWaFNjR0l5TldaWk1qbDFaRWRXTkdSR09XcGlSMFo2WTNsSlowOXBRV2xSVTBselEybEJaMGxEUVdkSlEwcHFZa2RzYkdKdVVtWlRXRUptVVZkU2EyTnRWbnBqZVVsblQybEJhVTFxUlhsTWFrVjNUVk0wZUU1NlRYVlBSRkZwVEVGdlowbERRV2RKUTBGcFdUTktiRmxZVW14YVJqbDJZbWxKWjA5cFFXbE5ha0Y1VFZNd2QwOVRNSGRQUmxGNFRtcHZkMDVVYnpCT2VUUXpUVVJHWVVscGQwdEpRMEZuU1VOQlowbHRiR3RKYVVFMlNVTkpNRmxxUW14Tk1rNXJXbWt3TWsxNldUVk1WRkp0VFZSRmRGbFVZelJOUXpGc1dsUlNhRTVFYkdoT1YxcHJUMFJWYVV4QmIyZEpRMEZuU1VOQmFXRlhOWGxhV0U1M1lqSTFlbHBXT1RCaWVVbG5UMmxCYVU1VVZtbFBWMWw2V1RKTmRFNXRXWHBQVXpBd1RtMVZNa3hYUlRCWlZFRjBUVEpPYUZwcVVtcGFhbXMwV1ZSQ2FFbHBkMHRKUTBGblNVTkJaMGx0Ykhwak0xWnNZMmxKWjA5cFFXbFNSVlpPVkhreFNsSkdRV2xNUVc5blNVTkJaMGxEUVdsak0xSm9aRWhXZWtscFFUWkpTSE5MU1VOQlowbERRV2RKUTBGblNXNU9NRmxZVWpGak1UbHFZakpTYkVscFFUWkpRMHA2WkZkT2FscFlUbnBKWjI5blNVTkJaMGxEUWpsTVFXOW5TVU5CWjBsRFFXbGpNMVpwWVcxV2FtUkRTV2RQYVVGcFRVUkZlVTE2VVRGT2FVbHpRMmxCWjBsRFFXZEpRMG95V2xoS2VtRlhPWFZKYVVFMlNVTkplRWxuYjJkSlEwSTVRMjR3UFZ3aUx6NWNjbHh1SUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUR4cGJuQjFkQ0IwZVhCbFBWd2ljM1ZpYldsMFhDSWdhV1E5WENKemRXSnRhWFJDZFhSMGIyNHhYQ0lnWTJ4aGMzTTlYQ0ppZEc0Z1luUnVMVzVsZUhSY0lpQjJZV3gxWlQxY0lsTjFZbTFwZEZ3aUx6NWNjbHh1SUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdQQzltYjNKdFBseHlYRzRnSUNBZ0lDQWdJQ0FnSUNBZ0lDQWdJQ0FnSUR3dmJtOXpZM0pwY0hRaWZRLkh1SEx3UnItTVZ4TEcxbm54WUZJNDRja3FYa2Q4UU1qODRpWUg2QVJRa0tNMXFIQV84ZHkyalREQUVjT242Q1h2MXhjQUp0MHEtOXg2Tjlxa2NmcGFlRDZGS182aHo2UUppUm0ySmhSektqM1JQQXpsekRCOF9kXzQtT1dZb1l6eEs3TmlPSmlrX0lJRVpKVG01Y2hXdE41ejNzRnoxV0NkUDNJT0tKQ3dCQSIsImFzc2VydGlvbiI6ImV5SjBlWEFpT2lKS1YxUWlMQ0poYkdjaU9pSlNVekkxTmlKOS5leUprWVhSaE1pSTZJbHdpWTNKbFlYUmxaRjl2Ymx3aUlEb2dYQ0l5TURJeExUQTVMVEE0VkRFMk9qQTFPalEzTGpjd01WcGNJaXhjYmlBZ0lDQWdJRndpYVdSY0lpQTZJRndpTkdJd1pUTmpaR1l0TmpNMk9TMDBaakV4TFdFM09EQXRaV1UwWVRRNVlUVm1aRGcxWENJc1hHNGdJQ0FnSUNCY0ltbHVjbVZ6Y0c5dWMyVmZkRzljSWlBNklGd2lOVFZpT1dZelkyTXRObVl6T1MwME5tVTJMV0UwWVRBdE0yTmhaalJqWmprNFlUQmhYQ0lzWEc0Z0lDQWdJQ0JjSW1semMzVmxjbHdpSURvZ1hDSkVSVTFQTFVsRVVGd2lMRnh1SUNBZ0lDQWdYQ0p6ZEdGMGRYTmNJaUE2SUh0Y2JpQWdJQ0FnSUNBZ0lGd2ljM1JoZEhWelgyTnZaR1ZjSWlBNklGd2ljM1ZqWTJWemMxd2lYRzRnSUNBZ0lDQjlMRnh1SUNBZ0lDQWdYQ0p6ZFdKcVpXTjBYQ0lnT2lCY0lqQXhNak0wTlRaY0lpeGNiaUFnSUNBZ0lGd2lkbVZ5YzJsdmJsd2lJRG9nWENJeFhDSmNiaUFnSUgxY2JuMDhMM1JsZUhSaGNtVmhQaUo5LmxXaU13M3pmNDZSb1NnYzR0aFlHNk16U0JSa3BkdjY2ejlPNXhzRlNkb2lycFI3ZThMNmtWZi1uVmVYNDVycXc1clNfNV9sd3ZnQ0NsYnJZSERsbjg5N0NHdkJjcnlGQXVrRjVqU3hOZEZaSklFOFlFZjlBQV91M040ZlYwcmlsakRLdmxnSW5XTTlta2szSjJZN1lENkF0dlhKVDh0cTQzX2VxSTRhX1BEQSJ9.rVLes34dZ7fNb7YsWCrOkfMwahYi-U8K7TXvS5kVHQWjuMzjJbc56v5qCr3H39g2GeJQKlw3B1_ab5tndIU_aG186jSihIJEYo7hF_vonyMKTatXO0XsjfdVU9T-W_1kISQFlyy92c2b7_YCypBTqCZZk-plV8kEnXWeHooUJr4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.executeButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new SendRequestToR2DAccess()).execute();
            }
        });

        Log.d("MR2DA.MainActivity", "Creating MR2DA instance...");
        mr2da = MR2DAFactory.createAsync("http://213.249.46.205:8080/iehr/r2da/",
                authToken, new CallbackHandler());


       /*

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
         */
    }


    private class SendRequestToR2DAccess extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            try {
                mr2da.getPatientSummary();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*
            mr2da.getResourcesByCategory(FHIRResourceCategory.ENCOUNTER,
                    null, false);
             */
            return null;
        }

    }
        /*
     * ASYNC TASK #1: method MR2D.login()
     */
    /*
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
    */

    /*
     * ASYNC TASK #2: method MR2D.logout()
     */
    /*
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
    */


}
