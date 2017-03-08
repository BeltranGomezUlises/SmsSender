package com.ub.smssender.Main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ub.smssender.R;
import com.ub.smssender.models.ModelLogin;
import com.ub.smssender.services.BodyResponse;
import com.ub.smssender.utils.JWTDecoder;
import com.ub.smssender.utils.UtilPreferences;

import java.util.Arrays;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ub.smssender.services.WSUtils.webServices;

public class LoginActivity extends Activity {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String correo = "";
        String contra = "";

        if (UtilPreferences.isLoged(LoginActivity.this)){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }else{
            ModelLogin modelLogin = UtilPreferences.getModelLogin(LoginActivity.this);
            correo = modelLogin.getCorreo();
            contra = modelLogin.getContra();
        }

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(correo);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setText(contra);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            login(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }

    private void login(final String email, final String pass){
        final Call<BodyResponse> request = webServices().login(new ModelLogin(email, pass));
        request.enqueue(new Callback<BodyResponse>() {
            @Override
            public void onResponse(Call<BodyResponse> call, Response<BodyResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().isExito()){
                        UtilPreferences.saveLoged(LoginActivity.this, new ModelLogin(email,pass));
                        UtilPreferences.saveToken(LoginActivity.this, response.body().getDatos().toString());
                        System.out.println("token: " + response.body().getDatos().toString());
                        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(myIntent);
                        showProgress(false);
                        finish();
                    }else{
                        showProgress(false);
                        Toast.makeText(LoginActivity.this, response.body().getMensaje(), Toast.LENGTH_LONG).show();;
                    }
                }else{
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, "respuesta no exitosa", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BodyResponse> call, Throwable t) {
                showProgress(false);
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "peticion al servidor fallida", Toast.LENGTH_LONG).show();;
                Log.e("Service: ", t.getMessage());

            }
        });

    }

}

