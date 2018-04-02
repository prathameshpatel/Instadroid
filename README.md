Instagram client for Android.

Compile sdk version = 27 | Target sdk version = 19

Instagram API developer scopes = basic+likes

SANDBOX MODE!!!!

Functionality:

1) Fetch recent user media (most recent 15 images uploaded by user)
2) Grid layout for the Feed
3) Photo details view with likes count and elapsed time since creation
4) User can like or unlike photos from Feed and Details views
5) Logout using user avatar in Action bar/Menu icon in Feed view (upper right corner)

---Replace your client_id and redirect_url in Constants.java in main package---

Path to Constants.java = app\src\main\java\io\github\prathameshpatel\instadroid\Constants.java

Libraries used:
1) Retrofit - http://square.github.io/retrofit/ (HTTP client)
2) Glide - https://github.com/bumptech/glide (Image loading)
