package com.example.bruger.mandatoryreservation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SingleReservationActivity extends AppCompatActivity {

    public static final String RESERVATION = "reservation";
    Reservation reservation;
    String uri = "https://anbo-roomreservation.azurewebsites.net/api/reservations/";

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_reservation);
        Intent intent = getIntent();

        reservation = (Reservation) intent.getSerializableExtra(RESERVATION);
        Log.d(CommonStuff.TAG, reservation.toString());

        TextView userIdView = findViewById(R.id.singleReservation_userId_textview);
        userIdView.setText("user: " + reservation.getUserId());

        TextView purposeView = findViewById(R.id.singleReservation_purpose_textview);
        purposeView.setText("purpose: " + reservation.getPurpose());


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        Button buttontnDelete = findViewById(R.id.deleteButtonID);
        if (reservation.getUserId().equals(user.getEmail())) {
            buttontnDelete.setVisibility(View.VISIBLE);

        }

    }


    public void deleteReservationOnCLick(View view) {
        DeleteReservationTask task = new DeleteReservationTask();
        task.execute();
    }
    private class DeleteReservationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String code = "";
            HttpUrl route = HttpUrl.parse(uri)
                    .newBuilder()
                    .addPathSegment(reservation.getId())
                    .build();
            Request request = new Request.Builder().url(route).delete().build();

            try {
                OkHttpClient client = new OkHttpClient.Builder().build();
                Response response = client.newCall(request).execute();
                if (response.code() == 204) code = "complete";
                else if (response.code() == 404) code = "not found";
                else code = String.valueOf(response.code());
                return code;
            } catch (IOException ex) {
                Log.e("BUILDINGS", ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(), "Reservation deleted.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getBaseContext(), ReservationOverviewActivity.class));
        }
    }
}
