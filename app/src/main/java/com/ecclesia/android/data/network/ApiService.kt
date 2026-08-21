package com.ecclesia.android.data.network

import com.ecclesia.android.domain.models.CambiarContrasenaRequest
import com.ecclesia.android.domain.models.CambiarEstadoRequest
import com.ecclesia.android.domain.models.CambiarEmailRequest
import com.ecclesia.android.domain.models.Certificado
import com.ecclesia.android.domain.models.CertificadoCreateRequest
import com.ecclesia.android.domain.models.Cohorte
import com.ecclesia.android.domain.models.ConfiguracionParroquial
import com.ecclesia.android.domain.models.ConfiguracionUpdateRequest
import com.ecclesia.android.domain.models.CorreoRequest
import com.ecclesia.android.domain.models.Curso
import com.ecclesia.android.domain.models.Evento
import com.ecclesia.android.domain.models.FotoPerfilResponse
import com.ecclesia.android.domain.models.LoginRequest
import com.ecclesia.android.domain.models.LogoutRequest
import com.ecclesia.android.domain.models.MensajeResponse
import com.ecclesia.android.domain.models.MisPermisosResponse
import com.ecclesia.android.domain.models.Notificacion
import com.ecclesia.android.domain.models.PaginatedSolicitudes
import com.ecclesia.android.domain.models.PaginadoUsuarios
import com.ecclesia.android.domain.models.Pago
import com.ecclesia.android.domain.models.PerfilRequest
import com.ecclesia.android.domain.models.Persona
import com.ecclesia.android.domain.models.PersonaRequest
import com.ecclesia.android.domain.models.RegistroSacramental
import com.ecclesia.android.domain.models.SacramentoRegistrado
import com.ecclesia.android.domain.models.RefreshTokenRequest
import com.ecclesia.android.domain.models.RegisterRequest
import com.ecclesia.android.domain.models.RestablecerContrasenaRequest
import com.ecclesia.android.domain.models.Sacramento
import com.ecclesia.android.domain.models.SesionInfo
import com.ecclesia.android.domain.models.Token
import com.ecclesia.android.domain.models.Usuario
import com.ecclesia.android.domain.models.ValidarEmailRequest
import com.ecclesia.android.domain.models.VerificarPerfilResponse
import com.ecclesia.android.domain.models.AuditoriaLog
import com.ecclesia.android.domain.models.AsignarPermisosRequest
import com.ecclesia.android.domain.models.Permiso
import com.ecclesia.android.domain.models.ReemplazarRolRequest
import com.ecclesia.android.domain.models.Rol
import com.ecclesia.android.domain.models.RolRequest
import com.ecclesia.android.domain.models.SacramentoRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Token

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Usuario

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequest): Token

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: CorreoRequest): MensajeResponse

    @POST("auth/reset-password-confirm")
    suspend fun resetPassword(@Body body: RestablecerContrasenaRequest): MensajeResponse

    @POST("auth/reenviar-validacion")
    suspend fun reenviarValidacion(@Body body: CorreoRequest): MensajeResponse

    @POST("auth/validar-email")
    suspend fun validarEmail(@Body body: ValidarEmailRequest): MensajeResponse

    @GET("auth/sesiones")
    suspend fun sesiones(): List<SesionInfo>

    @GET("usuarios/me")
    suspend fun usuarioActual(): Usuario

    @GET("usuarios/me/verificar-perfil")
    suspend fun verificarPerfil(): VerificarPerfilResponse

    @GET("usuarios/me/perfil")
    suspend fun miPerfil(): Persona

    @POST("usuarios/me/perfil")
    suspend fun crearMiPerfil(@Body body: PerfilRequest): Persona

    @PUT("usuarios/me/perfil")
    suspend fun actualizarMiPerfil(@Body body: PerfilRequest): Persona

    @PUT("usuarios/me/email")
    suspend fun cambiarEmail(@Body body: CambiarEmailRequest): MensajeResponse

    @PUT("usuarios/me/password")
    suspend fun cambiarContrasena(@Body body: CambiarContrasenaRequest): MensajeResponse

    @Multipart
    @POST("archivos/foto-perfil")
    suspend fun subirFotoPerfil(@Part file: MultipartBody.Part): FotoPerfilResponse

    @DELETE("archivos/foto-perfil")
    suspend fun eliminarFotoPerfil(): FotoPerfilResponse

    @GET("personas/")
    suspend fun personas(@Query("buscar") buscar: String? = null): List<Persona>

    @GET("personas/{id}")
    suspend fun persona(@Path("id") id: Long): Persona

    @GET("personas/{id}/sacramentos")
    suspend fun sacramentosPersona(@Path("id") id: Long): List<SacramentoRegistrado>

    @POST("personas/")
    suspend fun crearPersona(@Body body: PersonaRequest): Persona

    @PUT("personas/{id}")
    suspend fun actualizarPersona(@Path("id") id: Long, @Body body: PersonaRequest): Persona

    @GET("eventos/")
    suspend fun eventos(@Query("estado") estado: String? = null): List<Evento>

    @GET("eventos/{id}")
    suspend fun evento(@Path("id") id: Long): Evento

    @GET("cursos/")
    suspend fun cursos(): List<Curso>

    @GET("cursos/realizados")
    suspend fun cursosRealizados(): List<Cohorte>

    @GET("cursos/{cursoId}/cohortes")
    suspend fun cohortesCurso(@Path("cursoId") cursoId: Long): List<Cohorte>

    @GET("sacramentos/")
    suspend fun sacramentos(): List<Sacramento>

    @POST("sacramentos/")
    suspend fun crearSacramento(@Body body: SacramentoRequest): Sacramento

    @PUT("sacramentos/{id}")
    suspend fun actualizarSacramento(@Path("id") id: Long, @Body body: SacramentoRequest): Sacramento

    @DELETE("sacramentos/{id}")
    suspend fun eliminarSacramento(@Path("id") id: Long): MensajeResponse

    @GET("solicitudes/mis-solicitudes")
    suspend fun misSolicitudes(
        @Query("pagina") pagina: Int = 1,
        @Query("por_pagina") porPagina: Int = 20,
        @Query("estado") estado: String? = null
    ): PaginatedSolicitudes

    @GET("solicitudes/admin/todas")
    suspend fun todasSolicitudes(
        @Query("pagina") pagina: Int = 1,
        @Query("por_pagina") porPagina: Int = 20,
        @Query("estado") estado: String? = null
    ): PaginatedSolicitudes

    @PATCH("solicitudes/{id}/estado")
    suspend fun cambiarEstadoSolicitud(@Path("id") id: Long, @Body body: CambiarEstadoRequest): MensajeResponse

    @GET("pagos/")
    suspend fun pagos(): List<Pago>

    @GET("certificados/")
    suspend fun certificados(): List<Certificado>

    @GET("certificados/registros")
    suspend fun registrosCertificados(): List<RegistroSacramental>

    @POST("certificados/")
    suspend fun generarCertificado(@Body body: CertificadoCreateRequest): Certificado

    @GET("certificados/{id}/descargar")
    suspend fun descargarCertificado(@Path("id") id: Long): ResponseBody

    @GET("configuracion/")
    suspend fun configuracion(): ConfiguracionParroquial

    @PUT("configuracion/")
    suspend fun actualizarConfiguracion(@Body body: ConfiguracionUpdateRequest): ConfiguracionParroquial

    @GET("auditoria/")
    suspend fun auditoria(@Query("buscar") buscar: String? = null): List<AuditoriaLog>

    @GET("permisos/mis-permisos")
    suspend fun misPermisos(): MisPermisosResponse

    @GET("permisos/")
    suspend fun permisos(): List<Permiso>

    @GET("permisos/rol/{rolId}")
    suspend fun permisosDeRol(@Path("rolId") rolId: Long): List<Permiso>

    @POST("permisos/rol/{rolId}/asignar")
    suspend fun asignarPermisosARol(@Path("rolId") rolId: Long, @Body body: AsignarPermisosRequest): MensajeResponse

    @GET("roles/")
    suspend fun roles(): List<Rol>

    @POST("roles/")
    suspend fun crearRol(@Body body: RolRequest): Rol

    @PUT("roles/{id}")
    suspend fun actualizarRol(@Path("id") id: Long, @Body body: RolRequest): Rol

    @POST("roles/reemplazar-usuario")
    suspend fun reemplazarRolUsuario(@Body body: ReemplazarRolRequest): MensajeResponse

    @GET("usuarios/admin/list")
    suspend fun usuariosAdmin(): PaginadoUsuarios

    @PATCH("usuarios/admin/{id}/estado")
    suspend fun cambiarEstadoUsuario(@Path("id") id: Long, @Query("estado") estado: String): MensajeResponse

    @DELETE("usuarios/admin/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long): MensajeResponse

    @GET("notificaciones/")
    suspend fun notificaciones(): List<Notificacion>

    @PUT("notificaciones/{id}/marcar-leida")
    suspend fun marcarNotificacionLeida(@Path("id") id: Long): MensajeResponse

    @PUT("notificaciones/marcar-todas-leidas")
    suspend fun marcarTodasNotificacionesLeidas(): MensajeResponse

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequest): MensajeResponse
}
