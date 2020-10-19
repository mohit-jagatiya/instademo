# instademo

**INTIGRATION STEPS**
===========================

STEP 1:
Create app on Facebook account.
set Keyhash, package & activity.

HOW TO CREATE KEYHASH?
======================
1.1: Using terminal

Debug:
 keytool -exportcert -alias androiddebugkey -keystore debug.keystore | openssl sha1 -binary | openssl base64
 password: android

Release:
 keytool -exportcert -alias <aliasName> -keystore <keystoreFilePath> | openssl sha1 -binary | openssl base64
 password: <keystorepassword>

1.2 : Using code:

<code>
public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }
<code>

Configure Instagram Basic Display
=================================

Click Products, locate the Instagram Basic Display product, and click Set Up to add it to your app.

Add an Instagram Test User
--------------------------

*Navigate to Roles > Roles and scroll down to the Instagram Testers section.Click Add Instagram Testers and enter your Instagram account’s username and send the invitation.

*Open a new web browser and go to www.instagram.com and sign into your Instagram account that you just invited. Navigate to (Profile Icon) > Edit Profile > Apps and Websites > Tester Invites and accept the invitation.

define insta app id and secrate in string file
-----------------------------------------------

    <string name="instagram_app_id">app_id</string>
    <string name="instagram_app_secret">app_secret</string>
    <string name="instagram_redirect_url">https://www.google.com/</string>
    <string name="instagram_redirect_url_with_encode">https%3A%2F%2Fwww.google.com%2F</string>

create a custom web dilog
    private lateinit var customDialogClass: CustomDialogClass

    coll{
         customDialogClass = CustomDialogClass(this, this)
                    customDialogClass.show()
                    customDialogClass.window!!.setLayout(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
         }


# CustomDialogClass


class CustomDialogClass(context: Context, callBackUrlResponseListener: CallBackUrlResponseListener) : Dialog(context) {

    init {
        setCancelable(true)
    }

    private val appID = context.getString(R.string.instagram_app_id)
    private val redirectUrl = context.getString(R.string.instagram_redirect_url_with_encode)

    /**Replace your instagram app id and redirect url*/
    val url = "https://api.instagram.com/oauth/authorize?app_id=$appID&redirect_uri=$redirectUrl&scope=user_profile,user_media&response_type=code"

    val callBackUrlResponse = callBackUrlResponseListener

    //  private val redirectedUrl = "https://www.techumiya.com/?code="
    // Replace with your redirected url
    private val redirectedUrl = "https://www.google.com/?code="
    //Replace with your redirected url

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)
        attachListener()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
    }

    private fun attachListener() {
        /*listener for webView*/
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.d("TAG", "shouldUrl$url")
                if (url.endsWith("#_")) {
                    println("Load URL: $url")
                    view.stopLoading()
                    webView.gone()
                    callBackUrlResponse.gettingResponseCallBack(getAuthCodeFromRedirectedUrl(url))
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

    fun getAuthCodeFromRedirectedUrl(url: String): String {
        return url.replace(redirectedUrl, "").replace("#_", "")
    }

    interface CallBackUrlResponseListener {
        fun gettingResponseCallBack(response: String)
    }
}


*impliment CustomDialogClass.CallBackUrlResponseListener in your activity

make a api Coll with it respons https://api.instagram.com/ param as follow for userId and AccesToken

val appID = getString(R.string.instagram_app_id)
        val appSecret = getString(R.string.instagram_app_secret)
        val grantType = AUTHORIZATION_CODE
        val redirectUrl = getString(R.string.instagram_redirect_url)

viewModel.login(appID, appSecret, grantType, redirectUrl, response)

now coll a api https://graph.instagram.com/ with userId and AccesToken for UserDetails

viewModel.getData(it.data)


    /**This api for get User details from Instagram account*/
    @GET("{user-id}")
    fun getUserDetailsFromInstagram(
        @Path("user-id") userId: String,
        @Query("fields") fields: String,
        @Query("access_token") accessToken: String
    ): Call<InstagramUserDetailsModel>

fields in comma saprated value as "id,username"

with collback display username




