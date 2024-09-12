package ar.com.unq.eis.trainup.controller.dto

import ar.com.unq.eis.trainup.model.Usuario
import java.time.LocalDate

class UsuarioDTO(
    var id: String? = null,
    var username: String = "",
    var password: String = "",
    var rutinasSeguidas: List<RutinaDTO> = mutableListOf(),
    var nombre: String = "",
    var edad: Int? = null,
    var fecNacimiento: LocalDate? = null,
    var telefono: String = "",
    var genero: String = "",
    var altura: String = "",
    var peso: String = "",
    var objetivo: String = ""
) {


    fun aModelo(): Usuario {
        return Usuario(username, password, nombre, edad!!, fecNacimiento!!, telefono, genero, altura, peso, objetivo)
    }

    companion object {
        fun desdeModelo(usuario: Usuario): UsuarioDTO {
            return UsuarioDTO(usuario.id,
                usuario.username,
                usuario.password,
                usuario.rutinasSeguidas.map { rutina -> RutinaDTO.desdeModelo(rutina) },
                usuario.nombre,
                usuario.edad,
                usuario.fecNacimiento,
                usuario.telefono,
                usuario.genero,
                usuario.altura,
                usuario.peso,
                usuario.objetivo
                )
        }
    }

}