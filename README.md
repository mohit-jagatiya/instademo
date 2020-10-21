# MVVM INSTAGRAM BASIC API DEMO

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

Define Instagram app id and secrate in string file
-----------------------------------------------

    <string name="instagram_app_id">app_id</string>
    <string name="instagram_app_secret">app_secret</string>
    <string name="instagram_redirect_url">https://www.google.com/</string>
    <string name="instagram_redirect_url_with_encode">https%3A%2F%2Fwww.google.com%2F</string>

Create a custom web dialog
-------------------------

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

make a api Coll with it respons https://api.instagram.com/ param as follow for userId and AccesToken

    impliment CustomDialogClass.CallBackUrlResponseListener in your activity

    val appID = getString(R.string.instagram_app_id)
    val appSecret = getString(R.string.instagram_app_secret)
    val grantType = AUTHORIZATION_CODE
    val redirectUrl = getString(R.string.instagram_redirect_url)

    viewModel.login(appID, appSecret, grantType, redirectUrl, response)

now coll a api https://graph.instagram.com/ with userId and AccesToken for UserDetails

    viewModel.getData(it.data)

fields in comma saprated value as "id,username"

with collback display username

get media from instagram use Api

'https://graph.instagram.com/<userId>?fields=id,media_type,media_url,username,timestamp&access_token=IGQVJ...'

Instagram Graph API
-------------------

The Instagram Graph API use with Instagram Businesses and Creators account with it your app can manage their Account,with Api you can get Instagram  comment,hashtag, photo, video, story, or album and Facebook Page. if you intented to need full control over your account like followers_count,follows_count,recently_searched_hashtags User has been @mentioned in Comments then use Instagram Graph API and if you just need basic profile information, photos, and videos you must use Instagram Basic Display API.

how create your Instagram Businesses account

https://www.facebook.com/help/502981923235522

how create your Instagram Creators account

https://www.facebook.com/help/instagram/2358103564437429

with it you can get Insights of user for more https://developers.facebook.com/docs/instagram-api/reference/user

Reference
---------

https://developers.facebook.com/docs/instagram-basic-display-api/reference

https://developers.facebook.com/docs/instagram-api/reference



