package org.pltw.examples.triptracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private EditText mEnterName;
    private Button mSignMeUpButton;
    private Button mLogIn;
    private TextView mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEnterName = (EditText) findViewById (R.id.name);
        mSignMeUpButton = (Button) findViewById(R.id.sign_up_button);
        mLogIn = (Button) findViewById(R.id.login_button);
        mSignUpTextView = (TextView) findViewById(R.id.sign_up_text);

        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Switches from Sign up page to Log In Page
                if(mSignMeUpButton.getVisibility() == View.VISIBLE) {
                    mEnterName.setVisibility(View.GONE);
                    mSignMeUpButton.setVisibility(View.GONE);
                    mLogIn.setVisibility(View.VISIBLE);
                    mSignUpTextView.setText("Sign Up!");

                    // Switches from Log In page to Sign Up page
                } else if(mLogIn.getVisibility() == View.VISIBLE) {
                    mEnterName.setVisibility(View.VISIBLE);
                    mSignMeUpButton.setVisibility(View.VISIBLE);
                    mLogIn.setVisibility(View.GONE);
                    mSignUpTextView.setText("Cancel Sign Up");

                }
            }

        });




    }
}
