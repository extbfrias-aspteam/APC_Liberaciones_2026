using iRisModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;

namespace iRisCoreApis.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    [ApiController]
    public class LoginController : ControllerBase
    {
        private readonly IConfiguration _config;
        public LoginController(IConfiguration config) {
            _config = config;
        }

        [AllowAnonymous]
        [HttpPost]
        public IActionResult Login([FromBody] LoginModel login)
        {
            if (login.UserName == "VITAL" && login.Password == "1234") {
                var token = GenerateToken(login.UserName);

                ResponseLoginModel TokenModel = new ResponseLoginModel { Codigo = 0, Mensaje = "Successfull", Token = token };
                return Ok(TokenModel);
            } 
            
            return BadRequest("Login Failed");
        }

        private string GenerateToken(string UserName)
        {
            var securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config.GetSection("Jwt:secretkey").Value));
            var credentials = new SigningCredentials(securityKey,SecurityAlgorithms.HmacSha256);

            var claims = new[]
            {
               new Claim(ClaimTypes.NameIdentifier,UserName)
            };

            var token = new JwtSecurityToken(
                            _config.GetSection("Jwt:Issuer").Value, _config.GetSection("Jwt:Audience").Value,
                            claims,
                            expires: DateTime.UtcNow.AddSeconds(20),
                            signingCredentials: credentials
                            );
        
            return new JwtSecurityTokenHandler().WriteToken(token);
        }

    }
}
