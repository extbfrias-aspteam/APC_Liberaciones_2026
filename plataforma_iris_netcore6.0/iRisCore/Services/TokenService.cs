using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication;
using Newtonsoft.Json;
using RestSharp;
using iRisModel;
using iRisLog;
using static Org.BouncyCastle.Math.EC.ECCurve;

namespace iRisCore.Services
{
    public class TokenService
    {
        private readonly IHttpContextAccessor _httpContextAccessor;
        private IConfiguration _configuration;

        public TokenService(IHttpContextAccessor httpContextAccessor, IConfiguration iConfig)
        {
            _httpContextAccessor = httpContextAccessor;
            _configuration = iConfig;
        }

        // Método para enviar una solicitud HTTP que incluye el token
        public async Task<RestResponse> SendRequestWithToken(string userName, string apiUrl, Method method, object body = null)
        {
            // Obtener el token de acceso de la sesión
            var token = _httpContextAccessor.HttpContext.Session.GetString("AccessToken");
            var refreshToken = _httpContextAccessor.HttpContext.Session.GetString("RefreshToken");
            var plataforma = _httpContextAccessor.HttpContext.Session.GetString("Platform");
            var idUsuario = _httpContextAccessor.HttpContext.Session.GetString("IdUser");
            var numempleado = _httpContextAccessor.HttpContext.Session.GetString("NumEmploy");
            var usuario = _httpContextAccessor.HttpContext.Session.GetString("UserName");

            var client = new RestClient(apiUrl);

            // Crear la solicitud con el token de acceso en el encabezado Authorization
            var request = new RestRequest(apiUrl, method);
            if (!string.IsNullOrEmpty(token))
            {
                request.AddHeader("Authorization", $"Bearer {token}");
            }

            // Si hay un cuerpo en la petición, agrégalo
            if (body != null)
            {
                request.AddJsonBody(body);
            }

            // Enviar la solicitud
            var response = await client.ExecuteAsync(request);

            // Si la respuesta es 401 (token expirado), intentar renovar el token
            if (response.StatusCode == System.Net.HttpStatusCode.Unauthorized)
            {
                try
                {
                    RefreshTokenRequest renewTokenBody = new RefreshTokenRequest()
                    {
                        token = token,
                        refreshToken = refreshToken,
                        user = new UserToken()
                        {
                            idAplicativo = plataforma,
                            idUsuario = idUsuario,
                            numEmpleado = numempleado,
                            usuario = usuario
                        }
                    };
                    var refreshSuccess = await RenewToken(renewTokenBody);

                    if (refreshSuccess)
                    {
                        // Obtener el nuevo token de la sesión y reintentar la solicitud
                        token = _httpContextAccessor.HttpContext.Session.GetString("AccessToken");
                        request.AddOrUpdateHeader("Authorization", $"Bearer {token}");
                        response = await client.ExecuteAsync(request);
                    }
                    else
                    {
                        // Si no se puede renovar el token, cerrar sesión y borrar cookies
                        await HandleTokenExpiration();
                    }
                }
                catch (Exception e)
                {
                    await HandleTokenExpiration();
                }
            }

            if (userName.Length > 0)
            {
                var requestToLog = new
                {
                    resource = request.Resource,
                    parameters = request.Parameters.Select(parameter => new
                    {
                        name = parameter.Name,
                        value = parameter.Value,
                        type = parameter.Type.ToString()
                    }),
                    method = request.Method.ToString(),
                    uri = client.BuildUri(request),
                };

                logiRis.Add("Usuario:" + userName + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog), "SEG");
                logiRis.Add("Usuario:" + userName + "-Response:" + request.Resource + response.Content, "SEG");
            }

            return response;
        }


        // Método para enviar una solicitud HTTP que incluye el token pero se incluye la variable firma que ocupa el WH para los envios
        public async Task<RestResponse> SendRequestWithTokenFirma(string firma, string userName, string apiUrl, Method method, object body = null)
        {
            // Obtener el token de acceso de la sesión
            var token = _httpContextAccessor.HttpContext.Session.GetString("AccessToken");
            var client = new RestClient(apiUrl);

            // Crear la solicitud con el token de acceso en el encabezado Authorization
            var request = new RestRequest(apiUrl, method);
            if (!string.IsNullOrEmpty(token))
            {
                request.AddHeader("Authorization", $"Bearer {token}");
                request.AddHeader("signature", firma);
            }

            // Si hay un cuerpo en la petición, agrégalo
            if (body != null)
            {
                request.AddJsonBody(body);
            }

            // Enviar la solicitud
            var response = await client.ExecuteAsync(request);

            // Si la respuesta es 401 (token expirado), intentar renovar el token
            if (response.StatusCode == System.Net.HttpStatusCode.Unauthorized)
            {
                var refreshToken = _httpContextAccessor.HttpContext.Session.GetString("RefreshToken");
                var plataforma = _httpContextAccessor.HttpContext.Session.GetString("Platform");
                var idUsuario = _httpContextAccessor.HttpContext.Session.GetString("IdUser");
                var numempleado = _httpContextAccessor.HttpContext.Session.GetString("NumEmploy");
                var usuario = _httpContextAccessor.HttpContext.Session.GetString("UserName");

                RefreshTokenRequest renewTokenBody = new RefreshTokenRequest()
                {
                    token = token,
                    refreshToken = refreshToken,
                    user = new UserToken()
                    {
                        idAplicativo = plataforma,
                        idUsuario = idUsuario,
                        numEmpleado = numempleado,
                        usuario = usuario
                    }
                };
                var refreshSuccess = await RenewToken(renewTokenBody);

                if (refreshSuccess)
                {
                    // Obtener el nuevo token de la sesión y reintentar la solicitud
                    token = _httpContextAccessor.HttpContext.Session.GetString("AccessToken");
                    request.AddOrUpdateHeader("Authorization", $"Bearer {token}");
                    response = await client.ExecuteAsync(request);
                }
                else
                {
                    // Si no se puede renovar el token, cerrar sesión y borrar cookies
                    await HandleTokenExpiration();
                }
            }

            if (userName.Length > 0)
            {
                var requestToLog = new
                {
                    resource = request.Resource,
                    parameters = request.Parameters.Select(parameter => new
                    {
                        name = parameter.Name,
                        value = parameter.Value,
                        type = parameter.Type.ToString()
                    }),
                    method = request.Method.ToString(),
                    uri = client.BuildUri(request),
                };

                logiRis.Add("Usuario:" + userName + "-Request:" + request.Resource + JsonConvert.SerializeObject(requestToLog), "SEG");
                logiRis.Add("Usuario:" + userName + "-Response:" + request.Resource + response.Content, "SEG");
            }

            return response;
        }

        // Método para renovar el token usando el refresh token
        public async Task<bool> RenewToken(RefreshTokenRequest body)
        {
            var apiUrl = _configuration.GetValue<string>("EndPointsCore:apiURLRefreshToken");
            var client = new RestClient(apiUrl);
            var request = new RestRequest(apiUrl, Method.Post);
            request.AddJsonBody(body);

            // Añadir el refresh token en el encabezado Authorization
            request.AddHeader("Authorization", $"Bearer {body.refreshToken}");

            var response = await client.ExecuteAsync(request);

            if (response.IsSuccessful)
            {
                var tokenResponse = JsonConvert.DeserializeObject<TokenResponse>(response.Content);

                // Actualizar el token y el refresh token en la sesión
                _httpContextAccessor.HttpContext.Session.SetString("AccessToken", tokenResponse.data.token);
                //_httpContextAccessor.HttpContext.Session.SetString("RefreshToken", tokenResponse.data.refreshToken);

                return true;
            }

            return false;
        }

        // Método para cerrar la sesión y eliminar cookies cuando el token no se puede renovar
        private async Task HandleTokenExpiration()
        {
            if (_httpContextAccessor.HttpContext == null)
            {
                throw new UnauthorizedAccessException("Redirigir a login");
            }
            try
            {
                // Cerrar la sesión de autenticación
                await _httpContextAccessor.HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);

                // Eliminar todas las cookies
                foreach (var cookieKey in _httpContextAccessor.HttpContext.Request.Cookies.Keys)
                {
                    _httpContextAccessor.HttpContext.Response.Cookies.Delete(cookieKey);
                }

                // Redirigir al usuario a la página de login
                _httpContextAccessor.HttpContext.Response.Redirect("/Account/Login");
            }
            catch (Exception e)
            {
                throw new UnauthorizedAccessException("Redirigir a login");
            }
        }
    }
}
