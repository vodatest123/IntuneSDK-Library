package com.vodafone.intune_lib.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.PromptBehavior;
import com.vodafone.intune_lib.R;
import com.vodafone.intune_lib.logging.AppLoggerWrapper;
import com.vodafone.intune_lib.utils.CheckNetworkState;

public class SDKWrapper implements AuthListener{

    private String TAG = this.getClass().getSimpleName();
    private Handler mHandler;
    private AuthenticationContext mAuthContext;
    Context mContext;
    Activity mActivity;

    public SDKWrapper(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        AppLoggerWrapper.infoLog(mContext,TAG, "SDKWrapper Instantiated.");
    }

    @Override
    public void onSignedIn() {
        // Must be run on the UI thread because it is modifying the UI
        mActivity.runOnUiThread(this::openMainView);
    }

    @Override
    public void onSignedOut() {
        Toast.makeText(mContext, mContext.getString(R.string.auth_out_success), Toast.LENGTH_SHORT).show();
        AppLoggerWrapper.infoLog(mContext,TAG, mContext.getResources().getString(R.string.auth_out_success));
        mActivity.runOnUiThread(this::openSignInView);
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(mContext, mContext.getString(R.string.err_auth, e.getLocalizedMessage()),
                Toast.LENGTH_LONG).show();
        AppLoggerWrapper.errorLog(mContext,TAG, mActivity.getString(R.string.err_auth, e.getLocalizedMessage()));
    }

    @Override
    public Context getContext() {
        if(mContext != null)
            return mContext;
        else
            return null;
    }
    
    private void SignInUserAccount(Bundle savedInstanceState)
    {
        //Check Internet Connectivity
        if (CheckNetworkState.isOnline(mContext)) {
            /* If the app has already started, the user has signed in, and the activity was just restarted,
             * skip the rest of this initialization and open the main UI */
            if (savedInstanceState != null && AuthManager.shouldRestoreSignIn(savedInstanceState)) {
                AppLoggerWrapper.infoLog(mContext,TAG, "app has already started, the user has signed in, and the activity was just restarted,\n" +
                        "skip the rest of this initialization and open the main UI ");
                onSignedIn();
                return;
            }

            // Start by making a sign in window to show instead of the main view
            //openSignInView();

            mAuthContext = new AuthenticationContext(mContext, AuthManager.AUTHORITY, true);
            // Will make sign in attempts that are allowed to access/modify the UI (prompt)
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(final Message msg) {
                    AppLoggerWrapper.infoLog(mContext,TAG, "Inside handleMessage() method ");
                    if (msg.what == AuthManager.MSG_PROMPT_AUTO) {
                        AuthManager.signInWithPrompt(mContext,mAuthContext, mActivity,
                                SDKWrapper.this, PromptBehavior.Auto, mHandler);
                    } else if (msg.what == AuthManager.MSG_PROMPT_ALWAYS) {
                        AuthManager.signInWithPrompt(mContext,mAuthContext, mActivity,
                                SDKWrapper.this, PromptBehavior.Always, mHandler);
                    }
                }
            };

            /* We only need to change/set the view and sign in if this is the first time the app
             * has opened, which is when savedInstanceState is null */
            if (savedInstanceState == null) {
                AppLoggerWrapper.infoLog(mContext,TAG, "app sign in if this is the first time the app has opened");
                AuthManager.signInSilent(mContext,mAuthContext, this, mHandler);
            }
        }
        else
        {
            AppLoggerWrapper.infoLog(mContext,TAG, "Network not available");
            Toast.makeText(mContext, "Network not available!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSignInView() {
        mActivity.setContentView(R.layout.sign_in);
        AppLoggerWrapper.infoLog(mContext,TAG, "Inside openSignInView() method.");
        mActivity.findViewById(R.id.sign_in_button).setOnClickListener(signInListener);
    }

    private final View.OnClickListener signInListener = (View view) ->
            mHandler.sendEmptyMessage(AuthManager.MSG_PROMPT_ALWAYS);

    private void openMainView() {
        //setContentView(R.layout.activity_main);
        //changeNavigationView(R.id.nav_home);
        AppLoggerWrapper.infoLog(mContext,TAG, mContext.getResources().getString(R.string.auth_success));
        Toast.makeText(mContext, R.string.auth_success, Toast.LENGTH_SHORT).show();
    }


    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        // Required by ADAL
        mAuthContext.onActivityResult(requestCode, resultCode, data);
    }

    protected void onSaveInstanceState(final Bundle outState) {
        AuthManager.onSaveInstanceState(outState);
    }
}
