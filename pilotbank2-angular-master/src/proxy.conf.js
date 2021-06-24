const PROXY_CONFIG = [
  {
    context: [
      "/accounts",
      "/users",
      "/registration",
      "/login",
      "/transactions",
      "/transfer"
    ],
    target: "http://localhost:8082",
    secure: false,
    bypass: function (req, res, proxyOptions) {
      var endpt = req.url.split("/").pop()
      var endptsForGET = ["accounts", "transactions"];
      var endptsForPOST = ["registration", "login", "users"];
      if (req.method == "GET") {
        if (endptsForGET.indexOf(endpt) == -1) {
          return "/" + endpt;
        }
      } else if (req.method == "POST") {
        if (endptsForPOST.indexOf(endpt) == -1) {
          return "/" + endpt;
        }
      }
    }
  }
]

module.exports = PROXY_CONFIG;
