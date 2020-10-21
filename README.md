# instademo

**INTIGRATION STEPS**
----------------------

STEP 1:
Create app on Facebook account.
set Keyhash, package & activity.


Configure Instagram Basic Display
---------------------------------

Click Products, locate the Instagram Basic Display product, and click Set Up to add it to your app.

Add an Instagram Test User
--------------------------

Navigate to Roles > Roles and scroll down to the Instagram Testers section.Click Add Instagram Testers and enter your Instagram account’s username and send the invitation.

Open a new web browser and go to www.instagram.com and sign into your Instagram account that you just invited. Navigate to (Profile Icon) > Edit Profile > Apps and Websites > Tester Invites and accept the invitation.

define insta app id and secrate in string file
-----------------------------------------------

    <string name="instagram_app_id">app_id</string>
    <string name="instagram_app_secret">app_secret</string>
    <string name="instagram_redirect_url">https://www.google.com/</string>
    <string name="instagram_redirect_url_with_encode">https%3A%2F%2Fwww.google.com%2F</string>

*create a custom web dilog

    private lateinit var customDialogClass: CustomDialogClass
    customDialogClass = CustomDialogClass(this, this)
    customDialogClass.show()
    customDialogClass.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)

CustomDialogClass
-----------------

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

Configure Instagram Basic Display

Click Products, locate the Instagram Basic Display product, and click Set Up to add it to your app.

    impliment CustomDialogClass.CallBackUrlResponseListener in your activity

make a api Coll with it respons https://api.instagram.com/ param as follow for userId and AccesToken

    val appID = getString(R.string.instagram_app_id)
    val appSecret = getString(R.string.instagram_app_secret)
    val grantType = AUTHORIZATION_CODE
    val redirectUrl = getString(R.string.instagram_redirect_url)

    viewModel.login(appID, appSecret, grantType, redirectUrl, response)

now coll a api https://graph.instagram.com/ with userId and AccesToken for UserDetails

    viewModel.getData(it.data)

fields in comma saprated value as "id,username"

with collback display username

get media from instagram   'https://graph.instagram.com/17895695668004550?fields=id,media_type,media_url,username,timestamp&access_token=IGQVJ...'

#Instagram Graph API

The Instagram Graph API allows Instagram Professionals — Businesses and Creators — to use your app to manage their presence on Instagram. The API can be used to get their media, manage and reply to comments on their media, identify media where they have been @mentioned by other Instagram users, find hashtagged media, and get basic metadata and metrics about other Instagram Businesses and Creators.

The API is intended for Instagram Businesses and Creators who need insight into, and full control over, all of their social media interactions. If you are building an app for consumers or you only need to get an app user's basic profile information, photos, and videos, consider the Instagram Basic Display API instead.






Reference

https://developers.facebook.com/docs/instagram-basic-display-api/reference




