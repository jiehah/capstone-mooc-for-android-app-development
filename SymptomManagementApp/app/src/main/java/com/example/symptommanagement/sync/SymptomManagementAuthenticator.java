package com.example.symptommanagement.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.content.Context;
import android.os.Bundle;

/**
 * Custom authenticator for managing user accounts in the Android Account Manager.
 */
public class SymptomManagementAuthenticator extends AbstractAccountAuthenticator {

    /**
     * Constructor for the SymptomManagementAuthenticator.
     *
     * @param context The context of the application.
     */
    public SymptomManagementAuthenticator(Context context) {
        super(context);
    }

    /**
     * Unused method to edit account properties.
     *
     * @param r Unused.
     * @param s Unused.
     * @return Always throws UnsupportedOperationException.
     */
    @Override
    public Bundle editProperties(AccountAuthenticatorResponse r, String s) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unused method to add a new account.
     *
     * @param r       Unused.
     * @param s       Unused.
     * @param s2      Unused.
     * @param strings Unused.
     * @param bundle  Unused.
     * @return Always returns null.
     */
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse r, String s, String s2, String[] strings, Bundle bundle) {
        return null;
    }

    /**
     * Unused method to confirm the credentials of an account.
     *
     * @param r       Unused.
     * @param account Unused.
     * @param bundle  Unused.
     * @return Always returns null.
     */
    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse r, Account account, Bundle bundle) {
        return null;
    }

    /**
     * Unused method to get an authentication token for an account.
     *
     * @param r       Unused.
     * @param account Unused.
     * @param s       Unused.
     * @param bundle  Unused.
     * @return Always throws UnsupportedOperationException.
     */
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse r, Account account, String s, Bundle bundle) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unused method to get the label for the authentication token.
     *
     * @param s Unused.
     * @return Always throws UnsupportedOperationException.
     */
    @Override
    public String getAuthTokenLabel(String s) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unused method to update the credentials of an account.
     *
     * @param r       Unused.
     * @param account Unused.
     * @param s       Unused.
     * @param bundle  Unused.
     * @return Always throws UnsupportedOperationException.
     */
    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse r, Account account, String s, Bundle bundle) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unused method to check if the account has specific features.
     *
     * @param r       Unused.
     * @param account Unused.
     * @param strings Unused.
     * @return Always throws UnsupportedOperationException.
     */
    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse r, Account account, String[] strings) {
        throw new UnsupportedOperationException();
    }
}
