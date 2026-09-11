using RestSharp;
using RestSharp.Authenticators;
using System.Net.NetworkInformation;
using System.Security.Policy;

namespace iRisCore.Access
{
    public class GlobalStatic
    {
        public static RestClient hostClient = new RestClient();
        public GlobalStatic() {
            var configuration = new ConfigurationBuilder()
                .AddJsonFile("appsettings.json")
                .Build();

            string urlHost = configuration.GetValue<string>("EndPointsCore:apiURLCard");
            
            string userAccess = configuration.GetValue<string>("EndPointsCore:userASP");
            string passAccess = configuration.GetValue<string>("EndPointsCore:passASP");

            var options = new RestClientOptions("http://172.17.7.183:8080")
            {
                   Authenticator = new HttpBasicAuthenticator(userAccess, passAccess)
            };
            hostClient = new RestClient(options);
            
            
        }
    }
}
