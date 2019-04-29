/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.vodafone.intune_lib.authentication;

import android.content.Context;

/**
 * Listener that would be implemented by a UI class and called by an authentication class so they
 * can interact.
 */
public interface AuthListener {

    /**
     * Called when the authenticator successfully signs in.
     */
    void onSignedIn();

    /**
     * Called when the authenticator successfully signs out.
     */
    void onSignedOut();

    /**
     * Called when the authentication flow throws an exception.
     * @param e the thrown exception
     */
    void onError(final Exception e);

    /**
     * Returns the context of the AuthListener, needed to access shared preferences.
     *
     * @return the context of the AuthListener
     */
    Context getContext();
}
