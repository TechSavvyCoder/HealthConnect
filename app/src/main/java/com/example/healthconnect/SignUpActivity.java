package com.example.healthconnect;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    EditText txtUserName, txtUserEmail, txtUserPass, txtUserConfirmPass, txtUserFirstName, txtUserLastName, txtDateOfBirth;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtUserName = (EditText) findViewById(R.id.txtUserName);
        txtUserEmail = (EditText) findViewById(R.id.txtEmail);
        txtUserPass = (EditText) findViewById(R.id.txtUserPass);
        txtUserConfirmPass = (EditText) findViewById(R.id.txtUserConfirmPass);
        txtUserFirstName = (EditText) findViewById(R.id.txtUserFirstName);
        txtUserLastName = (EditText) findViewById(R.id.txtUserLastName);

        txtDateOfBirth = findViewById(R.id.txtDateOfBirth);
        txtDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        // Get current date and time
        Date currentDate = new Date();

        // Format date as yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(currentDate);

        btnSignUp = (Button) findViewById(R.id.onboarding3_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtUserPass.getText().toString().equals(txtUserConfirmPass.getText().toString())) {
                    MyDatabaseHelper myDB = new MyDatabaseHelper(SignUpActivity.this);
                    myDB.addUser(
                            txtUserName.getText().toString().trim(),
                            txtUserPass.getText().toString().trim(),
                            "Doctor",
                            txtUserEmail.getText().toString().trim(),
                            txtUserFirstName.getText().toString().trim(),
                            txtUserLastName.getText().toString().trim(),
                            txtDateOfBirth.getText().toString().trim(),
                            formattedDate
                    );
                } else {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, monthOfYear, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
                    String formattedDate = sdf.format(selectedDate.getTime());

                    txtDateOfBirth.setText(formattedDate);
                },
                year, month, day);

        datePickerDialog.show();
    }
}