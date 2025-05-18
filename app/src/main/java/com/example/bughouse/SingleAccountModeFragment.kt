package com.example.bughouse
import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.example.bughouse.DTO.GraphMSALResponse
import com.example.bughouse.MSGraphRequestWrapper.callGraphAPIUsingVolley
import com.microsoft.identity.client.*
import com.microsoft.identity.client.IPublicClientApplication.ISingleAccountApplicationCreatedListener
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.CurrentAccountCallback
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.SignOutCallback
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalServiceException
import com.microsoft.identity.client.exception.MsalUiRequiredException
import com.google.gson.Gson
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation sample for 'Single account' mode.
 *
 *
 * If your app only supports one account being signed-in at a time, this is for you.
 * This requires "account_mode" to be set as "SINGLE" in the configuration file.
 * (Please see res/raw/auth_config_single_account.json for more info).
 *
 *
 * Please note that switching mode (between 'single' and 'multiple' might cause a loss of data.
 */
class SingleAccountModeFragment(private val context: Context)  {


    /* Azure AD Variables */
    private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
    private var mAccount: IAccount? = null
    var accountDetails: GraphMSALResponse? = null
    init{
        PublicClientApplication.createSingleAccountPublicClientApplication(
            context,
            R.raw.auth_config_single_account,
            object : ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    /*
                         * This test app assumes that the app is only going to support one account.
                         * This requires "account_mode" : "SINGLE" in the config json file.
                         */
                    Log.d("MSAL", "MSAL App is initialized!")
                    mSingleAccountApp = application
                    loadAccount()
                }

                override fun onError(exception: MsalException) {
                    displayError(exception)
                }
            })
    }

   suspend fun  signIn( activity: Activity) {

            if (mSingleAccountApp == null) {
                Log.w("signIn","Checking mSingle signin")
                return
            }
            val signInParameters: SignInParameters = SignInParameters.builder()
                .withActivity(activity)
                .withLoginHint(null)
                .withScopes(Arrays.asList(*scopes))
                .withCallback(authInteractiveCallback)
                .build()
            mSingleAccountApp!!.signIn(signInParameters)

    }

    suspend fun callGraphApiInteractive(activity: Activity)  {
        if (mSingleAccountApp == null) {
            return
        }
        suspendCoroutine<Unit> { continuation ->
            val parameters = AcquireTokenParameters.Builder()
                .startAuthorizationFromActivity(activity)
                .withScopes(Arrays.asList(*scopes))
                .withCallback(authInteractiveCallback)
                .forAccount(mAccount)
                .build()
            continuation.resume(Unit)

        /*
                 * If acquireTokenSilent() returns an error that requires an interaction (MsalUiRequiredException),
                 * invoke acquireToken() to have the user resolve the interrupt interactively.
                 *
                 * Some example scenarios are
                 *  - password change
                 *  - the resource you're acquiring a token for has a stricter set of requirement than your Single Sign-On refresh token.
                 *  - you're introducing a new scope which the user has never consented for.
                 */mSingleAccountApp!!.acquireToken(parameters)
        }
    }


    /**
     * Extracts a scope array from a text field,
     * i.e. from "User.Read User.ReadWrite" to ["user.read", "user.readwrite"]
     */
    private val scopes: Array<String>
        private get() = arrayOf<String>("user.read")

    /**
     * Load the currently signed-in account, if there's any.
     */
    public fun loadAccount() {
        if (mSingleAccountApp == null) {
            return
        }
        mSingleAccountApp!!.getCurrentAccountAsync(object : CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                // You can use the account data to update your UI or your app database.
                mAccount = activeAccount

                Log.w("what","I am here")
                mAccount?.let { Log.w("what", it.claims.toString()) }

            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.

                }
            }

            override fun onError(exception: MsalException) {
                displayError(exception)
            }
        })
    }/* Tokens expired or no session, retry with interactive *//* Exception when communicating with the STS, likely config issue *//* Exception inside MSAL, more info inside MsalError.java *//* Failed to acquireToken *//* Successfully got a token, use it to call a protected resource - MSGraph */

    private val authSilentCallback: SilentAuthenticationCallback
        private get() = object : SilentAuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                Log.d(TAG, "Successfully authenticated")

                /* Successfully got a token, use it to call a protected resource - MSGraph */
                callGraphAPI(
                    authenticationResult
                )
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
                Log.d(
                    TAG,
                    "Authentication failed: $exception"
                )
                displayError(exception)
                if (exception is MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception is MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception is MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
            }
        }

    private val authInteractiveCallback: AuthenticationCallback
        private get() = object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Log.d(TAG, "Successfully authenticated")
                Log.d(TAG, "ID Token: " + authenticationResult.account.claims!!["id_token"])

                /* Update account */mAccount = authenticationResult.account


                /* call graph */callGraphAPI(authenticationResult)
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */
                Log.d(
                    TAG,
                    "Authentication failed: $exception"
                )
                displayError(exception)
                if (exception is MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception is MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            override fun onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.")
            }
        }

    private fun callGraphAPI(authenticationResult: IAuthenticationResult) {
        callGraphAPIUsingVolley(
            context,
            MSGraphRequestWrapper.MS_GRAPH_ROOT_ENDPOINT + "v1.0/me",
            authenticationResult.accessToken,
            Response.Listener<JSONObject> { response -> /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "CG CALL: $response")
                val gsonOb = Gson()
                accountDetails = gsonOb.fromJson(response.toString(), GraphMSALResponse::class.java)


            },
            Response.ErrorListener { error ->
                Log.d(TAG, "Error: $error")
                displayError(error)
            })
    }

    fun signOut(){
        mSingleAccountApp!!.signOut( object : SignOutCallback{
            override fun onSignOut() {
                mAccount = null
                showToastOnSignout()
                Log.d("SIGNOUT", "Yes, this works lets gooooooooooooooooo :-)")
            }
            override fun onError(exception: MsalException) {
                displayError(exception)
            }
            })

    }

    private fun  showToastOnSignout(){
        Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
    }
    private fun displayError(exception: Exception) {
        Log.d(TAG, exception.toString())
    }

    companion object {
        private val TAG = SingleAccountModeFragment::class.java.simpleName
    }
}
