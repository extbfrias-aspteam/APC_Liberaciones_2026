using iRisCore.Filters;
using iRisCore.Access;
using iRisCore.Services;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Http.Features;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllersWithViews(options =>
{
    options.Filters.Add<ConfiguracionSesionFilter>();
});
builder.Services.AddRazorPages().AddRazorRuntimeCompilation();
builder.Services.AddHttpContextAccessor();
builder.Services.AddScoped<TokenService>();
builder.Services.AddScoped<Permissions>();

builder.Services.AddSession(options =>
{
    var tiempoInactividadMinutos = builder.Configuration.GetValue<int>("Miscelanius:tiempoInactividadMinutos");
    options.IdleTimeout = TimeSpan.FromMinutes(tiempoInactividadMinutos);
    options.Cookie.HttpOnly = true;
    options.Cookie.IsEssential = true;
});

builder.Services.AddAuthentication(CookieAuthenticationDefaults.AuthenticationScheme).AddCookie(options =>
{
    var tiempoInactividadMinutos = builder.Configuration.GetValue<int>("Miscelanius:tiempoInactividadMinutos");
    options.LoginPath = "/Account/Login";
    options.ExpireTimeSpan = TimeSpan.FromMinutes(tiempoInactividadMinutos);
    options.SlidingExpiration = true;
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Home/Error");
}
app.UseStaticFiles();

app.UseRouting();

app.UseAuthentication();
app.UseAuthorization();

app.UseSession();

app.Use(async (context, next) =>
{
    Thread.CurrentPrincipal = context.User;

    var path = context.Request.Path.ToString().ToLower();

    var rutasExcluidas = new[] { "/", "/account/login", "/home/salir" };

    var esRutaExcluida = rutasExcluidas.Any(r => path.Equals(r));

    if (!esRutaExcluida)
    {
        if (!context.User.Identity.IsAuthenticated)
        {
            context.Response.Redirect("/account/login");
            return;
        }
    }

    await next(context);
});

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Home}/{action=Index}/{id?}");

app.Run();

