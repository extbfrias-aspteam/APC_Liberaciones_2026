package main

import (
	"condusef-api/app/controllers"
	"condusef-api/app/database"
	"condusef-api/app/services"
	"condusef-api/app/utils"

	"net/http"
	"os"
	"os/signal"
	"syscall"

	"github.com/golang-jwt/jwt/v5"
	"github.com/joho/godotenv"
	echojwt "github.com/labstack/echo-jwt/v4"
	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
)

func main() {

	// load environment variables
	err := godotenv.Load()
	if err != nil {
		utils.NoticeMessage("Environment file load error " + err.Error())
	}

	// database connection
	err = database.OpenConnection()
	if err != nil {
		utils.ErrorMessage("Database connection error", err.Error())
		os.Exit(-1)
	}

	// google storage connection
	err = services.InitGoogleStorage()
	if err != nil {
		utils.WarningMessage("Google storage connection error", err.Error())
	}

	// localstorage connection
	err = services.InitLocalstorage()
	if err != nil {
		utils.WarningMessage("Localstorage open connection error", err.Error())
	}
	defer func() {
		services.L_STORAGE.Dispose()
	}()

	// api configuration
	e := echo.New()

	e.Debug = true

	e.Use(middleware.Logger())
	e.Use(middleware.Recover())
	e.Use(middleware.CORSWithConfig(middleware.CORSConfig{
		AllowOrigins: []string{"*"},
		AllowMethods: []string{echo.GET, echo.HEAD, echo.PUT, echo.PATCH, echo.POST, echo.DELETE},
		AllowHeaders: []string{echo.HeaderOrigin, echo.HeaderContentType, echo.HeaderAccept, echo.HeaderAuthorization, echo.HeaderXRequestedWith},
	}))

	// web aplication in root
	e.Use(middleware.StaticWithConfig(middleware.StaticConfig{
		Root:   "public/html",
		Index:  "index.html",
		Browse: false,
		HTML5:  true,
	}))

	// public group configuration
	p1 := e.Group("/api/v1")

	p1.POST("/condusef/login", controllers.Autenticar)
	p1.GET("/condusef/login/empresas", controllers.ObtenerEmpresas)
	p1.POST("/condusef/logout", controllers.CerrarSesion)

	// version group configuration
	v1 := e.Group("/api/v1")

	JWTSecret := os.Getenv("JWT_SECRET")
	config := echojwt.Config{
		NewClaimsFunc: func(c echo.Context) jwt.Claims {
			return new(services.CustomClaims)
		},
		SigningKey: []byte(JWTSecret),
	}
	v1.Use(echojwt.WithConfig(config))

	v1.POST("/condusef/portal/token", controllers.ObtenerTokenCONDUSEF)
	v1.POST("/condusef/portal/token/asignar", controllers.AsignarTokenCondusef)
	v1.GET("/condusef/catalogos/sepomex/entidades", controllers.ObtenerEntidadesFed)
	v1.GET("/condusef/catalogos/sepomex/municipios", controllers.ObtenerMunicipios)
	v1.GET("/condusef/catalogos/sepomex/colonias", controllers.ObtenerColonias)
	v1.GET("/condusef/catalogos/sepomex/codigos-postales", controllers.ObtenerCodigosPostales)
	v1.GET("/condusef/catalogos/medios-recepcion", controllers.ObtenerMediosRecepcion)
	v1.GET("/condusef/catalogos/niveles-atencion", controllers.ObtenerNivelesAtencion)
	v1.GET("/condusef/catalogos/productos", controllers.ObtenerProductos)
	v1.GET("/condusef/catalogos/causas", controllers.ObtenerCausas)
	v1.GET("/condusef/catalogos/folios", controllers.ObtenerNumFolio)
	v1.GET("/condusef/periodos", controllers.ObtenerPeriodos)
	v1.GET("/condusef/periodos/activo", controllers.ObtenerPeriodoActivo)
	v1.POST("/condusef/periodos", controllers.CrearPeriodo)
	v1.PUT("/condusef/periodos/cerrar", controllers.CerrarPeriodo)
	v1.PUT("/condusef/periodos/abrir", controllers.AbrirPeriodo)
	v1.GET("/condusef/quejas/paginado", controllers.ObtenerQuejasPaginado)
	v1.GET("/condusef/quejas/sin-reportar", controllers.ObtenerQuejasSinReportar)
	v1.POST("/condusef/quejas/reportar", controllers.ReportarQuejasCondusef)
	v1.DELETE("/condusef/quejas/eliminar", controllers.EliminarQuejaCondusef)
	v1.PUT("/condusef/quejas/reportar/cambiar-estado", controllers.ModificarEstadoReportadoPorFolio)
	v1.POST("/condusef/quejas", controllers.CrearQueja)
	v1.PUT("/condusef/quejas", controllers.ModificarQueja)
	v1.DELETE("/condusef/quejas/:folio", controllers.EliminarQueja)
	v1.GET("/condusef/quejas/:folio/evidencias", controllers.ObtenerEvidenciasPorFolio)
	v1.GET("/condusef/quejas/:folio/evidencias/download", controllers.DescargarEvidencia)
	v1.POST("/condusef/quejas/:folio/evidencias/upload", controllers.SubirEvidencia)
	v1.GET("/condusef/quejas/reportadas", controllers.ObtenerQuejasCONDUSEF)
	v1.POST("/condusef/consultas/crear", controllers.CrearConsulta)
	v1.POST("/condusef/consultas/editar", controllers.ModificarConsulta)
	v1.DELETE("/condusef/consultas/:folio", controllers.EliminarConsulta)
	v1.GET("/condusef/consultas", controllers.ObtenerConsultaPorFolio)
	v1.POST("/condusef/aclaraciones/crear", controllers.CrearAclaracion)
	v1.POST("/condusef/aclaraciones/editar", controllers.ModificarAclaracion)
	v1.DELETE("/condusef/aclaraciones/:folio", controllers.EliminarAclaracion)
	v1.GET("/condusef/aclaraciones", controllers.ObtenerAclaracionPorFolio)
	v1.POST("/condusef/reclamaciones/crear", controllers.CrearReclamacion)
	v1.POST("/condusef/reclamaciones/editar", controllers.ModificarReclamacion)
	v1.DELETE("/condusef/reclamaciones/:folio", controllers.EliminarReclamacion)
	v1.GET("/condusef/reclamaciones", controllers.ObtenerReclamacionPorFolio)
	v1.GET("/condusef/reune/paginado", controllers.ObtenerPaginadoREUNE)
	v1.GET("/condusef/reune/sin-reportar", controllers.ObtenerRegistrosREUNESinReportar)
	v1.POST("/condusef/reune/reportar", controllers.ReportarREUNE)
	// chanel to receive system signals
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, os.Interrupt, syscall.SIGTERM)

	go func() {
		if err := e.Start(":8888"); err != nil && err != http.ErrServerClosed {
			e.Logger.Fatal("fatal error, shutting down the server", err.Error())
		}
	}()

	<-quit

	utils.NoticeMessage("Shutting down the server by interrupt signal")
}
