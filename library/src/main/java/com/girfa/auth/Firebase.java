package com.girfa.auth;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.girfa.file.Document;
import com.girfa.BuildConfig;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.Serializable;

/**
 * Created by Afrig Aminuddin on 29/03/2017.
 */

public class Firebase {
    private static final String NAME = Firebase.class.getSimpleName();
    private static final String
            E_NO_INTERNET = "No internet connection",
            E_GOOGLE = "Firebase auth failed",
            E_FIREBASE = "Firebase auth failed",
            E_FIREBASE_TOKEN = "Firebase token failed";

    private Firebase() {
    }

    public static void signInGoogle(Context context, Auth.Callback callback) {
        if (!connected(context, callback)) return;
        Intent intent = new Intent(context, GoogleActivity.class);
        GoogleActivity.callback = callback;
        context.startActivity(intent);
    }

    private static void signInFirebase(final Context context, AuthCredential authCredential, final Auth.Callback callback) {
        FirebaseAuth.getInstance().signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> firebase) {
                        if (firebase.isSuccessful()) {
                            tokenFirebase(context, firebase, callback);
                        } else {
                            if (callback != null) callback.onError(E_FIREBASE);
                            if (BuildConfig.DEBUG) Log.d(NAME, "signInFirebase " + firebase.getException().getMessage());
                        }
                    }
                });
    }

    private static void tokenFirebase(final Context context, final Task<AuthResult> firebase, final Auth.Callback callback) {
        firebase.getResult().getUser().getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            Auth.signIn(context, new Document().set("firebase", task.getResult().getToken()), callback);
                        } else {
                            if (callback != null) callback.onError(E_FIREBASE_TOKEN);
                            if (BuildConfig.DEBUG) Log.d(NAME, "tokenFirebase " + task.getException().getMessage());
                        }
                    }
                });
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private static boolean connected(Context context, Auth.Callback callback) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            if (callback != null) callback.onError(E_NO_INTERNET);
            return false;
        }
    }

    public static class GoogleActivity extends FragmentActivity {
        public static final int REQUEST_CODE = 9009;
        private GoogleApiClient api;
        private static Auth.Callback callback;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            String requestIdToken = getString(getResources()
                    .getIdentifier("default_web_client_id", "string", getPackageName()));
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestProfile()
                    .requestEmail()
                    .requestIdToken(requestIdToken)
                    .build();
            api = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, null)
                    .addApi(com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            Intent signInIntent = com.google.android.gms.auth.api.Auth.GoogleSignInApi.getSignInIntent(api);
            startActivityForResult(signInIntent, REQUEST_CODE);

        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE) {
                GoogleSignInResult result = com.google.android.gms.auth.api.Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount google = result.getSignInAccount();
                    AuthCredential credential = GoogleAuthProvider.getCredential(google.getIdToken(), null);
                    Firebase.signInFirebase(this, credential, callback);
                    api.clearDefaultAccountAndReconnect();
                } else {
                    if (callback != null) callback.onError(E_GOOGLE);
                    if (BuildConfig.DEBUG) Log.d(NAME, "signInGoogle " + result.getStatus().getStatusMessage());
                }
                finish();
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (api != null) {
                api.stopAutoManage(this);
                api.disconnect();
            }
        }
    }
}
