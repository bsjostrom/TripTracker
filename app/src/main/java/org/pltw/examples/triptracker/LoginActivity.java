package org.pltw.examples.triptracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class LoginActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getName();

    private EditText mEnterName;
    private Button mSignMeUpButton;
    private Button mLogIn;
    private TextView mSignUpTextView;
    private EditText mEnterEmail;
    private EditText mEnterPassword;

    private boolean validateData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Backendless.initApp( this,
                getString(R.string.BE_APP_ID),
                getString(R.string.BE_ANDROID_API_KEY));

        mEnterName = (EditText) findViewById (R.id.name);
        mSignMeUpButton = (Button) findViewById(R.id.sign_up_button);
        mLogIn = (Button) findViewById(R.id.login_button);
        mSignUpTextView = (TextView) findViewById(R.id.sign_up_text);
        mEnterEmail = (EditText) findViewById(R.id.enter_email);
        mEnterPassword = (EditText) findViewById(R.id.enter_password);

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Switches from Sign up page to Log In Page
                if(mSignMeUpButton.getVisibility() == View.VISIBLE) {
                    mEnterName.setVisibility(View.GONE);
                    mSignMeUpButton.setVisibility(View.GONE);
                    mLogIn.setVisibility(View.VISIBLE);
                    mSignUpTextView.setText(getString(R.string.sign_up_text));

                    // Switches from Log In page to Sign Up page
                } else if(mLogIn.getVisibility() == View.VISIBLE) {
                    mEnterName.setVisibility(View.VISIBLE);
                    mSignMeUpButton.setVisibility(View.VISIBLE);
                    mLogIn.setVisibility(View.GONE);
                    mSignUpTextView.setText(getString(R.string.cancel_sign_up_text));

                }
            }

        });

        mSignMeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = mEnterEmail.getText().toString();
                String password = mEnterPassword.getText().toString();
                String name = mEnterName.getText().toString();

                userEmail = userEmail.trim();
                password = password.trim();
                name = name.trim();

                if (!userEmail.isEmpty() &&!password.isEmpty() && !name.isEmpty()) {

                    if (userEmail.contains("@") && userEmail.contains(".")){
                        if (password.length()>= 6){
                            if (!password.equals(userEmail)){
                                validateData = true;
                            }
                        }
                        }
                        else{
                        validateData = false;
                        warnUser(getString(R.string.data_not_validated));
                    }


                    if(validateData = true){} //ended here
                    /* register the user in Backendless */
                    BackendlessUser user = new BackendlessUser();
                    user.setEmail(userEmail);
                    user.setPassword(password);
                    user.setProperty("name", name);

                    Backendless.UserService.register(user,
                            new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse( BackendlessUser backendlessUser ) {
                                    Log.i(TAG, "Registration successful for " +
                                            backendlessUser.getEmail());
                                }
                                @Override
                                public void handleFault( BackendlessFault fault ) {
                                    Log.i(TAG, "Registration failed: " + fault.getMessage());
                                    warnUser(fault.getMessage());

                                }
                            } );

                }
                else {
                    /* warn the user of the problem */
                   warnUser(getString(R.string.empty_field_signup_error));

                }
            }

        });

        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = mEnterEmail.getText().toString();
                String password = mEnterPassword.getText().toString();

                userEmail = userEmail.trim();
                password = password.trim();


                if (!userEmail.isEmpty() &&!password.isEmpty()) {

                    Backendless.UserService.login(userEmail, password,
                            new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse( BackendlessUser backendlessUser ) {
                                    Log.i(TAG, "Login successful for " +
                                            backendlessUser.getEmail());
                                    //unnecessary line of code below, implemented for ease
                                    warnUser("Login Successful!");
                                }
                                @Override
                                public void handleFault( BackendlessFault fault ) {
                                    Log.i(TAG, "Login failed: " + fault.getMessage());
                                    warnUser(fault.getMessage());

                                }
                            } );

                }
                else {
                    warnUser(getString(R.string.empty_field_signup_error));
                }
            }
        });


    }
    public String warnUser(String WarnUserMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(WarnUserMessage);
        builder.setTitle(R.string.authentication_error_title);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
